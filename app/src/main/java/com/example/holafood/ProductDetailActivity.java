package com.example.holafood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.adapter.ReviewAdapter;
import com.example.holafood.model.Product;
import com.example.holafood.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    private ImageView imageView;
    private TextView nameView, priceView, statusView, descriptionView, reviewCountView;
    private Button orderButton;
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo các view
        imageView = findViewById(R.id.detail_image);
        nameView = findViewById(R.id.detail_name);
        priceView = findViewById(R.id.detail_price);
        statusView = findViewById(R.id.detail_status);
        descriptionView = findViewById(R.id.detail_description);
        orderButton = findViewById(R.id.btn_order);
        rvReviews = findViewById(R.id.rv_reviews);
        reviewCountView = findViewById(R.id.review_count); // TextView để hiển thị số lượng review

        // Kiểm tra database instance
        if (App.getDatabase() == null) {
            Log.e(TAG, "Database instance is null");
            Toast.makeText(this, "Lỗi: Không thể kết nối cơ sở dữ liệu", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Cấu hình RecyclerView
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        rvReviews.setAdapter(reviewAdapter);

        // Lấy productId từ Intent
        productId = getIntent().getIntExtra("productId", -1);
        Log.d(TAG, "Received productId: " + productId);
        if (productId == -1) {
            Log.e(TAG, "Invalid productId");
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thực hiện tải dữ liệu trên thread riêng
        new LoadProductTask().execute(productId);

        // Xử lý nút Đặt hàng
        orderButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
            intent.putExtra("productId", productId);
            startActivity(intent);
            Toast.makeText(ProductDetailActivity.this, "Chuyển đến trang thanh toán", Toast.LENGTH_SHORT).show();
        });
    }

    private class LoadProductTask extends AsyncTask<Integer, Void, Product> {
        @Override
        protected Product doInBackground(Integer... params) {
            try {
                return App.getDatabase().productDao().getProductByIdSync(params[0]);
            } catch (Exception e) {
                Log.e(TAG, "Error loading product: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Product product) {
            if (product == null) {
                Log.e(TAG, "Product not found for productId: " + productId);
                Toast.makeText(ProductDetailActivity.this, "Lỗi: Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Gán dữ liệu vào các view
            nameView.setText(product.getName());
            priceView.setText("Giá: " + product.getPrice() + " USD");
            statusView.setText("Trạng thái: " + (product.getStatus() != null ? product.getStatus().getDisplayName() : "Không xác định"));
            descriptionView.setText(product.getDescription() != null ? product.getDescription() : "Không có mô tả");

            // Xử lý hình ảnh
            if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(product.getImagePath());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.w(TAG, "Failed to decode image: " + product.getImagePath());
                    imageView.setImageResource(R.drawable.default_image);
                }
            } else {
                Log.w(TAG, "Image path is null or empty");
                imageView.setImageResource(R.drawable.default_image);
            }

            // Tải danh sách đánh giá
            new LoadReviewsTask().execute(product.getProductId());
        }
    }

    private class LoadReviewsTask extends AsyncTask<Integer, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(Integer... params) {
            try {
                List<Review> reviews = App.getDatabase().reviewDao().getReviewsByProductId(params[0]);
                Log.d(TAG, "Fetched reviews count: " + (reviews != null ? reviews.size() : 0));
                return reviews;
            } catch (Exception e) {
                Log.e(TAG, "Error loading reviews: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews == null) {
                Log.e(TAG, "Reviews list is null");
                if (reviewCountView != null) {
                    reviewCountView.setText("Không có đánh giá");
                }
                reviewAdapter.setReviewList(new ArrayList<>());
                return;
            }

            Log.d(TAG, "Updating reviews, count: " + reviews.size());
            if (reviewCountView != null) {
                reviewCountView.setText("Số đánh giá: " + reviews.size());
            }
            reviewAdapter.setReviewList(reviews);
            if (reviews.isEmpty()) {
                Log.d(TAG, "No reviews found for productId: " + productId);
                Toast.makeText(ProductDetailActivity.this, "Chưa có đánh giá nào", Toast.LENGTH_SHORT).show();
            }
        }
    }
}