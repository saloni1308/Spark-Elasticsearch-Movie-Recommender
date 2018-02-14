package sparkes.recommender.movies;

import java.io.Serializable;

public class AlsModel implements Serializable {
    String factor;
    String version;
    long timestamp;
    String movieId;

    public String getFactor() {
        return factor;
    }

    public void setFactor(final String factor) {
        this.factor = factor;
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
