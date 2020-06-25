package com.example.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Review {

    String content;
    String author;

    public Review(JSONObject jsonObject) throws JSONException {
        this.content = jsonObject.getString("content");
        this.author = jsonObject.getString("author");
    }

    public static List<Review> fromJSONArray(JSONArray results) throws JSONException {
        List<Review> reviews = new ArrayList<>();
        for (int i=0; i<results.length(); i++) {
            reviews.add(new Review(results.getJSONObject(i)));
        }
        return reviews;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }
}
