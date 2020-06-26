package com.example.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Parcel
public class Movie {

    public static String posterSize;
    public static String backdropSize;
    String id;
    String posterPath;
    String backdropPath;
    String title;
    String overview;
    Double voteAverage;
    Double popularity;
    String releaseDate;

    public Movie() {}

    public Movie(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        voteAverage = jsonObject.getDouble("vote_average");
        popularity = jsonObject.getDouble("popularity");
        releaseDate = jsonObject.getString("release_date");
    }

    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i=0; i<movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    public String getId() {
        return id;
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/%s/%s", posterSize, posterPath);
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/%s/%s", backdropSize, backdropPath);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Double getPopularity() {
        return popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public static class MovieVoteRatingComparator implements Comparator<Movie> {
        public int compare(Movie a, Movie b) {
            if (a.getVoteAverage() >= b.getVoteAverage()){
                return -1;
            }
            else {
                return 1;
            }
        }
    }

    public static class MoviePopularityComparator implements Comparator<Movie> {
        public int compare(Movie a, Movie b) {
            if (a.getPopularity() >= b.getPopularity()){
                return -1;
            }
            else {
                return 1;
            }
        }
    }

    public static class MovieReleaseDateComparator implements Comparator<Movie> {
        public int compare(Movie a, Movie b) {
            return a.getReleaseDate().compareTo(b.getReleaseDate()) * (-1);
        }
    }
}
