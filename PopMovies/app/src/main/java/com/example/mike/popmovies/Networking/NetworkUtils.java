package com.example.mike.popmovies.Networking;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Mike on 2/17/2018.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_KEY = "YOUR_API";
    private static final String LANGUAGE = "en-US";

    private final static String PARAM_API_KEY = "api_key";
    private final static String PARAM_LANGUAGE = "language";
    private final static String PARAM_PAGE = "page";

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w500";


    public static URL buildUrl(String typeOfRecord, int page) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .path("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(typeOfRecord)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page));

        Uri addressUri = builder.build();

        URL url = null;
        try {
            url = new URL(addressUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

     //   Log.v(TAG, "Built URI " + url);

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String buildImageUrl (String image_path) {
        String imageURL = IMAGE_BASE_URL + IMAGE_SIZE + image_path;
        Log.v(TAG, "Built Image URI " + imageURL);

        return imageURL;
    }
}
