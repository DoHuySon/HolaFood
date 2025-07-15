package com.example.holafood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.adapter.ProductAdapter;
import com.example.holafood.model.Product;
import com.example.holafood.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvProducts = findViewById(R.id.rv_products);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter
        productAdapter = new ProductAdapter(productList, this);
        rvProducts.setAdapter(productAdapter);

        // Quan sát danh sách sản phẩm từ LiveData
        App.getDatabase().productDao().getAllProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if (products != null) {
                    productList.clear();
                    productList.addAll(products);
                    productAdapter.setProductList(productList); // Cập nhật danh sách trong Adapter
                }
            }
        });

        // Khởi tạo và xử lý menu icon
        menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_profile) {
                Toast.makeText(this, "Xem thông tin cá nhân", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                return true;
            } else if (item.getItemId() == R.id.menu_orders) {
                Toast.makeText(this, "Xem đơn hàng", Toast.LENGTH_SHORT).show();
                SessionManager sessionManager = new SessionManager(this);
                int userId = sessionManager.getUserId();
                if (userId != -1) {
                    Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Lỗi: Không xác định người dùng", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (item.getItemId() == R.id.menu_logout) {
                Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                SessionManager sessionManager = new SessionManager(this);
                sessionManager.logout(); // Xóa session
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa tất cả activity trước đó
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onViewDetailsClick(Product product) {
        Toast.makeText(this, "Xem chi tiết: " + product.getName() + " (ID: " + product.getProductId() + ")",
                Toast.LENGTH_SHORT).show();
        // Có thể chuyển sang DetailActivity
        // Intent intent = new Intent(this, DetailActivity.class);
        // intent.putExtra("productId", product.getProductId());
        // startActivity(intent);
    }
}