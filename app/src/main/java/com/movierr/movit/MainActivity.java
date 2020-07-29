package com.movierr.movit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Because the OtherActivity now includes a <meta-data> element in Manifest, to declare which
 * searchable activity to use for searches, the activity has enabled the search dialog.
 * While the user is in this activity, the onSearchRequested() method activates the search dialog.
 * When the user executes the search, the system starts SearchableActivity and delivers it the
 * ACTION_SEARCH intent.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);

        // Get the SearchView and set the searchable configuration ->
        // Searchable configuration specifies various UI aspects of the search widget.
        // It is the res/xml/searchable file.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_widget).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, SearchableActivity.class))
        );
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true); // Enables the submit button (as opposed to pressing return)
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This is being used as a button click listener
     */
    public void test(View view) {
        final TextView text = (TextView) findViewById(R.id.textTest);

        RequestQueue q = Volley.newRequestQueue(this);
        String apiKey = "7be4389b4300fcd9529d877730e7987a";

        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&language=en-US&" +
                "query=interstellar";

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        text.setText("Response is: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        text.setText("Something went wrong innih");
                    }
                });

        q.add(req);
    }
}