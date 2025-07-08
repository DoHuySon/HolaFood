package com.example.holafood;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.holafood.model.Product;
import com.example.holafood.model.ProductStatus;

public class EditProductActivity extends AppCompatActivity {
    private static final String TAG = "EditProductActivity";
    private EditText editTextName, editTextDescription, editTextPrice, editTextImageResourceName;
    private Spinner spinnerStatus;
    private Button buttonSave;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Ánh xạ các view
        editTextName = findViewById(R.id.edit_text_name);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextImageResourceName = findViewById(R.id.edit_text_image_resource_name);
        spinnerStatus = findViewById(R.id.spinner_status);
        buttonSave = findViewById(R.id.button_save);

        // Cài đặt Spinner
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll("Có sẵn", "Không có sẵn");
        spinnerStatus.setAdapter(adapter);

        // Lấy dữ liệu từ Intent
        productId = getIntent().getIntExtra("productId", -1);
        editTextName.setText(getIntent().getStringExtra("name"));
        editTextDescription.setText(getIntent().getStringExtra("description"));
        editTextPrice.setText(String.valueOf(getIntent().getDoubleExtra("price", 0.0)));
        editTextImageResourceName.setText(getIntent().getStringExtra("imageResourceName"));
        String currentStatus = getIntent().getStringExtra("status");
        if ("AVAILABLE".equals(currentStatus)) {
            spinnerStatus.setSelection(0); // "Có sẵn"
        } else if ("UNAVAILABLE".equals(currentStatus)) {
            spinnerStatus.setSelection(1); // "Không có sẵn"
        }

        buttonSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        try {
            String name = editTextName.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            double price = Double.parseDouble(editTextPrice.getText().toString().trim());
            String imageResourceName = editTextImageResourceName.getText().toString().trim();
            String selectedStatus = spinnerStatus.getSelectedItem().toString();
            ProductStatus status = "Có sẵn".equals(selectedStatus) ? ProductStatus.AVAILABLE : ProductStatus.UNAVAILABLE;

            if (name.isEmpty() || description.isEmpty() || price <= 0) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Product updatedProduct = new Product();
            updatedProduct.setProductId(productId);
            updatedProduct.setSellerId(getIntent().getIntExtra("sellerId", -1));
            updatedProduct.setName(name);
            updatedProduct.setDescription(description);
            updatedProduct.setPrice(price);
            updatedProduct.setImageResourceName(imageResourceName);
            updatedProduct.setStatus(status);
            updatedProduct.setUpdatedAt(System.currentTimeMillis());

            new Thread(() -> {
                App.getDatabase().productDao().updateProduct(updatedProduct);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại SellerMainActivity
                });
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error saving product", e);
            runOnUiThread(() -> Toast.makeText(this, "Cập nhật sản phẩm thất bại", Toast.LENGTH_SHORT).show());
        }
    }
}