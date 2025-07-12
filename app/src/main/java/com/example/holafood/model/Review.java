package com.example.holafood.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Reviews",
        foreignKeys = {
                @ForeignKey(entity = Product.class,
                        parentColumns = "product_id",
                        childColumns = "product_id",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "customer_id",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Order.class,
                        parentColumns = "order_id",
                        childColumns = "order_id",
                        onDelete = ForeignKey.SET_NULL)
        })
public class Review {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "review_id")
    private int reviewId;

    @ColumnInfo(name = "product_id")
    private Integer productId;

    @ColumnInfo(name = "customer_id")
    private Integer customerId;

    @ColumnInfo(name = "order_id")
    private Integer orderId;

    @ColumnInfo(name = "rating")
    private int rating;

    @ColumnInfo(name = "comment")
    private String comment;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public Review() {
        this.createdAt = System.currentTimeMillis();
    }

    public Review(Integer productId, Integer customerId, Integer orderId, int rating, String comment) {
        this.productId = productId;
        this.customerId = customerId;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = System.currentTimeMillis();
    }

    // Getter và Setter
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
