package com.example.holafood;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.holafood.model.Order;
import com.example.holafood.model.OrderItem;
import com.example.holafood.model.OrderStatus;
import com.example.holafood.model.PaymentMethod;
import com.example.holafood.model.Product;
import com.example.holafood.model.ProductStatus;
import com.example.holafood.model.Review;
import com.example.holafood.model.Role;
import com.example.holafood.model.StoreStatus;
import com.example.holafood.model.User;
import com.example.holafood.util.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText editPhoneNumber, editPassword;
    private Button buttonLogin;
    private SessionManager sessionManager;

    private void onBindingView(){
        editPhoneNumber = findViewById(R.id.edit_phone_number);
        editPassword = findViewById(R.id.edit_password);
        buttonLogin = findViewById(R.id.button_login);
    }
    private void onBindingAction(){
        buttonLogin.setOnClickListener(this:: onClickLogin);
    }

    private void onClickLogin(View view) {
        String phoneNumber = editPhoneNumber.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập số điện thoại và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra đăng nhập
        new LoginTask().execute(phoneNumber, password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        insertSampleData();
        //insertSampleData2();
        onBindingView();
        onBindingAction();

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);

        // Kiểm tra nếu đã đăng nhập
        if (sessionManager.isLoggedIn()) {
            if(sessionManager.getRole() == Role.ADMIN){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else if (sessionManager.getRole() == Role.SELLER) {
                startActivity(new Intent(this, SellerMainActivity.class));
                finish();
            }else {
                startActivity(new Intent(this, ProductReviewActivity.class));
                finish();
            }
        }
    }

    private void insertSampleData() {
        new Thread(() -> {
            // Kiểm tra xem dữ liệu đã tồn tại chưa
            User existingUser = App.getDatabase().userDao().getUserByPhoneAndPassword("12345678", "123");
            if (existingUser == null) {
                // Chèn dữ liệu mẫu cho Users (giữ nguyên user1 và user2, sửa user3)
                User user1 = new User(
                        "user1@example.com", "123", "Nguyen Van A", "1234567890", "123 Đường ABC, TP.HCM",
                        Role.CUSTOMER, null, null, StoreStatus.PENDING
                );
                User user2 = new User(
                        "seller1@example.com", "123", "Le Thi B", "0987654321", "456 Đường XYZ, TP.HCM",
                        Role.SELLER, "Quán B", "Mô tả quán ăn ngon", StoreStatus.ACTIVE
                );
                User user3 = new User(
                        "customer2@example.com", // Email sửa để tránh trùng
                        "123", "Le Thi C", "12345678", "456 Đường XYZ, TP.HCM",
                        Role.CUSTOMER, null, null, StoreStatus.PENDING
                );
                App.getDatabase().userDao().insertUser(user1);
                App.getDatabase().userDao().insertUser(user2);
                App.getDatabase().userDao().insertUser(user3);
                int userId1 = 1;
                int userId2 = 2;
                int userId3 = 3;

                // Chèn dữ liệu mẫu cho Products (giữ nguyên)
                Product product1 = new Product(userId2, "Pizza", "Pizza ngon", 10.99, "pizza_son", ProductStatus.AVAILABLE);
                Product product2 = new Product(userId2, "Hamburger", "Hamburger giòn", 5.99, "hamburger_son", ProductStatus.AVAILABLE);
                App.getDatabase().productDao().insertProduct(product1);
                App.getDatabase().productDao().insertProduct(product2);
                int productId1 = 1;
                int productId2 = 2;

                // Chèn dữ liệu mẫu cho Orders
                Order order1 = new Order(
                        userId3, // customerId
                        userId2, // sellerId
                        22.97, // 1 Pizza (10.99) + 2 Hamburger (2 * 5.99)
                        "456 Đường XYZ, TP.HCM",
                        "12345678",
                        PaymentMethod.BANK_TRANSFER,
                        OrderStatus.DELIVERED
                );
                Order order2 = new Order(
                        userId3, // customerId
                        userId2, // sellerId
                        16.98, // 1 Pizza (10.99) + 1 Hamburger (5.99)
                        "456 Đường XYZ, TP.HCM",
                        "12345678",
                        PaymentMethod.CASH,
                        OrderStatus.DELIVERED
                );
                App.getDatabase().orderDao().insertOrder(order1);
                App.getDatabase().orderDao().insertOrder((order2));
                int orderId1 = 1;
                int orderId2 = 2;

                // Chèn dữ liệu mẫu cho Order_Items
                OrderItem orderItem1 = new OrderItem(orderId1, productId1, 1, 10.99); // 1 Pizza
                OrderItem orderItem2 = new OrderItem(orderId1, productId2, 2, 5.99);  // 2 Hamburger
                OrderItem orderItem3 = new OrderItem(orderId2, productId1, 1, 10.99); // 1 Pizza
                OrderItem orderItem4 = new OrderItem(orderId2, productId2, 1, 5.99);  // 1 Hamburger

                App.getDatabase().orderItemDao().insertOrderItem(orderItem1);
                App.getDatabase().orderItemDao().insertOrderItem(orderItem2);
                App.getDatabase().orderItemDao().insertOrderItem(orderItem3);
                App.getDatabase().orderItemDao().insertOrderItem(orderItem4);

                // Chèn dữ liệu mẫu cho Reviews (chỉ cho đơn hàng DELIVERED)
                Review review1 = new Review(productId1, userId3, orderId1, 4, "Pizza rất ngon, giao hàng nhanh!");
                Review review2 = new Review(productId2, userId3, orderId1, 3, "Hamburger ổn, nhưng hơi khô.");

                App.getDatabase().reviewDao().insertReview(review1);
                App.getDatabase().reviewDao().insertReview(review2);
            }

            // Cập nhật UI trên main thread
            runOnUiThread(() -> {
                // Không cần thông báo, nhưng có thể thêm Toast nếu muốn
                // Toast.makeText(LoginActivity.this, "Dữ liệu mẫu đã được chèn", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private class LoginTask extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... params) {
            String phoneNumber = params[0];
            String password = params[1];
            return App.getDatabase().userDao().getUserByPhoneAndPassword(phoneNumber, password);
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                // Đăng nhập thành công, lưu thông tin vào SharedPreferences
                sessionManager.createLoginSession(user);
                if(user.getRole() == Role.ADMIN){
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else if (user.getRole() == Role.SELLER) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, SellerMainActivity.class));
                }else{

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                }
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Số điện thoại hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        }
    }
}