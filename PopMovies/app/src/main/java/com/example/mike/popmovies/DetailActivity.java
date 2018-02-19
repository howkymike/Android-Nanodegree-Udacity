package com.example.mike.popmovies;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mike.popmovies.Networking.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    // Can I make it private?
    @BindView(R.id.tv_title) TextView mTitle;
    @BindView(R.id.tv_releaseDate) TextView mReleaseDate;
    @BindView(R.id.tv_overview) TextView mOverview;
    @BindView(R.id.image_backdrop) ImageView mBackdrop;
    @BindView(R.id.image_poster) ImageView mPoster;
    @BindView(R.id.rb_vote) RatingBar mRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);


        Intent receivedIntent = getIntent();
        MovieObject movie = (MovieObject) receivedIntent.getSerializableExtra(LoadMoviesAdapter.INTENT_MOVIE);

        this.setTitle(movie.getTitle());

        String backdropUrl = NetworkUtils.buildImageUrl(movie.getBackdrop_path());
        Picasso.with(this)
                .load(backdropUrl)
                .into(mBackdrop);
        String posterUrl = NetworkUtils.buildImageUrl(movie.getPoster_path());
        Picasso.with(this)
                .load(posterUrl)
                .into(mPoster);

        mTitle.setText(movie.getTitle());

        String dateString = movie.getRelease_date();
     //   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
     //   Date convertedDate = new Date();
     //   try {
     //       convertedDate = dateFormat.parse(dateString);
     //   } catch (ParseException e) {
     //       e.printStackTrace();
        //  }
        String date = "(" + dateString + ")";
        mReleaseDate.setText(date);


        mRating.setRating( (float) movie.getVote_average() / 2);
        mOverview.setText(movie.getOverview());

    }
}
