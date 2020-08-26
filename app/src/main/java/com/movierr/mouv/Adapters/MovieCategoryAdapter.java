package com.movierr.mouv.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.movierr.mouv.FavouriteMovie;
import com.movierr.mouv.R;
import com.movierr.mouv.TMDBConfig.Config;

import org.json.JSONException;
import org.json.JSONObject;


public class MovieCategoryAdapter
        extends RecyclerView.Adapter<MovieCategoryAdapter.ViewHolder> {
    // The parametrized <MovieCategoryAdapter.ViewHolder> above in class declaration
    // is used to specify which views should be used for each data item.

    // the array containing all the TMDB IDs of movies (required for implementing
    // the functionality of the click listeners in recyclerview):
    private String[] tmdbIDs;

    private String[] movieNames;

    // movie urls would then be fetched using the glide lib
    private String[] movieImageUrls;

    // Array containing IMDB ratings of the movies
    private String[] imdbRatings;

    // required for the glide stuff
    private Context context;

    private static final String TAG = MovieCategoryAdapter.class.getName();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            // Our recycler view needs to display CardViews, so we specify that
            // our ViewHolder contains CardViews. If you want to display another
            // type of data in the recycler view, you define it above (i.e., constructor param).

            super(v);
            cardView = v;
        }
    }

    /**
     * Public constructor of this Adapter.
     */
    public MovieCategoryAdapter(String[] tmdbIDs, String[] movieNames,
                                String[] movieImageUrls, String[] imdbRatings, Context context) {
        this.tmdbIDs = tmdbIDs;
        this.movieNames = movieNames;
        this.movieImageUrls = movieImageUrls;
        this.imdbRatings = imdbRatings;
        this.context = context;
    }

    /**
     * Tells the adapter how many data items we have.
     *
     * @return int number of items.
     */
    @Override
    public int getItemCount() {
        return movieNames.length;
    }

    /**
     * Gets called when the recycler view requires a new view holder.
     * The recycler view calls the method repeatedly when the recycler view is first
     * constructed to build the set of view holders that will be displayed on the screen.
     *
     * @param parent   The recycler view itself.
     * @param viewType Used if you want to display different kinds of views for different
     *                 items in the list.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // basically the TLDR of this method and the ViewHolder class is to tell android
        // that use THIS cardview below for displaying recyclerview data items
        CardView cardView = (CardView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_captioned_image, parent, false);
        return new ViewHolder(cardView);
    }

    /**
     * You add data to the card views by implementing the adapter’s onBindViewHolder() method.
     * This gets called whenever the recycler view needs to display data in a view holder.
     *
     * @param holder   The view holder the data needs to be bound to.
     * @param position The position in the data set of the data that needs to be bound.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchImdbPage(tmdbIDs[position]);
            }
        });

        ImageView imageView = (ImageView) cardView.findViewById(R.id.movie_image);
        final TextView movieName = (TextView) cardView.findViewById(R.id.movie_name);

        AppCompatButton addToFavourites = (AppCompatButton) cardView.findViewById(R.id.add_favourite);
        addToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavouriteToFirestore(position);
            }
        });

        TextView imdbRating = (TextView) cardView.findViewById(R.id.imdb_rating);

        // Setting the movie's text (movie name/title)
        movieName.setText(movieNames[position]);

        // Setting the movie's poster using Glide lib
        Glide.with(this.context)
                .load(movieImageUrls[position])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(new RequestOptions().override(cardView.getWidth(), 250))  // resizing it to the same size as its parent (250dp being the height of cardview)
                .into(imageView);

        // Setting the text of imdb rating of the movie
        setImdbRatingTextColor(imdbRatings[position], imdbRating);
    }

    /**
     * Set the color of the string showing IMDB rating of the movie.
     */
    private void setImdbRatingTextColor(String rating, TextView view) {
        // tutorial here: https://codinginflow.com/tutorials/android/spannablestring-text-color

        SpannableString imdbRatingString = new SpannableString("IMDB rating: ");
        SpannableString ratingValue = new SpannableString(rating);

        // IMDB's theme yellow color is #f3ce13 as below
        ForegroundColorSpan fcsImdbYellow =
                new ForegroundColorSpan(Color.parseColor("#f3ce13"));
        ForegroundColorSpan fcsWhite =
                new ForegroundColorSpan(Color.parseColor("#FFFFFF"));

        // sets color of the string "IMDB Rating: " to IMDB's theme yellow
        // and that of the actual value to white:
        imdbRatingString.setSpan(fcsImdbYellow, 0, imdbRatingString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ratingValue.setSpan(fcsWhite, 0, rating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // sets it in the layout
        // TextUtils.concat concatenates two SpannableString values (with diff colors as above).
        view.setText(TextUtils.concat(imdbRatingString, ratingValue));
    }

    /**
     * Launches the respective IMDB page of the movie in the user's web browser.
     *
     * @param movieID The TMDB ID of the movie.
     */
    private void launchImdbPage(String movieID) {
        String url = "https://api.themoviedb.org/3/movie/" + movieID + "/external_ids?api_key="
                + Config.API_KEY;
        RequestQueue q = Volley.newRequestQueue(this.context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // getting the IMDB ID of the movie from the API
                            String imdbID = response.getString("imdb_id");
                            // launch the user's browser on the movie's URL
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.imdb.com/title/" + imdbID + "/"));
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            Toast.makeText(context,
                                    "Sorry a network error occurred",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,
                                "Sorry a network error occurred",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        q.add(request);
    }


    /**
     * Adds the favourite movie object to the Firebase Firestore.
     *
     * @param position The position of the movie info (check usage in onBindViewHolder())
     */
    private void addFavouriteToFirestore(int position) {
        // run this method when "add to favourites" button is clicked
        // todo add code to add to the db here
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getUid();

            FavouriteMovie favMovie = new FavouriteMovie(tmdbIDs[position], movieImageUrls[position]);

            // add the object containing info about the favourite movie to a sub-collection
            // called "favourites" in the document path "users/userID" where userID is the
            // unique Firebase-provided user ID of the user.
            FirebaseFirestore.getInstance()
                    .collection("users").document(userID)
                    .collection("favourites").add(favMovie)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            Toast.makeText(MovieCategoryAdapter.this.context,
                                    "Added to favourites!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        } else {
            Toast.makeText(MovieCategoryAdapter.this.context, "Please Log In first!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Run whenever the "add to favourites" button is clicked.
     * Displays the movie name in toast currently (only for testing)
     */
    private void testToastListener(CharSequence movieName) {
        Toast.makeText(context, "Movie name: " + movieName, Toast.LENGTH_SHORT).show();
    }
}
