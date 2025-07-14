package com.example.holafood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.adapter.ProductSellerAdapter;
import com.example.holafood.model.Product;
import com.example.holafood.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class SellerMainActivity extends AppCompatActivity implements ProductSellerAdapter.OnProductActionListener {
    private static final String TAG = "SellerMainActivity";
    private SessionManager sessionManager;
    private RecyclerView recyclerViewProducts;
    private ProductSellerAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_seller_main);

            sessionManager = new SessionManager(this);

            if (sessionManager.getRole() != null && !sessionManager.getRole().equals(com.example.holafood.model.Role.SELLER)) {
                Toast.makeText(this, "Bạn không có quyền truy cập màn hình này", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            recyclerViewProducts = findViewById(R.id.recycler_view_products);
            recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
            productList = new ArrayList<>();
            productAdapter = new ProductSellerAdapter(productList, this);
            recyclerViewProducts.setAdapter(productAdapter);

            loadProducts();

            ImageButton buttonMenu = findViewById(R.id.button_menu);
            buttonMenu.setOnClickListener(v -> showPopupMenu(v));
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Lỗi khởi tạo màn hình", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProducts() {
        int sellerId = sessionManager.getUserId();
        //Log.d(TAG, "Seller ID: " + sellerId);
        LiveData<List<Product>> productsLiveData = App.getDatabase().productDao().getProductsBySeller(sellerId);

        productsLiveData.observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                //Log.d(TAG, "onChanged called with products: " + (products != null ? products.size() : "null"));
                productList.clear();
                if (products != null && !products.isEmpty()) {
                    productList.addAll(products);
                    Log.d(TAG, "Số sản phẩm: " + products.size());
                } else {
                    //Log.d(TAG, "Không có sản phẩm nào");
                    Toast.makeText(SellerMainActivity.this, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
                }
                productAdapter.updateProducts(productList);
                recyclerViewProducts.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.seller_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_add_product) {
                Toast.makeText(this, "Tạo sản phẩm", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SellerMainActivity.this, AddProductActivity.class));
                return true;
            } else if (item.getItemId() == R.id.menu_view_orders) {
                Toast.makeText(this, "Danh sách đặt đồ ăn", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SellerMainActivity.this, OrderListSellerActivity.class));
                return true;
            } else if (item.getItemId() == R.id.menu_view_revenue) {
                Toast.makeText(this, "Thống kê doanh thu", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SellerMainActivity.this, RevenueStatActivity.class);
                intent.putExtra("sellerId", getCurrentSellerId()); // Thay bằng logic lấy sellerId thực tế
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private int getCurrentSellerId() {
        // Logic lấy sellerId từ session, SharedPreferences, hoặc database
        // Ví dụ tạm thời
        return 1; // Thay bằng giá trị thực tế
    }

    @Override
    public void onEditProduct(Product product) {
        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("productId", product.getProductId());
        intent.putExtra("sellerId", product.getSellerId());
        intent.putExtra("name", product.getName());
        intent.putExtra("description", product.getDescription());
        intent.putExtra("price", product.getPrice());
        intent.putExtra("imageResourceName", product.getImagePath());
        intent.putExtra("status", product.getStatus().name());
        startActivity(intent);
    }

    @Override
    public void onDeleteProduct(int productId) {
        new Thread(() -> {
            try {
                App.getDatabase().productDao().deleteProduct(productId);
                runOnUiThread(() -> {
                    Toast.makeText(SellerMainActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    loadProducts();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(SellerMainActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}