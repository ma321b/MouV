package com.movierr.mouv;

/**
 * Represents the information about the movie added to the
 * favourite movies' collection of the user (used to be added to the Firebase Firestore).
 */
public class FavouriteMovie {
    // TMDB ID of the movie
    private String tmdbID;

    private String imageUrl;

    public FavouriteMovie(String tmdbID, String imageUrl) {
        this.tmdbID = tmdbID;
        this.imageUrl = imageUrl;
    }

    public String getTmdbID() {
        return tmdbID;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
