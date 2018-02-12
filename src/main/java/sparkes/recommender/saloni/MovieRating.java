package sparkes.recommender.saloni;

import java.time.Instant;

public class MovieRating {
  private String userId;
  private String movieId;
  private String timeStamp;
  private String rating;

  public String getUserId() {
    return userId;
  }

  public void setUserId(final String userId) {
    this.userId = userId;
  }

  public String getMovieId() {
    return movieId;
  }

  public void setMovieId(final String movieId) {
    this.movieId = movieId;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(final String timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getRating() {
    return rating;
  }

  public void setRating(final String rating) {
    this.rating = rating;
  }

  public boolean anyNull() {
    if(movieId == null || timeStamp == null || rating == null || userId == null)
      return true;
    return false;
  }
}
