package com.example.holafood;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.holafood.model.Order;
import com.example.holafood.model.OrderItem;
import com.example.holafood.model.Product;
import com.example.holafood.model.Review;
import com.example.holafood.util.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class ProductReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderReviewAdapter adapter;
    private SessionManager sessionManager;
    private List<Order> deliveredOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_review);

        // Ánh xạ RecyclerView
        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);

        // Kiểm tra trạng thái đăng nhập
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải danh sách đơn hàng DELIVERED
        App.getDatabase().orderDao().getDeliveredOrdersByCustomer(sessionManager.getUserId(), "DELIVERED").observe(this, orders -> {
            if (orders != null && !orders.isEmpty()) {
                deliveredOrders = orders;
                adapter = new OrderReviewAdapter(orders);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Không có đơn hàng nào hoàn tất để đánh giá", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private class OrderReviewAdapter extends RecyclerView.Adapter<OrderReviewAdapter.OrderViewHolder> {
        private List<Order> orders;

        public OrderReviewAdapter(List<Order> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_review, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = orders.get(position);
            holder.textOrderId.setText("Đơn hàng #" + order.getOrderId());
            new LoadOrderItemsTask(holder).execute(order.getOrderId());
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView textOrderId;
            RecyclerView recyclerViewItems;

            OrderViewHolder(View itemView) {
                super(itemView);
                textOrderId = itemView.findViewById(R.id.text_order_id);
                recyclerViewItems = itemView.findViewById(R.id.recycler_view_order_items);
                recyclerViewItems.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            }
        }
    }

    private class LoadOrderItemsTask extends AsyncTask<Integer, Void, List<OrderItem>> {
        private OrderReviewAdapter.OrderViewHolder holder;

        LoadOrderItemsTask(OrderReviewAdapter.OrderViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected List<OrderItem> doInBackground(Integer... orderIds) {
            return App.getDatabase().orderItemDao().getOrderItemsByOrder(orderIds[0]).getValue();
        }

        @Override
        protected void onPostExecute(List<OrderItem> items) {
            if (items != null && !items.isEmpty()) {
                ProductReviewAdapter adapter = new ProductReviewAdapter(items, sessionManager.getUserId(), deliveredOrders.get(holder.getAdapterPosition()));
                holder.recyclerViewItems.setAdapter(adapter);
            }
        }
    }

    private class ProductReviewAdapter extends RecyclerView.Adapter<ProductReviewAdapter.ViewHolder> {
        private List<OrderItem> orderItems;
        private int customerId;
        private Order order;

        public ProductReviewAdapter(List<OrderItem> orderItems, int customerId, Order order) {
            this.orderItems = orderItems;
            this.customerId = customerId;
            this.order = order;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_review, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrderItem orderItem = orderItems.get(position);
            new LoadProductTask(holder).execute(orderItem.getProductId());
            new CheckReviewTask(holder).execute(orderItem.getProductId());
        }

        @Override
        public int getItemCount() {
            return orderItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageProduct;
            TextView textProductName;
            RatingBar ratingBar;
            EditText editComment;
            Button buttonSubmitReview;

            ViewHolder(View itemView) {
                super(itemView);
                imageProduct = itemView.findViewById(R.id.image_product);
                textProductName = itemView.findViewById(R.id.text_product_name);
                ratingBar = itemView.findViewById(R.id.rating_bar);
                editComment = itemView.findViewById(R.id.edit_comment);
                buttonSubmitReview = itemView.findViewById(R.id.button_submit_review);
            }
        }

        private class LoadProductTask extends AsyncTask<Integer, Void, Product> {
            private ViewHolder holder;

            LoadProductTask(ViewHolder holder) {
                this.holder = holder;
            }

            @Override
            protected Product doInBackground(Integer... productIds) {
                return App.getDatabase().productDao().getProductById(productIds[0]).getValue();
            }

            @Override
            protected void onPostExecute(Product product) {
                if (product != null) {
                    holder.textProductName.setText(product.getName());
                    if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(product.getImagePath());
                        if (bitmap != null) {
                            holder.imageProduct.setImageBitmap(bitmap);
                        } else {
                            holder.imageProduct.setImageResource(R.drawable.default_image);
                        }
                    } else {
                        holder.imageProduct.setImageResource(R.drawable.default_image);
                    }
                }
            }
        }

        private class CheckReviewTask extends AsyncTask<Integer, Void, Review> {
            private ViewHolder holder;

            CheckReviewTask(ViewHolder holder) {
                this.holder = holder;
            }

            @Override
            protected Review doInBackground(Integer... productIds) {
                return App.getDatabase().reviewDao().getReviewByOrderAndCustomer(order.getOrderId(), customerId, productIds[0]);
            }

            @Override
            protected void onPostExecute(Review review) {
                if (review != null) {
                    holder.ratingBar.setRating(review.getRating());
                    holder.editComment.setText(review.getComment());
                    holder.ratingBar.setEnabled(false);
                    holder.editComment.setEnabled(false);
                    holder.buttonSubmitReview.setEnabled(false);
                    holder.buttonSubmitReview.setText("Đã đánh giá");
                } else {
                    holder.buttonSubmitReview.setOnClickListener(v -> {
                        float rating = holder.ratingBar.getRating();
                        if (rating == 0) {
                            Toast.makeText(ProductReviewActivity.this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Review newReview = new Review();
                        newReview.setProductId(orderItems.get(holder.getAdapterPosition()).getProductId());
                        newReview.setCustomerId(customerId);
                        newReview.setOrderId(order.getOrderId());
                        newReview.setRating((int) rating);
                        newReview.setComment(holder.editComment.getText().toString().trim());
                        newReview.setCreatedAt(System.currentTimeMillis());
                        new SaveReviewTask(holder).execute(newReview);
                    });
                }
            }
        }

        private class SaveReviewTask extends AsyncTask<Review, Void, Void> {
            private ViewHolder holder;

            SaveReviewTask(ViewHolder holder) {
                this.holder = holder;
            }

            @Override
            protected Void doInBackground(Review... reviews) {
                App.getDatabase().reviewDao().insertReview(reviews[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(ProductReviewActivity.this, "Đánh giá đã được lưu", Toast.LENGTH_SHORT).show();
                holder.ratingBar.setEnabled(false);
                holder.editComment.setEnabled(false);
                holder.buttonSubmitReview.setEnabled(false);
                holder.buttonSubmitReview.setText("Đã đánh giá");
            }
        }
    }
}