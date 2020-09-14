package com.movierr.mouv;

import android.content.Context;

/**
 * Represents the information about the movie added to the
 * favourite movies' collection of the user (used to be added to the Firebase Firestore).
 */
public class FavouriteMovie {
    // All these fields are required to be able to display fav movies in the UI when we need
    private String tmdbID;
    private String movieTitle;
    private String imageUrl;
    private String imdbRating;

    public FavouriteMovie(String tmdbID, String movieTitle, String imageUrl, String imdbRating) {
        this.tmdbID = tmdbID;
        this.movieTitle = movieTitle;
        this.imageUrl = imageUrl;
        this.imdbRating = imdbRating;
    }

    /**
     * No argument Constructor required for Firebase data conversion to object
     */
    public FavouriteMovie() {

    }

    public String getTmdbID() {
        return tmdbID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public String getMovieTitle() {
        return movieTitle;
    }
}
