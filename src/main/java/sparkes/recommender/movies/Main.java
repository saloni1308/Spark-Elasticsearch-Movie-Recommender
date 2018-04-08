package sparkes.recommender.movies;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.*;
import org.apache.spark.sql.Row;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import scala.Tuple2;
import sparkes.recommender.movies.AwsConnection.AwsMain;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class Main implements Serializable {
    private final static String RESOURCES_PATH = ".\\resources\\";
    static SparkConf conf;
    static JavaSparkContext jsc;
    static SparkSession sparkSession;
    static SparkContext sc;
    static RestClient client;
    private final String DATA_SET_PATH = ".\\resources\\datasets\\ml-latest-small\\";


    Dataset<Row> movies;
    Dataset<MovieRating> ratings;
    Dataset<Row> tags;
    Dataset<Links> links;
    Dataset<Row> movies_filter;
    Dataset<MovieRating> ratings_filter;
    Dataset<Movie> movieJoin;
    JavaRDD<AlsModel> features_vector;
    JavaRDD<AlsModel> user_features;


    public void initializeSession() {
        //spark
        conf = new SparkConf().setAppName("Movie Recommender").setMaster("local[*]");
        sc = new SparkContext(conf);
        jsc = JavaSparkContext.fromSparkContext(sc);
        sparkSession = new SparkSession(sc);

        //elasticsearch
        client = AwsMain.esClient("es", "ap-south-1");
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


            HttpEntity entity = new NStringEntity(indexMapping, ContentType.APPLICATION_JSON);

            Response response = createIndexES(client, "/"+ indexName, entity);


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

    private void closeESClientConnection(RestClient client) {
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

    private JavaRDD<Map<String, Object>> getMovieFromES(final int movieId, final String ESindexName, final String ESindexmapping) throws IOException {
        // fetching data from elasticsearch
        //  final JavaPairRDD movie = JavaEsSpark.esRDD(jsc, indexName+"/"+mapping+"/?q=movieId="+movieId);
        final JavaRDD<Map<String, Object>> movie = JavaEsSpark.esRDD(jsc, "/" + ESindexName + "/" + ESindexmapping, "?q=movieId=" + movieId).values();

        //  Response response = client.performRequest("GET", indexName, );

        return movie;
    }

    private static void initialSetup(Main driver, Recommender recommender_system) {
        driver.importData2Spark();

        // create mapping
        driver.createIndex("i_movies", "movies_mapping.json");
        driver.createIndex("i_rating", "rating_mapping.json");
        driver.createIndex("i_user", "users_mapping.json");


        // remove null values
        driver.dataPreProcessing();


        // prepare data for Als algorithm
        JavaRDD<Rating> als_data = recommender_system.createAlsData(driver.ratings_filter);

        // train model using ALS algorithm
        recommender_system.trainModelByAls(als_data);

        // fetch item factor from the model
        JavaRDD<Tuple2<Integer, double[]>> factors_ratings = (JavaRDD<Tuple2<Integer, double[]>>) (JavaRDD<?>) recommender_system.model.productFeatures().toJavaRDD();

        JavaRDD<Tuple2<Integer, double[]>> users_feature = (JavaRDD<Tuple2<Integer, double[]>>) (JavaRDD<?>) recommender_system.model.userFeatures().toJavaRDD();

        // transform item feature to (id | feature | version | timestamp) format
        driver.features_vector = factors_ratings.map(objectTuple2 -> recommender_system.getModelVector(objectTuple2, recommender_system.model.formatVersion()));

        driver.user_features = users_feature.map(objectTuple -> recommender_system.getModelVector(objectTuple, recommender_system.model.formatVersion()));

        // combine movie data with link and factor
        driver.transformMovieData();

        // save data to ES
        driver.save2ES("i_movies", "movies", driver.movieJoin.toJavaRDD());
        driver.save2ES("i_rating", "ratings", driver.ratings_filter.toJavaRDD());
        driver.save2ES("i_user", "users", driver.user_features);
    }

    public void getMoviesSimilarToGivenMovie(int movieId, int recommedationCount, String index) {

    }

    public static void main(String arg[]) throws IOException {
        // initialize spark session
        Main driver = new Main();
        final Recommender recommender_system = new Recommender();
        driver.initializeSession();

        final boolean isFirstRun = true;
        if (isFirstRun) {
            Main.initialSetup(driver, recommender_system);
        }


        // get similar movies
        final int movieId = 2628;
        final int number_of_recommendation = 10;
        final String es_index = "i_movies";
        final String es_index_mapping = "movies";


        JavaRDD<Map<String, Object>> movie_query = driver.getMovieFromES(movieId, es_index, es_index_mapping);
        boolean factorsAvailable = movie_query.first().containsKey("factor");
        System.out.println(movie_query.first().keySet());
        if (!factorsAvailable) {
            System.out.println("Cannot find similar movies..");
        } else {
            StringBuilder feature = new StringBuilder(movie_query.first().get("factor").toString());
            String query_vec = feature.toString();
            sparkes.recommender.movies.QueryBuilder queryBuilder = new sparkes.recommender.movies.QueryBuilder();
            String query = queryBuilder.getMovieRecommenderQuery("*", query_vec, true);
            JavaRDD<Map<String, Object>> similarQueryResult = JavaEsSpark.esRDD(jsc,  "i_movies/movies" , query).values();
            similarQueryResult.collect().forEach(results -> {
                System.out.println(results.toString());
            });
            //driver.getMoviesSimilarToGivenMovie(movieId, number_of_recommendation, es_index);
        }

        //close elasticsearch client
        driver.closeESClientConnection(client);
        //close spark
        sparkSession.close();

    }


}
