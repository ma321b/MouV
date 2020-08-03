package com.movierr.mouv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;

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

    /**
     * Adding the search widget in the app bar when it is created.
     */
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
        searchView.setIconifiedByDefault(true); // iconify the widget; do not expand it by default (this is the default option. writing it has no effect. just there for clarity)
        searchView.setSubmitButtonEnabled(true); // Enables the submit button (as opposed to pressing return)
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Invoked when the button saying movie search is clicked
     */
    public void startSearch(View view) {
        SearchView searchView = (SearchView) findViewById(R.id.search_widget);
        // when the user presses the button it launches the search widget
        searchView.setIconified(false);
    }
}