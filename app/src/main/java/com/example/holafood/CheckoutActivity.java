package com.example.holafood;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.holafood.database.AppDatabase;
import com.example.holafood.model.Order;
import com.example.holafood.model.OrderItem;
import com.example.holafood.model.OrderStatus;
import com.example.holafood.model.PaymentMethod;
import com.example.holafood.model.Product;
import com.example.holafood.model.User;
import com.example.holafood.util.SessionManager;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";
    private AppDatabase db;
    private Product product;
    private User currentUser;

    private EditText editPhone, editAddress;
    private RadioButton radioCash, radioBank;
    private NumberPicker numberPicker;
    private TextView textTotalPrice;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = AppDatabase.getDatabase(this);

        // Lấy productId từ Intent
        int productId = getIntent().getIntExtra("productId", -1);
        if (productId != -1) {
            new Thread(() -> {
                product = db.productDao().getProductByIdSync(productId);
                runOnUiThread(() -> updateUI());
            }).start();
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Liên kết View
        editPhone = findViewById(R.id.edit_phone);
        editAddress = findViewById(R.id.edit_address);
        radioCash = findViewById(R.id.radio_cash);
        radioBank = findViewById(R.id.radio_bank);
        numberPicker = findViewById(R.id.number_picker_quantity);
        textTotalPrice = findViewById(R.id.text_total_price);
        btnCheckout = findViewById(R.id.button_checkout);

        // Cấu hình NumberPicker
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
        numberPicker.setValue(1);

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (product != null) {
                double total = newVal * product.getPrice();
                textTotalPrice.setText("Tổng: " + total + "đ");
            }
        });

        // Lấy user từ Session và gán vào EditText
        SessionManager sessionManager = new SessionManager(this);
        int userId = sessionManager.getUserId();

        new Thread(() -> {
            currentUser = db.userDao().getUserByIdNow(userId);
            runOnUiThread(() -> {
                if (currentUser != null) {
                    editPhone.setText(currentUser.getPhoneNumber());
                    editAddress.setText(currentUser.getAddress());
                }
            });
        }).start();

        // Bắt sự kiện Thanh toán
        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void updateUI() {
        if (product != null) {
            double total = product.getPrice() * numberPicker.getValue();
            textTotalPrice.setText("Tổng: " + total + "đ");
        } else {
            textTotalPrice.setText("Tổng: 0đ");
            Toast.makeText(this, "Lỗi: Không tải được sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCheckout() {
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        PaymentMethod paymentMethod = radioCash.isChecked() ? PaymentMethod.CASH : PaymentMethod.BANK_TRANSFER;
        int quantity = numberPicker.getValue();

        if (phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null || product == null) {
            Toast.makeText(this, "Không thể xác định người dùng hoặc sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount = product.getPrice() * quantity;

        Order order = new Order(
                currentUser.getUserId(),
                product.getSellerId(),
                totalAmount,
                address,
                phone,
                paymentMethod,
                OrderStatus.PLACED
        );

        new Thread(() -> {
            try {
                long orderId = db.orderDao().insertOrders(order);
                if (orderId != -1) {
                    OrderItem orderItem = new OrderItem((int) orderId, product.getProductId(), quantity, product.getPrice());
                    db.orderItemDao().insertOrderItem(orderItem);

                    // Lấy danh sách đơn hàng để log
                    List<Order> userOrders = db.orderDao().getOrdersByCustomerNow(currentUser.getUserId());
                    for (Order o : userOrders) {
                        Log.d(TAG, "OrderId: " + o.getOrderId()
                                + ", Tổng: " + o.getTotalAmount()
                                + ", Phương thức: " + o.getPaymentMethod()
                                + ", Trạng thái: " + o.getStatus()
                                + ", Ngày: " + o.getCreatedAt());
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình trước
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: Không thể tạo đơn hàng", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in checkout: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi xử lý đơn hàng", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}