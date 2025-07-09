package com.example.holafood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.holafood.util.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "AddProductActivity";
    private EditText editTextName, editTextDescription, editTextPrice, editTextImagePath;
    private Spinner spinnerStatus;
    private Button buttonSelectImage, buttonSave;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private Uri selectedImageUri;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Log.d(TAG, "onCreate: Khởi tạo AddProductActivity");

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);
        Log.d(TAG, "Seller ID: " + sessionManager.getUserId());

        // Ánh xạ các thành phần giao diện
        editTextName = findViewById(R.id.edit_text_name);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextImagePath = findViewById(R.id.edit_text_image_path);
        spinnerStatus = findViewById(R.id.spinner_status);
        buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSave = findViewById(R.id.button_save);

        // Cấu hình Spinner cho trạng thái
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll("Có sẵn", "Không có sẵn");
        spinnerStatus.setAdapter(adapter);

        // Xử lý chọn ảnh
        ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "pickImageLauncher: Kết quả nhận được");
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            String imagePath = copyImageToInternalStorage(selectedImageUri);
                            editTextImagePath.setText(imagePath != null ? imagePath : "");
                            Log.d(TAG, "Đường dẫn ảnh: " + editTextImagePath.getText().toString());
                        }
                    } else {
                        Log.d(TAG, "Không chọn được ảnh hoặc kết quả null");
                    }
                });

        buttonSelectImage.setOnClickListener(v -> {
            Log.d(TAG, "buttonSelectImage: Kiểm tra quyền");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Yêu cầu quyền READ_MEDIA_IMAGES");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openImagePicker(pickImageLauncher);
            }
        });

        // Xử lý sự kiện lưu sản phẩm
        buttonSave.setOnClickListener(v -> saveProduct());
    }

    private void openImagePicker(ActivityResultLauncher<Intent> launcher) {
        Log.d(TAG, "openImagePicker: Mở trình chọn ảnh");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Đảm bảo mở file có thể đọc
        try {
            launcher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        } catch (Exception e) {
            Log.e(TAG, "Lỗi mở trình chọn ảnh", e);
            Toast.makeText(this, "Không thể mở trình chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: Kết quả quyền");
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker(registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                selectedImageUri = result.getData().getData();
                                if (selectedImageUri != null) {
                                    String imagePath = copyImageToInternalStorage(selectedImageUri);
                                    editTextImagePath.setText(imagePath != null ? imagePath : "");
                                    Log.d(TAG, "URI ảnh sau cấp quyền: " + editTextImagePath.getText().toString());
                                }
                            }
                        }));
            } else {
                Log.d(TAG, "Quyền bị từ chối");
                Toast.makeText(this, "Quyền truy cập ảnh bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveProduct() {
        Log.d(TAG, "saveProduct: Bắt đầu lưu sản phẩm");
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String imagePath = editTextImagePath.getText().toString().trim();
        String statusStr = spinnerStatus.getSelectedItem().toString();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || imagePath.isEmpty()) {
            Log.d(TAG, "Dữ liệu không đầy đủ");
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            ProductStatus status = "Có sẵn".equals(statusStr) ? ProductStatus.AVAILABLE : ProductStatus.UNAVAILABLE;

            int sellerId = sessionManager.getUserId();
            if (sellerId == -1) {
                Log.e(TAG, "Seller ID không hợp lệ");
                Toast.makeText(this, "Chưa đăng nhập hoặc seller ID không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Product product = new Product(
                    sellerId,
                    name,
                    description,
                    price,
                    imagePath,
                    status
            );

            new Thread(() -> {
                try {
                    Log.d(TAG, "Thực hiện insert vào database");
                    App.getDatabase().productDao().insertProduct(product);
                    runOnUiThread(() -> {
                        Log.d(TAG, "Lưu thành công");
                        Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi lưu vào database", e);
                    runOnUiThread(() -> {
                        Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();

        } catch (NumberFormatException e) {
            Log.e(TAG, "Giá không hợp lệ", e);
            Toast.makeText(this, "Giá phải là số hợp lệ", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Trạng thái không hợp lệ", e);
            Toast.makeText(this, "Trạng thái không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    // Thêm phương thức từ EditProductActivity
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
}