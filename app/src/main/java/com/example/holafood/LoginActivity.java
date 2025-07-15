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
import com.example.holafood.model.RevenueStat;
import com.example.holafood.model.Review;
import com.example.holafood.model.Role;
import com.example.holafood.model.StoreStatus;
import com.example.holafood.model.User;
import com.example.holafood.util.SessionManager;

import java.util.Date;

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
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }
//
private void insertSampleData() {
    new Thread(() -> {
        // Kiểm tra xem dữ liệu đã tồn tại chưa
        User existingUser = App.getDatabase().userDao().getUserByPhoneAndPassword("1234567890", "123");
        if (existingUser == null) {
            // Chèn người dùng mẫu
            User user1 = new User(
                    "user1@example.com",
                    "123",
                    "Nguyen Van A",
                    "1234567890",
                    "123 Đường ABC, TP.HCM",
                    Role.CUSTOMER,
                    null,
                    null,
                    StoreStatus.PENDING
            );
            User user2 = new User(
                    "seller1@example.com",
                    "123",
                    "Le Thi B",
                    "0987654321",
                    "456 Đường XYZ, TP.HCM",
                    Role.SELLER,
                    "Quán B",
                    "Mô tả quán ăn ngon",
                    StoreStatus.ACTIVE
            );
            long user1Id = App.getDatabase().userDao().insertUser(user1); // Lấy ID sau khi chèn
            long user2Id = App.getDatabase().userDao().insertUser(user2);
            user1.setUserId((int) user1Id); // Cập nhật ID cho user1
            user2.setUserId((int) user2Id); // Cập nhật ID cho user2

            // Chèn sản phẩm mẫu
            Product product1 = new Product(
                    user2.getUserId(),
                    "Pizza",
                    "Pizza ngon",
                    10.99,
                    "/storage/emulated/0/pictures/pizza.jpg",
                    ProductStatus.AVAILABLE
            );
            Product product2 = new Product(
                    user2.getUserId(),
                    "Hamburger",
                    "Hamburger giòn",
                    5.99,
                    "/storage/emulated/0/pictures/hamburger.jpg",
                    ProductStatus.AVAILABLE
            );
            long product1Id = App.getDatabase().productDao().insertProduct(product1);
            long product2Id = App.getDatabase().productDao().insertProduct(product2);
            product1.setProductId((int) product1Id);
            product2.setProductId((int) product2Id);

            // Chèn đơn hàng mẫu
            Order order1 = new Order(
                    user1.getUserId(),
                    user2.getUserId(),
                    50.0,
                    "123 Đường ABC, TP.HCM",
                    "0901234567",
                    PaymentMethod.CASH,
                    OrderStatus.PLACED
            );
            Order order2 = new Order(
                    user1.getUserId(),
                    user2.getUserId(),
                    75.0,
                    "456 Đường XYZ, TP.HCM",
                    "0912345678",
                    PaymentMethod.BANK_TRANSFER,
                    OrderStatus.PROCESSING
            );
            long order1Id = App.getDatabase().orderDao().insertOrder(order1);
            long order2Id = App.getDatabase().orderDao().insertOrder(order2);
            order1.setOrderId((int) order1Id);
            order2.setOrderId((int) order2Id);

            // Chèn mục đơn hàng mẫu
            OrderItem orderItem1 = new OrderItem(
                    order1.getOrderId(),
                    product1.getProductId(),
                    3,
                    10.99
            );
            OrderItem orderItem2 = new OrderItem(
                    order1.getOrderId(),
                    product2.getProductId(),
                    5,
                    5.99
            );
            OrderItem orderItem3 = new OrderItem(
                    order2.getOrderId(),
                    product1.getProductId(),
                    2,
                    10.99
            );
            App.getDatabase().orderItemDao().insertOrderItem(orderItem1);
            App.getDatabase().orderItemDao().insertOrderItem(orderItem2);
            App.getDatabase().orderItemDao().insertOrderItem(orderItem3);

            // Chèn đánh giá mẫu
            Review review1 = new Review(
                    product1.getProductId(),
                    user1.getUserId(),
                    order1.getOrderId(),
                    4,
                    "Rất ngon, sẽ đặt lại!"
            );
            Review review2 = new Review(
                    product2.getProductId(),
                    user1.getUserId(),
                    order1.getOrderId(),
                    5,
                    "Hamburger tuyệt vời!"
            );
            App.getDatabase().reviewDao().insertReview(review1);
            App.getDatabase().reviewDao().insertReview(review2);

            // Chèn thống kê doanh thu mẫu
            RevenueStat revenueStat1 = new RevenueStat(
                    user2.getUserId(),
                    125.0,
                    new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000), // Tuần trước
                    new Date(System.currentTimeMillis()) // Hôm nay
            );
            RevenueStat revenueStat2 = new RevenueStat(
                    user2.getUserId(),
                    200.0,
                    new Date(System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000), // Hai tuần trước
                    new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000) // Tuần trước
            );
            App.getDatabase().revenueStatDao().insertRevenueStat(revenueStat1);
            App.getDatabase().revenueStatDao().insertRevenueStat(revenueStat2);

        }
        runOnUiThread(() -> {
            // Cập nhật UI nếu cần
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
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Số điện thoại hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        }
    }
}