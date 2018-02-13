package sparkes.recommender.movies;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.Dataset;
import scala.Serializable;
import scala.Tuple2;

import java.time.Instant;
import java.util.Arrays;

public class Recommender implements Serializable {

    ALS als;
    MatrixFactorizationModel model;


    JavaRDD<Rating> createAlsData(final Dataset<MovieRating> ratings_filter) {
        return ratings_filter.toJavaRDD().map(r -> {
            return new Rating(Integer.parseInt(r.getUserId()), Integer.parseInt(r.getMovieId()), Float.parseFloat(r.getRating()));
        });
    }

    void trainModelByAls(JavaRDD<Rating> als_data) {
        als = new ALS();
        als.setLambda(0.1);
        als.setAlpha(0.1);
        als.setIterations(10);
        als.setRank(10);
        als.setSeed(42);
        model = als.run(als_data);
    }

    AlsModel getModelVector(Tuple2<Integer, double[]> model, String version) {
        AlsModel alsModel = new AlsModel();
        alsModel.setVersion(version);
        alsModel.setTimestamp(Instant.now().getEpochSecond());
        alsModel.setMovieId(model._1().toString());
        alsModel.setFeatures(Arrays.toString(model._2()).replace(",", "|").replace("[", "").replace("]", ""));
        return alsModel;
    }

/*    private void getSimilarMovies() {

    }*/


}
