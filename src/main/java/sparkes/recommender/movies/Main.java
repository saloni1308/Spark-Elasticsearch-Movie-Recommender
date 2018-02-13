package sparkes.recommender.movies;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.*;
import org.apache.spark.sql.Row;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.json.simple.JSONObject;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

public class Main implements Serializable {
    private final static String RESOURCES_PATH = "C:\\Users\\RajatKhandelwal\\IdeaProjects\\sparkes.recommender.movies\\resources";
    static SparkConf conf;
    static JavaSparkContext jsc;
    static SparkSession sparkSession;
    static SparkContext sc;
    private final String DATA_SET_PATH = "C:\\Users\\RajatKhandelwal\\IdeaProjects\\sparkes.recommender.movies\\src\\datasets\\ml-latest-small";
    ALS als;
    MatrixFactorizationModel model;
    Dataset<Row> movies;
    Dataset<MovieRating> ratings;
    Dataset<Row> tags;
    Dataset<Links> links;
    Dataset<Row> movies_filter;
    Dataset<MovieRating> ratings_filter;
    Dataset<Movie> movieJoin;
    JavaRDD<AlsModel> features_vector;
    JavaRDD<AlsModel> user_features;

    public static void main(String arg[]) {
        // initialize spak session
        Main driver = new Main();
        driver.initializeSession();
        driver.importData2Spark();

        // create mapping
        driver.createIndex("i_movies", "movies_mapping.json");
        driver.createIndex("i_rating", "rating_mapping.json");
        driver.createIndex("i_user", "users_mapping.json");


        // remove null values
        driver.dataPreProcessing();

        // prepare data for Als algorithm
        JavaRDD<Rating> als_data = driver.createAlsData();

        // train model using ALS algorithm
        driver.trainModelByAls(als_data);

        // fetch item features from the model
        JavaRDD<Tuple2<Integer, double[]>> factors_ratings = (JavaRDD<Tuple2<Integer, double[]>>) (JavaRDD<?>) driver.model.productFeatures().toJavaRDD();

        JavaRDD<Tuple2<Integer, double[]>> users_feature = (JavaRDD<Tuple2<Integer, double[]>>) (JavaRDD<?>) driver.model.userFeatures().toJavaRDD();

        // transform item feature to (id | feature | version | timestamp) format
        driver.features_vector = factors_ratings.map(objectTuple2 -> driver.getModelVector(objectTuple2, driver.model.formatVersion()));

        driver.user_features = users_feature.map(objectTuple -> driver.getModelVector(objectTuple, driver.model.formatVersion()));

        // combine movie data with link and features
        driver.transformMovieData();

        // save data to ES
        driver.save2ES("i_movies", "movies", driver.movieJoin.toJavaRDD());
        driver.save2ES("i_rating", "ratings", driver.ratings_filter.toJavaRDD());
        driver.save2ES("i_user", "users", driver.user_features);

        sparkSession.close();
    }

    public void initializeSession() {
        conf = new SparkConf().setAppName("Movie Recommender").setMaster("local");
        sc = new SparkContext(conf);
        jsc = JavaSparkContext.fromSparkContext(sc);
        sparkSession = new SparkSession(sc);
    }

    private void importData2Spark() {
        movies = sparkSession.read().option("header", Boolean.TRUE)
                .csv(DATA_SET_PATH + "movies.csv");

        Encoder<sparkes.recommender.movies.MovieRating> ratingsEncoder = Encoders.bean(sparkes.recommender.movies.MovieRating.class);
        ratings = sparkSession.read().option("header", Boolean.TRUE)
                .csv(DATA_SET_PATH + "ratings.csv")
                .as(ratingsEncoder);

        tags = sparkSession.read().option("header", Boolean.TRUE)
                .csv(DATA_SET_PATH + "tags.csv");

        Encoder<Links> linksEncoder = Encoders.bean(Links.class);
        links = sparkSession.read().option("header", Boolean.TRUE)
                .csv(DATA_SET_PATH + "links.csv")
                .as(linksEncoder);
    }

    private void dataPreProcessing() {
        movies_filter = movies.filter(m -> !m.anyNull());
        ratings_filter = ratings.filter(r -> !r.anyNull());
        Dataset<Row> tags_filter = tags.filter(r -> !r.anyNull());
        Dataset<Links> links_filter = links.filter(r -> !r.anyNull());
    }

    private void transformMovieData() {
        Encoder<Movie> movieEncoder = Encoders.bean(Movie.class);
        Encoder<AlsModel> featureEncoder = Encoders.bean(AlsModel.class);
        Dataset<AlsModel> featureData = sparkSession.createDataset(features_vector.rdd(), featureEncoder);
        movieJoin = movies_filter.join(links, "movieId").join(featureData, "movieId").as(movieEncoder);
    }

    private void createIndex(String indexName, String indexMappingJson) {
        String indexMapping = readJson(indexMappingJson);
        if (indexMapping != null) {

            RestClient client = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();

            HttpEntity entity = new NStringEntity(indexMapping, ContentType.APPLICATION_JSON);

            Response response = createIndexES(client, indexName, entity);

            closeClientConnection(client);

        }
    }

    private String readJson(String fileName) {
        try {
            String mapping = new String(Files.readAllBytes(Paths.get(RESOURCES_PATH + fileName)));
            return mapping;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response createIndexES(RestClient client, String indexName, HttpEntity entity) {
        try {
            return client.performRequest("PUT", indexName, Collections.EMPTY_MAP, entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void closeClientConnection(RestClient client) {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private <T> void save2ES(final String indexName, final String documentType, final JavaRDD<T> data) {
        // inject data into elasticsearch
        JavaEsSpark.saveToEs(data, "/" + indexName + "/" + documentType);

    }

    private JavaRDD<Rating> createAlsData() {
        return ratings_filter.toJavaRDD().map(r -> {
            return new Rating(Integer.parseInt(r.getUserId()), Integer.parseInt(r.getMovieId()), Float.parseFloat(r.getRating()));
        });
    }

    private void trainModelByAls(JavaRDD<Rating> als_data) {
        als = new ALS();
        als.setLambda(0.1);
        als.setAlpha(0.1);
        als.setIterations(10);
        als.setRank(10);
        als.setSeed(42);
        model = als.run(als_data);
    }

    private AlsModel getModelVector(Tuple2<Integer, double[]> model, String version) {
        AlsModel alsModel = new AlsModel();
        alsModel.setVersion(version);
        alsModel.setTimestamp(Instant.now().getEpochSecond());
        alsModel.setMovieId(model._1().toString());
        alsModel.setFeatures(Arrays.toString(model._2()).replace(",", "|").replace("[", "").replace("]", ""));
        return alsModel;
    }

    private String mergeJson(JSONObject a, JSONObject b) {

        return "";
    }

    private String func_query(String q) {

        JSONObject query_json = new JSONObject();


        JSONObject function_score_json = new JSONObject();


        JSONObject sub_query_json = new JSONObject();
        JSONObject sub_query_string_json = new JSONObject();

        JSONObject script_score_json = new JSONObject();

        JSONObject script_json = new JSONObject();

        sub_query_string_json.put("query_string", new JSONObject().put("query", q));
        sub_query_json.put("query", sub_query_string_json);
        function_score_json.put("function_score", sub_query_json);
        query_json.put("query", function_score_json);


        System.out.println(query_json.toJSONString());


        JSONObject boostmode_json = new JSONObject();
        boostmode_json.put("boost_mode", "replace");

        //	query_json.put("query", new JSONObject("function_score", new JSONObject("query", new JSONObject("query_string", new JSONObject("query",q )))));

        return "";
    }


}
