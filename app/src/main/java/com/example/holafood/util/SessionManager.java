package com.example.holafood.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.holafood.model.Role;
import com.example.holafood.model.User;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Lưu thông tin người dùng sau khi đăng nhập
    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_PHONE_NUMBER, user.getPhoneNumber());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_ROLE, user.getRole().name());
        editor.apply();
    }

    // Kiểm tra trạng thái đăng nhập
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy thông tin người dùng
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getPhoneNumber() {
        return pref.getString(KEY_PHONE_NUMBER, null);
    }

    public String getFullName() {
        return pref.getString(KEY_FULL_NAME, null);
    }

    public Role getRole() {
        String role = pref.getString(KEY_ROLE, null);
        return role != null ? Role.valueOf(role) : null;
    }

    // Đăng xuất
    public void logout() {
        editor.clear();
        editor.apply();
    }
}