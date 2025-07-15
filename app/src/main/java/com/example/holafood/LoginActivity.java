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
                        "123", // Password dạng văn bản thô
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

                App.getDatabase().userDao().insertUser(user1);
                App.getDatabase().userDao().insertUser(user2);
                Product product1 = new Product(2, "Pizza", "Pizza ngon", 10.99, "/storage/emulated/0/pictures/pizza_son", ProductStatus.AVAILABLE);
                Product product2 = new Product(2, "Hamburger", "Hamburger giòn", 5.99, "/storage/emulated/0/pictures/hamburger_son", ProductStatus.AVAILABLE);
                App.getDatabase().productDao().insertProduct(product1);
                App.getDatabase().productDao().insertProduct(product2);
                Order order1 = new Order(1, 2, 50.0, "123 ABC", "0901234567", PaymentMethod.CASH, OrderStatus.PLACED);
                Order order2 = new Order(2, 2, 75.0, "456 XYZ", "0912345678", PaymentMethod.BANK_TRANSFER, OrderStatus.PROCESSING);
                App.getDatabase().orderDao().insertOrder(order1);
                App.getDatabase().orderDao().insertOrder(order2);
                OrderItem orderItem1 = new OrderItem(1, 1, 3, 10.99); // order1 với product1, số lượng 3
                OrderItem orderItem2 = new OrderItem(1, 2, 5, 5.99);  // order2 với product2, số lượng 5
                App.getDatabase().orderItemDao().insertOrderItem(orderItem1);
                App.getDatabase().orderItemDao().insertOrderItem(orderItem2);
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
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Số điện thoại hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        }
    }
}