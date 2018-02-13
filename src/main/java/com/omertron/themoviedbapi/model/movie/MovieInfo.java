/*
 *      Copyright (c) 2004-2016 Stuart Boston
 *
 *      This file is part of TheMovieDB API.
 *
 *      TheMovieDB API is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      TheMovieDB API is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with TheMovieDB API.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.omertron.themoviedbapi.model.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.omertron.themoviedbapi.enumeration.MovieMethod;
import com.omertron.themoviedbapi.interfaces.AppendToResponse;
import com.omertron.themoviedbapi.interfaces.Identification;
import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.Language;
import com.omertron.themoviedbapi.model.artwork.Artwork;
import com.omertron.themoviedbapi.model.change.ChangeKeyItem;
import com.omertron.themoviedbapi.model.collection.Collection;
import com.omertron.themoviedbapi.model.credits.MediaCreditCast;
import com.omertron.themoviedbapi.model.credits.MediaCreditCrew;
import com.omertron.themoviedbapi.model.keyword.Keyword;
import com.omertron.themoviedbapi.model.list.UserList;
import com.omertron.themoviedbapi.model.media.AlternativeTitle;
import com.omertron.themoviedbapi.model.media.MediaCreditList;
import com.omertron.themoviedbapi.model.media.Translation;
import com.omertron.themoviedbapi.model.media.Video;
import com.omertron.themoviedbapi.model.review.Review;
import com.omertron.themoviedbapi.results.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Movie Info
 *
 * @author stuart.boston
 */
public class MovieInfo extends MovieBasic implements Serializable, Identification, AppendToResponse<MovieMethod> {

    private static final long serialVersionUID = 100L;
    // AppendToResponse
    private final Set<MovieMethod> methods = EnumSet.noneOf(MovieMethod.class);
    @JsonProperty("belongs_to_collection")
    private Collection belongsToCollection;
    @JsonProperty("budget")
    private long budget;
    @JsonProperty("genres")
    private List<Genre> genres;
    @JsonProperty("homepage")
    private String homepage;
    @JsonProperty("imdb_id")
    private String imdbID;
    @JsonProperty("production_companies")
    private List<ProductionCompany> productionCompanies = Collections.emptyList();
    @JsonProperty("production_countries")
    private List<ProductionCountry> productionCountries = Collections.emptyList();
    @JsonProperty("runtime")
    private int runtime;
    @JsonProperty("spoken_languages")
    private List<Language> spokenLanguages = Collections.emptyList();
    @JsonProperty("tagline")
    private String tagline;
    @JsonProperty("status")
    private String status;
    // AppendToResponse Properties
    private List<AlternativeTitle> alternativeTitles = Collections.emptyList();
    private MediaCreditList credits = new MediaCreditList();
    private List<Artwork> images = Collections.emptyList();
    private List<Keyword> keywords = Collections.emptyList();
    private List<ReleaseInfo> releases = Collections.emptyList();
    private List<Video> videos = Collections.emptyList();
    private List<Translation> translations = Collections.emptyList();
    private List<MovieInfo> similarMovies = Collections.emptyList();
    private List<Review> reviews = Collections.emptyList();
    private List<UserList> lists = Collections.emptyList();
    private List<ChangeKeyItem> changes = Collections.emptyList();

    // <editor-fold defaultstate="collapsed" desc="Getter methods">
    public Collection getBelongsToCollection() {
        return belongsToCollection;
    }

    // <editor-fold defaultstate="collapsed" desc="Setter methods">
    public void setBelongsToCollection(Collection belongsToCollection) {
        this.belongsToCollection = belongsToCollection;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }
    // </editor-fold>

    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public List<ProductionCountry> getProductionCountries() {
        return productionCountries;
    }

    public void setProductionCountries(List<ProductionCountry> productionCountries) {
        this.productionCountries = productionCountries;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<Language> getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(List<Language> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="AppendToResponse Getters">
    public List<AlternativeTitle> getAlternativeTitles() {
        return alternativeTitles;
    }

    //<editor-fold defaultstate="collapsed" desc="AppendToResponse Setters">
    @JsonSetter("alternative_titles")
    public void setAlternativeTitles(WrapperAlternativeTitles alternativeTitles) {
        this.alternativeTitles = alternativeTitles.getTitles();
        addMethod(MovieMethod.ALTERNATIVE_TITLES);
    }

    public List<MediaCreditCast> getCast() {
        return credits.getCast();
    }

    public List<MediaCreditCrew> getCrew() {
        return credits.getCrew();
    }

    public List<Artwork> getImages() {
        return images;
    }

    @JsonSetter("images")
    public void setImages(WrapperImages images) {
        this.images = images.getAll();
        addMethod(MovieMethod.IMAGES);
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    @JsonSetter("keywords")
    public void setKeywords(WrapperMovieKeywords keywords) {
        this.keywords = keywords.getKeywords();
        addMethod(MovieMethod.KEYWORDS);
    }

    public List<ReleaseInfo> getReleases() {
        return releases;
    }

    @JsonSetter("releases")
    public void setReleases(WrapperReleaseInfo releases) {
        this.releases = releases.getCountries();
        addMethod(MovieMethod.RELEASES);
    }

    public List<Video> getVideos() {
        return videos;
    }

    @JsonSetter("videos")
    public void setVideos(WrapperVideos trailers) {
        this.videos = trailers.getVideos();
        addMethod(MovieMethod.VIDEOS);
    }
    // </editor-fold>

    public List<Translation> getTranslations() {
        return translations;
    }

    @JsonSetter("translations")
    public void setTranslations(WrapperTranslations translations) {
        this.translations = translations.getTranslations();
        addMethod(MovieMethod.TRANSLATIONS);
    }

    public List<MovieInfo> getSimilarMovies() {
        return similarMovies;
    }

    @JsonSetter("similar")
    public void setSimilarMovies(WrapperGenericList<MovieInfo> similarMovies) {
        this.similarMovies = similarMovies.getResults();
        addMethod(MovieMethod.SIMILAR);
    }

    public List<UserList> getLists() {
        return lists;
    }

    @JsonSetter("lists")
    public void setLists(WrapperGenericList<UserList> lists) {
        this.lists = lists.getResults();
        addMethod(MovieMethod.LISTS);
    }

    public List<Review> getReviews() {
        return reviews;
    }

    @JsonSetter("reviews")
    public void setReviews(WrapperGenericList<Review> reviews) {
        this.reviews = reviews.getResults();
        addMethod(MovieMethod.REVIEWS);
    }

    public List<ChangeKeyItem> getChanges() {
        return changes;
    }

    @JsonSetter("changes")
    public void setChanges(WrapperChanges changes) {
        this.changes = changes.getChangedItems();
    }

    @JsonSetter("credits")
    public void setCredits(MediaCreditList credits) {
        this.credits = credits;
        addMethod(MovieMethod.CREDITS);
    }
    // </editor-fold>

    private void addMethod(MovieMethod method) {
        methods.add(method);
    }

    @Override
    public boolean hasMethod(MovieMethod method) {
        return methods.contains(method);
    }
}
