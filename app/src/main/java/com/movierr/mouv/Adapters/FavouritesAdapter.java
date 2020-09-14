package com.movierr.mouv.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.movierr.mouv.Favourites;
import com.movierr.mouv.R;

/**
 * Adapter to provide data for displaying Favourite movies
 */
public class FavouritesAdapter
        extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {
    // urls of the movie poster images
    private String[] imageUrls;

    private String[] movieTitles;

    private String[] imdbRatings;

    private Context context;

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

    public FavouritesAdapter(String[] imageUrls, String[] movieTitles,
                             String[] imdbRatings, Context context) {
        this.imageUrls = imageUrls;
        this.movieTitles = movieTitles;
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
        return movieTitles.length;
    }

    @NonNull
    @Override
    public FavouritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // basically the TLDR of this method and the ViewHolder class is to tell android
        // that use THIS cardview below for displaying recyclerview data items
        CardView cardView = (CardView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_favourite_movies, parent, false);
        return new FavouritesAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView) cardView.findViewById(R.id.favourite_movie_image);

        // Setting the movie's poster using Glide lib
        Glide.with(this.context)
                .load(imageUrls[position])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(new RequestOptions().override(cardView.getWidth(), 250))  // resizing it to the same size as its parent (250dp being the height of cardview)
                .into(imageView);

        TextView movieName = (TextView) cardView.findViewById(R.id.favourite_movie_name);
        movieName.setText(movieTitles[position]);

        TextView imdbRating = (TextView) cardView.findViewById(R.id.favourite_imdb_rating);
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
}
