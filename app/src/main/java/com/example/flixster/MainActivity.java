package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    MovieAdapter movieAdapter;
    List<Movie> movies;
    RadioGroup rgSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        rgSortBy = binding.rgSortBy;
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.rbPopularity) {
                    Collections.sort(movies, new Movie.MoviePopularityComparator());
                }
                else if (radioGroup.getCheckedRadioButtonId() == R.id.rbVoteAverage) {
                    Collections.sort(movies, new Movie.MovieVoteRatingComparator());
                }
                else if (radioGroup.getCheckedRadioButtonId() == R.id.rbReleaseDate) {
                    Collections.sort(movies, new Movie.MovieReleaseDateComparator());
                }
                movieAdapter.notifyDataSetChanged();
            }
        });
        RecyclerView rvMovies = binding.rvMovies;
        movies = new ArrayList<>();

        // Create the adapter
        movieAdapter = new MovieAdapter(this, movies);

        // Set the adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);

        // Set a Layout Manager on the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        getPictureSizes(movieAdapter);
        getMovies(movieAdapter);
    }

    private void getMovies(final MovieAdapter movieAdapter) {
        final String NOW_PLAYING_URL = getString(R.string.NOW_PLAYING_API) + getString(R.string.moviedb_api_key);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "getMovies onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    Collections.sort(movies, new Movie.MoviePopularityComparator());
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies: " + movies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "getMovies onFailure");
            }
        });
    }

    private void getPictureSizes(final MovieAdapter movieAdapter) {
        final String CONFIG_URL = getString(R.string.CONFIG_API) + getString(R.string.moviedb_api_key);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(CONFIG_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "getPosterSize onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray posterArray = jsonObject.getJSONObject("images").getJSONArray("poster_sizes");
                    Movie.posterSize = posterArray.optString(3, "w342"); // set posterSize
                    JSONArray backdropArray = jsonObject.getJSONObject("images").getJSONArray("backdrop_sizes");
                    Movie.backdropSize = backdropArray.optString(1, "w780");
                    movieAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "getPosterSize onFailure");
            }
        });
    }

}