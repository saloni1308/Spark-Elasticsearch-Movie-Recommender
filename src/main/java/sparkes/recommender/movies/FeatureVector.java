package sparkes.recommender.movies;

import java.io.Serializable;
import java.util.Map;

public class FeatureVector {
  String movieId;
  Map<String, AlsModel> featureVector;

  public String getMovieId() {
    return movieId;
  }

  public void setMovieId(final String movieId) {
    this.movieId = movieId;
  }

  public Map<String, AlsModel> getFeatureVector() {
    return featureVector;
  }

  public void setFeatureVector(final Map<String, AlsModel> featureVector) {
    this.featureVector = featureVector;
  }

}
