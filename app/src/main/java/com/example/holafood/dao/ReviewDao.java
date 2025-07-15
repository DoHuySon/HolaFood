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
    long insertReview(Review review);

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
    @Query("SELECT * FROM Reviews WHERE order_id = :orderId AND customer_id = :customerId AND product_id = :productId")
    Review getReviewByOrderAndCustomer(int orderId, int customerId, int productId);
    @Query("SELECT * FROM Reviews WHERE product_id = :productId")
    List<Review> getReviewsByProductId(int productId);
}
