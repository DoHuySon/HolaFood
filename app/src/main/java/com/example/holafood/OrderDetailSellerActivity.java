package com.example.holafood;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.adapter.OrderItemSellerAdapter;
import com.example.holafood.dao.OrderItemDao;
import com.example.holafood.dao.ProductDao;
import com.example.holafood.model.OrderItem;

import java.util.List;

public class OrderDetailSellerActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailSellerActivity";
    private RecyclerView recyclerView;
    private OrderItemDao orderItemDao;
    private ProductDao productDao;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_seller);

        orderId = getIntent().getIntExtra("orderId", -1);
        recyclerView = findViewById(R.id.recycler_view_order_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderItemDao = App.getDatabase().orderItemDao();
        productDao = App.getDatabase().productDao();

        // Sử dụng LiveData để quan sát thay đổi
        orderItemDao.getOrderItemsByOrder(orderId).observe(this, new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(List<OrderItem> orderItems) {
                if (orderItems != null && !orderItems.isEmpty()) {
                    OrderItemSellerAdapter adapter = new OrderItemSellerAdapter(OrderDetailSellerActivity.this, orderItems, productDao);
                    recyclerView.setAdapter(adapter);
                    Log.d(TAG, "Đã tải " + orderItems.size() + " mục đơn hàng");
                } else {
                    Log.d(TAG, "Không có dữ liệu OrderItem cho orderId: " + orderId);
                }
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
}