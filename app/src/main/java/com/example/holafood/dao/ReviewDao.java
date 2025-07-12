package com.example.holafood.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.holafood.model.Review;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insertReview(Review review);

    @Insert
    void insertReviews(List<Review> reviews);

    @Update
    void updateReview(Review review);

    @Query("DELETE FROM Reviews WHERE review_id = :reviewId")
    void deleteReview(int reviewId);

    @Query("SELECT * FROM Reviews WHERE product_id = :productId")
    LiveData<List<Review>> getReviewsByProduct(int productId);

    @Query("SELECT * FROM Reviews WHERE customer_id = :customerId")
    LiveData<List<Review>> getReviewsByCustomer(int customerId);
}
