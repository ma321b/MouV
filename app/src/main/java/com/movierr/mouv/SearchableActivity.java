package com.movierr.mouv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.movierr.mouv.Adapters.MovieCategoryAdapter;
import com.movierr.mouv.TMDBConfig.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * This Activity is used in search box of the activity.
 * It basically handles the data entered in the search query by the user.
 */
public class SearchableActivity extends AppCompatActivity {

    private String toastText;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // initialise db
        db = FirebaseFirestore.getInstance();

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            try {
                performSearch(query);
            } catch (Exception e) {
                Toast.makeText(this, "An unexpected error occurred!", Toast.LENGTH_SHORT).show();
                finish();
            }
            // adds each query received by your searchable
            // activity (this) to the SearchRecentSuggestionsProvider (SuggestionProvider class).
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
    }

    /**
     * Performs the main search operation on the user's query
     * to match movies.
     *
     * @param query The search query provided by user.
     */
    private void performSearch(String query) throws UnsupportedEncodingException, JSONException {
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + Config.API_KEY
                + "&language=en-US&" + "query=" + URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

        RequestQueue q = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        processSearchMovieQueryResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finish();
                    }
                });
        q.add(request);
    }

    /**
     * Processes the results returned by the TMDB API for
     * a query to search a movie, and then updates the UI to show
     * the results.
     * @param response The response object from the API server
     */
    private void processSearchMovieQueryResponse(JSONObject response) {
        try {
            JSONArray resultsArray = response.getJSONArray("results");
            String[] tmdbIDs = new String[resultsArray.length()];
            String[] movieTitles = new String[resultsArray.length()];
            String[] imageUrls = new String[resultsArray.length()];
            String[] imdbRatings = new String[resultsArray.length()];

            for (int i = 0; i < resultsArray.length(); i++) {
                tmdbIDs[i] = resultsArray.getJSONObject(i).getString("id");
                // movie titles go in the movieTitle array
                movieTitles[i] = resultsArray
                        .getJSONObject(i)
                        .getString("title");

                // baseUrl + image size + poster path in the imageUrls
                // NOTE: using the http:// instead of https:// results in error cleartext
                //       traffic not allowed on image.tmdb.org, which throws a
                //       java.IO exception or smt.
                imageUrls[i] = Config.IMAGE_SECURE_BASE_URL + "original" +
                        resultsArray.getJSONObject(i).getString("poster_path");

                imdbRatings[i] = resultsArray
                        .getJSONObject(i)
                        .getString("vote_average");
            }
            updateUI(tmdbIDs, movieTitles, imageUrls, imdbRatings);
        } catch (JSONException e) {
            Toast.makeText(this, "An unexpected error occurred!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Updates the RecyclerView (and binds it to adapter) in the layout with the cards created
     * in layouts, with the movies' posters and their titles.
     *
     * @param movieTitles Array containing movie titles
     * @param imageUrls   Array containing urls of images
     */
    private void updateUI(String[] tmdbIDs, String[] movieTitles, String[] imageUrls, String[] imdbRatings) {
        MovieCategoryAdapter adapter = new
                MovieCategoryAdapter(tmdbIDs, movieTitles, imageUrls, imdbRatings, this);
        RecyclerView recyclerView = findViewById(R.id.search_results);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // setting the adapter
        recyclerView.setAdapter(adapter);
    }
}