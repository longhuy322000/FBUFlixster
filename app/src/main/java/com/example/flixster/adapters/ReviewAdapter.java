package com.example.flixster.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flixster.R;
import com.example.flixster.models.Movie;
import com.example.flixster.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    public static final String TAG = "ReviewAdapter";

    Context context;
    List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateHolder");
        View reviewView = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewAdapter.ViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Log.i(TAG, "onBind");
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvReviewContent;
        TextView getTvReviewAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewContent = itemView.findViewById(R.id.tvReviewContent);
            getTvReviewAuthor = itemView.findViewById(R.id.tvReviewAuthor);
        }

        public void bind(Review review) {
            if (review.getContent().length() > 500) {
                tvReviewContent.setText(review.getContent().substring(0, 500) + "...");
            }
            else {
                tvReviewContent.setText(review.getContent());
            }
            getTvReviewAuthor.setText(review.getAuthor());
        }
    }
}
