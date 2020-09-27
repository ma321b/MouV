package com.movierr.mouv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.movierr.mouv.Adapters.FavouritesAdapter;
import com.movierr.mouv.Adapters.RecommendationsAdapter;
import com.movierr.mouv.TMDBConfig.Config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class Recommendations extends AppCompatActivity {

    ArrayList<String> imageUrls = new ArrayList<>();
    ArrayList<String> movieNames = new ArrayList<>();
    ArrayList<String> ratings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        String firebaseUserID = getIntent().getStringExtra("userID");
        fetchRecommended(firebaseUserID);
    }

    /**
     * Fetches recommended movies for user
     * @param userID The Firebase userID of the user to retrieve user's favourites from
     */
    private void fetchRecommended(String userID) {
        FirebaseFirestore.getInstance()
                .collection("users").document(userID)
                .collection("favourites").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                // if user has some favourite movies added
                                ArrayList<FavouriteMovie> favs = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    favs.add(document.toObject(FavouriteMovie.class));
                                }
                                String[] tmdbIds = getTmdbIds(favs);
                                getRecommendedMovies(tmdbIds);
                            } else {
                                // user hasn't added any favourite movies
                                Toast.makeText(Recommendations.this,
                                        "Please add some favourite movies first!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Recommendations.this,
                                    "There was an error fetching favourites!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Sets the adapter to display the recommended movies for the user
     */
    private void setAdapter() {

        RecommendationsAdapter adapter = new RecommendationsAdapter(imageUrls, movieNames, ratings, this);
        RecyclerView recyclerView = findViewById(R.id.recommended_movies);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // setting the adapter
        recyclerView.setAdapter(adapter);
    }

    /**
     * Gets an array of all IMDB ratings from the list containing FavouriteMovie objects
     */
    private String[] getTmdbIds(ArrayList<FavouriteMovie> favouriteMovies) {
        String[] tmdbIds = new String[favouriteMovies.size()];
        for (int i = 0; i < favouriteMovies.size(); i++) {
            FavouriteMovie movie = favouriteMovies.get(i);
            tmdbIds[i] = movie.getTmdbID();
        }
        return tmdbIds;
    }

    /**
     * Gets the recommended movies from TMDB API, adding their data to this class's fields
     * @param tmdbIds The array containing TMDB IDs of user's favourite movies
     */
    private void getRecommendedMovies(String[] tmdbIds) {
        // get recommended for up to 4 favs
        if (tmdbIds.length >= 4) {
            for (int i = 0; i < 4; i++) {
                Random random = new Random();
                // random movie from user's fav collection to be used for recommendations
                String id = tmdbIds[random.nextInt(tmdbIds.length)];
                String url = "https://api.themoviedb.org/3/movie/" + id + "/recommendations?api_key="
                        + Config.API_KEY + "&language=en-US&page=1";
                RequestQueue q = Volley.newRequestQueue(this);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject object) {
                                try {
                                    JSONArray results = object.getJSONArray("results");
                                    for (int i = 0; i < 3; i++) {
                                        JSONObject movie = results.getJSONObject(i);
                                        imageUrls.add(Config.IMAGE_SECURE_BASE_URL + "original" + movie.getString("poster_path"));
                                        movieNames.add(movie.getString("title"));
                                        ratings.add(movie.getString("vote_average"));
                                    }
                                    // setting the adapter after populating the lists with the data req
                                    setAdapter();
                                } catch (org.json.JSONException e) {

                                };
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Recommendations.this,
                                        "An unexpected error occurred! Try again!", Toast.LENGTH_SHORT).show();
                            }
                        });
                q.add(request);
            }
        }
        else {
            Toast.makeText(this,
                    "Please add more movies as favourites to get recommendations!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Processes the response of JSON object request to create arrays of
     * recommended movies' data to bind it to adapter
     */
    private void processRecommendationsResponse(JSONObject object) {
        try {
            JSONArray results = object.getJSONArray("results");
            for (int i = 0; i < 3; i++) {
                JSONObject movie = results.getJSONObject(i);
                imageUrls.add(movie.getString("poster_path"));
                movieNames.add(movie.getString("title"));
                ratings.add(movie.getString("vote_average"));
            }
            // setting the adapter after populating the lists with the data req
            setAdapter();
        } catch (org.json.JSONException e) {

        }
    }
}