package com.movierr.mouv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;


// todo add functionality to sign out

/**
 * Because the OtherActivity now includes a <meta-data> element in Manifest, to declare which
 * searchable activity to use for searches, the activity has enabled the search dialog.
 * While the user is in this activity, the onSearchRequested() method activates the search dialog.
 * When the user executes the search, the system starts SearchableActivity and delivers it the
 * ACTION_SEARCH intent.
 */
public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Choose an arbitrary request code value for Auth UI
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialising Firebase components:
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

        // initialising the auth state listener:
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // @param firebaseAuth is guaranteed to contain whether the user
                // is authenticated at that time or not
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed-in
                    onSignedInInitialise(user.getDisplayName());
                } else {
                    // user is signed-out
                    // this is where we need the sign in (Auth) UI
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)   // a way for the phone to auto save user credentials and try to log them in (we won't need it).
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(), // Gmail login flow
                                            new AuthUI.IdpConfig.EmailBuilder().build())) // Email login flow
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        // ref: https://stackoverflow.com/questions/61582391/purpose-of-removing-the-firebase-authstatelistener-for-authentication-on-onpause
        auth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ref: https://stackoverflow.com/questions/61582391/purpose-of-removing-the-firebase-authstatelistener-for-authentication-on-onpause
        auth.addAuthStateListener(authStateListener);
    }

    /**
     * Adding the search widget in the app bar when it is created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items_main, menu);

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
     * Invoked when the menu item "show favourites"
     * is clicked (using for showing favourites)
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.show_favourite_movies) {
            // if the "Show Favourites" button from overflow menu is clicked,
            // show user's favourite movies by launching Favourites activity
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // if user is logged in
                String userID = user.getUid();
                Intent intent = new Intent(this, Favourites.class);
                // putting userID as extra in Intent to facilitate fetching data from Firebase
                intent.putExtra("userID", userID);
                startActivity(intent);
            } else {
                // if user is not logged in
                Toast.makeText(this,
                        "Please log in to view favourites!", Toast.LENGTH_LONG).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When a user is signed in, initialise the UI,
     * showing a welcome message containing their name.
     * @param userName The signed in user
     */
    private void onSignedInInitialise(String userName) {
        String[] fullName = userName.split(" ");
        String firstName = fullName[0];
        TextView welcomeMessage = (TextView) findViewById(R.id.welcome_message);
        welcomeMessage.setText(String.format("Welcome back, %s!", firstName));
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