package sparkes.recommender.movies;

public class Links {
    private String movieId;
    private String imdbId;
    private String tmdbId;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(final String movieId) {
        this.movieId = movieId;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(final String imbId) {
        this.imdbId = imbId;
    }

    public String getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(final String tmdbId) {
        this.tmdbId = tmdbId;
    }

    public boolean anyNull() {
        if (movieId == null || imdbId == null || tmdbId == null) {
            return true;
        }
        return false;
    }
}
