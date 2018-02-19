package com.example.mike.popmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mike.popmovies.Networking.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int numberOfColumnsPortrait = 3;
    private static final int numberOfColumnsLandscape = 5;
    private static final String ON_SAVED_TYPE_RECORD_KEY = "typeOfRecord";
    private static final String MY_PREFS = "myPrefernces";

    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.recyclerview_main) RecyclerView mRecyclerView;

    private LoadMoviesAdapter loadMoviesAdapter;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private ArrayList<MovieObject> movies;
    private int mPage;
    private String mTypeOfRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // doesn't work
/*        if (savedInstanceState != null) {
            mTypeOfRecord = savedInstanceState.getString(ON_SAVED_TYPE_RECORD_KEY);
            Log.v("myTAG notNull: ", savedInstanceState.getString(ON_SAVED_TYPE_RECORD_KEY));
        } else {
            mTypeOfRecord = "popular";
            Log.v("myTAG Null: ", mTypeOfRecord);
        }*/

        SharedPreferences prefs = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        String restoredTypeOfRecord = prefs.getString(ON_SAVED_TYPE_RECORD_KEY, null);
        if (restoredTypeOfRecord != null) {
            mTypeOfRecord = restoredTypeOfRecord;
        } else {
            mTypeOfRecord = "popular";
        }


        boolean isOnline = isOnline();
        if (!isOnline) {
            final AlertDialog.Builder alterDialogBuilder = new AlertDialog.Builder(this);
            alterDialogBuilder.setTitle(R.string.no_internet_connection)
                    .setMessage(R.string.no_internet_message)
                    .setPositiveButton(R.string.no_internet_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }
                    );

            alterDialogBuilder.show();
        }

        movies = new ArrayList<MovieObject>();
        mPage = 1;

        GridLayoutManager gridLayoutManager;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(this, numberOfColumnsPortrait);
        } else {
            gridLayoutManager = new GridLayoutManager(this, numberOfColumnsLandscape);
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);

        loadMoviesAdapter = new LoadMoviesAdapter(this);
        mRecyclerView.setAdapter(loadMoviesAdapter);

        mScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mPage++;
                Log.v(MainActivity.class.getSimpleName(), "onLoadMore:  " + mPage + " " + mTypeOfRecord);
                new TheMovieRequestTask().execute();
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);

        // is it really necessary?
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        new TheMovieRequestTask().execute();
    }




    public class TheMovieRequestTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
           // mRecyclerView.setVisibility(View.INVISIBLE);
          //  mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();


        }


        @Override
        protected JSONObject doInBackground(Void... voids) {

            URL searchUrl = NetworkUtils.buildUrl(mTypeOfRecord, mPage);

            String theMovieSearchResults;
            JSONObject jsonObject = null;
            try {
                theMovieSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                try {
                    jsonObject = new JSONObject(theMovieSearchResults);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject theMovieSearchResults) {

            if (theMovieSearchResults != null && !theMovieSearchResults.equals("")) {

                ArrayList<MovieObject> moreMovies = JsonUtils.parseJson2Movies(theMovieSearchResults);
                movies.addAll(moreMovies);

                loadMoviesAdapter.setMovieData(movies);
                loadMoviesAdapter.notifyDataSetChanged();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                showErrorMessage();
            }

        }
    }



    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.sortBy)
                        .setItems(R.array.sort_options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        mTypeOfRecord = "popular";
                                                break;
                                    case 1:
                                        mTypeOfRecord = "top_rated";
                                                break;
                                    case 2:
                                        mTypeOfRecord = "upcoming";
                                        break;
                                    default:
                                        mTypeOfRecord = "popular";

                                }
                                movies.clear();
                                mPage = 1;
                                mScrollListener.resetState();
                                mRecyclerView.setVisibility(View.INVISIBLE);
                                mLoadingIndicator.setVisibility(View.VISIBLE);
                                new TheMovieRequestTask().execute();
                            }
                        });
                builder.show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS, MODE_PRIVATE).edit();
        editor.putString(ON_SAVED_TYPE_RECORD_KEY, mTypeOfRecord);
        editor.apply();
        super.onSaveInstanceState(outState);
    //    outState.putString(ON_SAVED_TYPE_RECORD_KEY, mTypeOfRecord);
    }
}
