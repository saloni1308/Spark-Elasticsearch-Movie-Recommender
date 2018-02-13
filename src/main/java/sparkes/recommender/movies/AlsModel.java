package sparkes.recommender.movies;

import java.io.Serializable;

public class AlsModel implements Serializable {
    String features;
    String version;
    long timestamp;
    String movieId;

    public String getFeatures() {
        return features;
    }

    public void setFeatures(final String features) {
        this.features = features;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(final String movieId) {
        this.movieId = movieId;
    }
}
