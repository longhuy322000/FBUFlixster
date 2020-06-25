package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.Target;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.ReviewAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;
import com.example.flixster.models.Review;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%s/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String REVIEW_URL = "https://api.themoviedb.org/3/movie/%s/reviews?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public final int RADIUS = 30; // corner radius for image
    public static final String TAG = "MovieDetailsActivity";

    Movie movie;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivBackdrop;
    YouTubePlayerFragment playerView;
    YouTubePlayer youTubePlayer;
    RecyclerView rvReview;
    String videoId;
    List<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use ViewBinding
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // set data
        tvTitle = binding.tvTitle;
        tvOverview = binding.tvOverview;
        rbVoteAverage = binding.rbVoteAverage;
        ivBackdrop = binding.ivBackdrop;
        rvReview = binding.rvReview;

        // set up playerView for Youtube Player Fragment
        playerView = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player);
        playerView.initialize(getString(R.string.youtube_api_key), this);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s", movie));

        // set data
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 1..10, convert to 0.5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        // get video key and display youtube video in fragment
        getVideoKey();

        reviews = new ArrayList<>();
        final ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviews);

        rvReview.setAdapter(reviewAdapter);

        rvReview.setLayoutManager(new LinearLayoutManager(this));

        getReviews(reviewAdapter);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Log.i(TAG, "OnYoutubeInitSuccess");
        this.youTubePlayer = youTubePlayer;
        getVideoKey();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.e(TAG, "Error initializing Youtube player");
    }

    private void getVideoKey() {
        String youtubeUrl = String.format(VIDEO_URL, movie.getId());
        Log.i(TAG, "youtube urf " + youtubeUrl);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(youtubeUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "getVideoKey onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    // Display the video if there exists a trailer
                    if (results.length() > 0) {
                        videoId = results.getJSONObject(0).getString("key");
                        youTubePlayer.cueVideo(videoId);
                    }
                    // otherwise display backdrop image
                    else {
                        displayBackdropImage();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "getVideoKey hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "getVideoKey onFailure");
            }
        });
    }

    private void displayBackdropImage() {
        String aa = movie.getBackdropPath();
        Log.i(TAG, "backdrop path " + movie.getBackdropPath());
        Glide.with(this)
                .load(movie.getBackdropPath())
                .override(Target.SIZE_ORIGINAL)
                .into(ivBackdrop);
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
    }

    private void getReviews(final ReviewAdapter reviewAdapter) {
        AsyncHttpClient client = new AsyncHttpClient();
        String reviewUrl = String.format(REVIEW_URL, movie.getId());
        client.get(reviewUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "getReview onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "review results: " + results.toString());
                    reviews.addAll(Review.fromJSONArray(results));
                    reviewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "getReview hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "getReview onFailure");
            }
        });
    }
}