package com.movierr.movit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.movierr.movit.R;


public class MovieCategoryAdapter
        extends RecyclerView.Adapter<MovieCategoryAdapter.ViewHolder> {

    private String[] movieNames;

    // movie urls would then be fetched using the glide lib
    private String[] movieImageUrls;

    // required for the glide stuff
    private Context context;

    // The parametrized ViewHolder above in class declaration
    // is used to specify which views should be used for each data item.

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            // Our recycler view needs to display CardViews, so we specify that
            // our ViewHolder contains CardViews. If you want to display another
            // type of data in the recycler view, you define it here (i.e., constructor param)
            super(v);
            cardView = v;
        }
    }

    /**
     * Public constructor of this Adapter.
     */
    public MovieCategoryAdapter(String[] movieNames, String[] movieImageUrls, Context context) {
        this.movieNames = movieNames;
        this.movieImageUrls = movieImageUrls;
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
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView) cardView.findViewById(R.id.movie_image);
        TextView textView = (TextView) cardView.findViewById(R.id.movie_name);

        // Setting the movie's text (movie name/title)
        textView.setText(movieNames[position]);

        // Setting the movie's poster using Glide lib
        Glide.with(this.context)
                .load(movieImageUrls[position])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(new RequestOptions().override(cardView.getWidth(), cardView.getHeight()))  // resizing it to the same size as its parent
                .into(imageView);
    }
}
