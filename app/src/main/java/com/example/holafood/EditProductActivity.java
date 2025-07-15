package com.example.holafood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.holafood.model.Product;
import com.example.holafood.model.ProductStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditProductActivity extends AppCompatActivity {
    private static final String TAG = "EditProductActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 100;

    private EditText editTextName, editTextDescription, editTextPrice, editTextImagePath;
    private Spinner spinnerStatus;
    private Button buttonSave, buttonSelectImage;
    private int productId;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Ánh xạ các view
        editTextName = findViewById(R.id.edit_text_name);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextImagePath = findViewById(R.id.edit_text_image_path);
        spinnerStatus = findViewById(R.id.spinner_status);
        buttonSave = findViewById(R.id.button_save);
        buttonSelectImage = findViewById(R.id.button_select_image);

        // Cài đặt Spinner
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll("Có sẵn", "Không có sẵn");
        spinnerStatus.setAdapter(adapter);

        // Khởi tạo ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        String imagePath = copyImageToInternalStorage(imageUri);
                        editTextImagePath.setText(imagePath != null ? imagePath : "");
                    }
                });

        // Lấy dữ liệu từ Intent
        productId = getIntent().getIntExtra("productId", -1);
        editTextName.setText(getIntent().getStringExtra("name"));
        editTextDescription.setText(getIntent().getStringExtra("description"));
        editTextPrice.setText(String.valueOf(getIntent().getDoubleExtra("price", 0.0)));
        editTextImagePath.setText(getIntent().getStringExtra("imagePath"));
        String currentStatus = getIntent().getStringExtra("status");
        if ("AVAILABLE".equals(currentStatus)) {
            spinnerStatus.setSelection(0); // "Có sẵn"
        } else if ("UNAVAILABLE".equals(currentStatus)) {
            spinnerStatus.setSelection(1); // "Không có sẵn"
        }

        buttonSelectImage.setOnClickListener(v -> requestPermissionsAndPickImage());
        buttonSave.setOnClickListener(v -> saveProduct());
    }

    private void requestPermissionsAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSIONS);
        } else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, "Quyền truy cập ảnh bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File storageDir = getFilesDir(); // Lưu vào bộ nhớ nội bộ
            String fileName = "product_" + System.currentTimeMillis() + ".jpg";
            File file = new File(storageDir, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error copying image", e);
            return null;
        }
    }

    private void saveProduct() {
        try {
            String name = editTextName.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            double price = Double.parseDouble(editTextPrice.getText().toString().trim());
            String imagePath = editTextImagePath.getText().toString().trim();
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
            updatedProduct.setImagePath(imagePath);
            updatedProduct.setStatus(status);
            updatedProduct.setUpdatedAt(System.currentTimeMillis());

            new Thread(() -> {
                App.getDatabase().productDao().updateProduct(updatedProduct);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error saving product", e);
            runOnUiThread(() -> Toast.makeText(this, "Cập nhật sản phẩm thất bại", Toast.LENGTH_SHORT).show());
        }
    }
}