package com.example.mike.popmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mike.popmovies.Networking.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mike on 2/16/2018.
 */

public class LoadMoviesAdapter extends RecyclerView.Adapter<LoadMoviesAdapter.LoadMoviesAdapterViewHolder> {

    public static final String INTENT_MOVIE = "movie_s";

    private final Context mContext;
    private final LayoutInflater mInflater;
    private ArrayList<MovieObject> mData;


    public LoadMoviesAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);


    }

    @Override
    public LoadMoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new LoadMoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoadMoviesAdapterViewHolder holder, int position) {
        String image_path = mData.get(position).getPoster_path();
        String imageURL = NetworkUtils.buildImageUrl(image_path);


        Picasso.with(mContext)
                .load(imageURL)
                .into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.size();
    }

    public class LoadMoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView imageView;

        LoadMoviesAdapterViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.movieImage);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieObject movie = mData.get(adapterPosition);

            Intent openDetailsIntent = new Intent(mContext, DetailActivity.class);
            openDetailsIntent.putExtra(INTENT_MOVIE, movie);
            mContext.startActivity(openDetailsIntent);
        }
    }


    public void setMovieData(ArrayList<MovieObject> movieData) {
        mData = movieData;
        notifyDataSetChanged();
    }
}
