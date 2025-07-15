package com.example.holafood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.R;
import com.example.holafood.model.Review;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.textRating.setText("Đánh giá: " + review.getRating() + "/5");
        holder.textComment.setText("Bình luận: " + (review.getComment() != null ? review.getComment() : "Không có"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.textDate.setText("Ngày: " + sdf.format(review.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
        notifyDataSetChanged();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView textRating, textComment, textDate;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textRating = itemView.findViewById(R.id.text_rating);
            textComment = itemView.findViewById(R.id.text_comment);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
}