package com.movierr.mouv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.movierr.mouv.Adapters.FavouritesAdapter;

import java.util.ArrayList;

/**
 * This class fetches the favourite movies of the user from their database and displays them
 * when "Show Favourites" button from SearchableActivity is clicked
 */
public class Favourites extends AppCompatActivity {
    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        String firebaseUserID = getIntent().getStringExtra("userID");
        setAdapter(firebaseUserID);
    }

    /**
     * Sets the adapter to display the favourite movies of the user
     * @param userID The Firebase userID of the user to retrieve data from
     */
    private void setAdapter(String userID) {
        FirebaseFirestore.getInstance()
                .collection("users").document(userID)
                .collection("favourites").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<FavouriteMovie> favs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                favs.add(document.toObject(FavouriteMovie.class));
                            }
                            String[] imageUrls = getImgUrls(favs);
                            String[] movieNames = getMovieNames(favs);
                            String[] imdbRatings = getImdbRatings(favs);

                            FavouritesAdapter adapter = new FavouritesAdapter(imageUrls, movieNames, imdbRatings, Favourites.this);
                            RecyclerView recyclerView = findViewById(R.id.favourite_movies);
                            recyclerView.setHasFixedSize(true);

                            // use a linear layout manager
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Favourites.this);
                            recyclerView.setLayoutManager(layoutManager);

                            // setting the adapter
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(Favourites.this,
                                    "There was an error fetching favourites!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Gets an array of all image urls from the list containing FavouriteMovie objects
     */
    private String[] getImgUrls(ArrayList<FavouriteMovie> favouriteMovies) {
        String[] imageUrls = new String[favouriteMovies.size()];
        for (int i = 0; i < favouriteMovies.size(); i++) {
            FavouriteMovie movie = favouriteMovies.get(i);
            imageUrls[i] = movie.getImageUrl();
        }
        return imageUrls;
    }

    /**
     * Gets an array of all movie names from the list containing FavouriteMovie objects
     */
    private String[] getMovieNames(ArrayList<FavouriteMovie> favouriteMovies) {
        String[] movieNames = new String[favouriteMovies.size()];
        for (int i = 0; i < favouriteMovies.size(); i++) {
            FavouriteMovie movie = favouriteMovies.get(i);
            movieNames[i] = movie.getMovieTitle();
        }
        return movieNames;
    }

    /**
     * Gets an array of all IMDB ratings from the list containing FavouriteMovie objects
     */
    private String[] getImdbRatings(ArrayList<FavouriteMovie> favouriteMovies) {
        String[] imdbRatings = new String[favouriteMovies.size()];
        for (int i = 0; i < favouriteMovies.size(); i++) {
            FavouriteMovie movie = favouriteMovies.get(i);
            imdbRatings[i] = movie.getImdbRating();
        }
        return imdbRatings;
    }
}