package com.movierr.movit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movierr.movit.Adapters.MovieCategoryAdapter;
import com.movierr.movit.Network.Connection;
import com.movierr.movit.TMDBConfig.Config;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            try {
                performSearch(query);
            } catch (Exception e) {
                toastText = "Sorry, there was a network issue";
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

        // TESTING:
//        Connection connection = new Connection(this, url);
//        JSONObject object = connection.getResponseObject();

        RequestQueue q = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray resultsArray = null;
                        try {
                            resultsArray = response.getJSONArray("results");
                            String[] movieTitles = new String[resultsArray.length()];
                            String[] imageUrls = new String[resultsArray.length()];
                            String[] imdbRatings = new String[resultsArray.length()];

                            for (int i = 0; i < resultsArray.length(); i++) {
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
                            updateUI(movieTitles, imageUrls, imdbRatings);
                        } catch (JSONException e) {
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finish();
                    }
                });
        q.add(request);
        /*if (object != null) {
            resultsArray = response.getJSONArray("results");
            String[] movieTitles = new String[resultsArray.length()];
            String[] imageUrls = new String[resultsArray.length()];

            for (int i = 0; i < resultsArray.length(); i++) {
                // movie titles go in the movieTitle array
                movieTitles[i] = resultsArray.getJSONObject(i).getString("title");

                // baseUrl + image size + poster path in the imageUrls
                imageUrls[i] = Config.IMAGE_BASE_URL + "original" +
                        resultsArray.getJSONObject(i).getString("poster_path");
            }
            for(String urlss : imageUrls) {
                Log.i("idk", urlss);
            }
            updateUI(movieTitles, imageUrls);
        } else {
            // null object means an error occurred.
            finish();  // for now.
        }*/
    }

    /**
     * Updates the RecyclerView (and binds it to adapter) in the layout with the cards created
     * in layouts, with the movies' posters and their titles.
     *
     * @param movieTitles Array containing movie titles
     * @param imageUrls   Array containing urls of images
     */
    private void updateUI(String[] movieTitles, String[] imageUrls, String[] imdbRatings) {
        MovieCategoryAdapter adapter = new
                MovieCategoryAdapter(movieTitles, imageUrls, imdbRatings, this);
        RecyclerView recyclerView = findViewById(R.id.search_results);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // setting the adapter
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toastText != null) {
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "An unexpected error occurred!", Toast.LENGTH_SHORT).show();
        }
    }
}