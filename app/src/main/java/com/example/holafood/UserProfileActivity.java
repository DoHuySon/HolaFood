package com.example.holafood;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.holafood.model.Role;
import com.example.holafood.model.StoreStatus;
import com.example.holafood.model.User;
import com.example.holafood.util.SessionManager;

public class UserProfileActivity extends AppCompatActivity {

    private EditText editUserId, editEmail, editPassword, editFullName, editPhoneNumber, editAddress;
    private EditText editStoreName, editStoreDescription, editStoreStatus;
    private Button buttonEdit, buttonSave, buttonCancel;
    private LinearLayout layoutEditButtons;
    private User user;
    private boolean isEditing = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Khởi tạo các view
        editUserId = findViewById(R.id.edit_user_id);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editFullName = findViewById(R.id.edit_full_name);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
        editAddress = findViewById(R.id.edit_address);
        editStoreName = findViewById(R.id.edit_store_name);
        editStoreDescription = findViewById(R.id.edit_store_description);
        editStoreStatus = findViewById(R.id.edit_store_status);
        buttonEdit = findViewById(R.id.button_edit);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);
        layoutEditButtons = findViewById(R.id.layout_edit_buttons);

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);

        // Kiểm tra trạng thái đăng nhập
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy thông tin cơ bản từ SessionManager và tải đầy đủ từ Room
        new LoadUserTask().execute();

        // Xử lý sự kiện nút "Chỉnh Sửa"
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditing(true);
                buttonEdit.setVisibility(View.GONE);
                layoutEditButtons.setVisibility(View.VISIBLE);
            }
        });

        // Xử lý sự kiện nút "Lưu"
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                enableEditing(false);
                buttonEdit.setVisibility(View.VISIBLE);
                layoutEditButtons.setVisibility(View.GONE);
                Toast.makeText(UserProfileActivity.this, "Thông tin đã được lưu", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện nút "Hủy"
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayUserInfo();
                enableEditing(false);
                buttonEdit.setVisibility(View.VISIBLE);
                layoutEditButtons.setVisibility(View.GONE);
            }
        });
    }

    private void displayUserInfo() {
        if (user == null) return;

        // Kiểm tra role là Customer để ẩn các trường store
        if (user.getRole() == Role.CUSTOMER) {
            editUserId.setText(String.valueOf(user.getUserId()));
            editEmail.setText(user.getEmail() != null ? user.getEmail() : "");
            editPassword.setText(user.getPassword() != null ? user.getPassword() : "");
            editFullName.setText(user.getFullName() != null ? user.getFullName() : "");
            editPhoneNumber.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            editAddress.setText(user.getAddress() != null ? user.getAddress() : "");
            editStoreName.setVisibility(View.GONE);
            editStoreDescription.setVisibility(View.GONE);
            editStoreStatus.setVisibility(View.GONE);
        } else {
            editUserId.setText(String.valueOf(user.getUserId()));
            editEmail.setText(user.getEmail() != null ? user.getEmail() : "");
            editPassword.setText(user.getPassword() != null ? user.getPassword() : "");
            editFullName.setText(user.getFullName() != null ? user.getFullName() : "");
            editPhoneNumber.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            editAddress.setText(user.getAddress() != null ? user.getAddress() : "");
            editStoreName.setText(user.getStoreName() != null ? user.getStoreName() : "");
            editStoreDescription.setText(user.getStoreDescription() != null ? user.getStoreDescription() : "");
            editStoreStatus.setText(user.getStoreStatus() != null ? user.getStoreStatus().name() : "");
            editStoreName.setVisibility(View.VISIBLE);
            editStoreDescription.setVisibility(View.VISIBLE);
            editStoreStatus.setVisibility(View.VISIBLE);
        }
    }

    private void enableEditing(boolean enable) {
        editUserId.setEnabled(false); // Luôn khóa ID
        editEmail.setEnabled(enable);
        editPassword.setEnabled(enable);
        editFullName.setEnabled(enable);
        editPhoneNumber.setEnabled(enable);
        editAddress.setEnabled(enable);
        editStoreName.setEnabled(enable);
        editStoreDescription.setEnabled(enable);
        editStoreStatus.setEnabled(enable);
        isEditing = enable;
    }

    private void saveUserInfo() {
        if (user == null) return;

        // Cập nhật thông tin từ EditText vào đối tượng User
        user.setEmail(editEmail.getText().toString());
        user.setPassword(editPassword.getText().toString());
        user.setFullName(editFullName.getText().toString());
        user.setPhoneNumber(editPhoneNumber.getText().toString());
        user.setAddress(editAddress.getText().toString());
        user.setStoreName(editStoreName.getText().toString());
        user.setStoreDescription(editStoreDescription.getText().toString());
        // StoreStatus cần xử lý thủ công nếu cần (giả sử giữ nguyên giá trị cũ)
        user.setUpdatedAt(System.currentTimeMillis());

        // Lưu vào SessionManager
        sessionManager.createLoginSession(user);

        // Lưu vào cơ sở dữ liệu (nếu cần)
        new SaveUserTask().execute(user);
    }

    private class LoadUserTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... voids) {
            int userId = sessionManager.getUserId();
            if (userId != -1) {
                return App.getDatabase().userDao().getUserByIdd(userId); // Giả định có phương thức getUserById
            }
            return null;
        }

        @Override
        protected void onPostExecute(User loadedUser) {
            if (loadedUser != null) {
                user = loadedUser;
                displayUserInfo();
            } else {
                Toast.makeText(UserProfileActivity.this, "Không tải được thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SaveUserTask extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... users) {
            if (users[0] != null) {
                App.getDatabase().userDao().updateUser(users[0]); // Giả định có phương thức updateUser
            }
            return null;
        }
    }
}