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
import com.movierr.mouv.R;

import java.util.ArrayList;

/**
 * Adapter for Recommended Movies
 */
public class RecommendationsAdapter extends
        RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {
    private ArrayList<String> imageUrls;

    private ArrayList<String> movieTitles;

    private ArrayList<String> ratings;

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

    public RecommendationsAdapter(ArrayList<String> imageUrls, ArrayList<String> movieTitles,
                                  ArrayList<String> ratings, Context context) {
        this.imageUrls = imageUrls;
        this.movieTitles = movieTitles;
        this.ratings = ratings;
        this.context = context;
    }

    /**
     * Tells the adapter how many data items we have.
     *
     * @return int number of items.
     */
    @Override
    public int getItemCount() {
        return movieTitles.size();
    }

    @NonNull
    @Override
    public RecommendationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // basically the TLDR of this method and the ViewHolder class is to tell android
        // that use THIS cardview below for displaying recyclerview data items
        CardView cardView = (CardView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_recommended_movies, parent, false);
        return new RecommendationsAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(RecommendationsAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView) cardView.findViewById(R.id.recommended_movie_image);

        // Setting the movie's poster using Glide lib
        Glide.with(this.context)
                .load(imageUrls.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        TextView movieName = (TextView) cardView.findViewById(R.id.recommended_movie_name);
        movieName.setText(movieTitles.get(position));

        TextView rating = (TextView) cardView.findViewById(R.id.recommended_rating);
        // Setting the text of imdb rating of the movie
        setImdbRatingTextColor(ratings.get(position), rating);
    }

    /**
     * Set the color of the string showing IMDB rating of the movie.
     */
    private void setImdbRatingTextColor(String rating, TextView view) {
        // tutorial here: https://codinginflow.com/tutorials/android/spannablestring-text-color

        SpannableString imdbRatingString = new SpannableString("Rating: ");
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
