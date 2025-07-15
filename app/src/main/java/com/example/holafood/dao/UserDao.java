package com.example.holafood.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.holafood.model.User;
import com.example.holafood.model.Role;
import com.example.holafood.model.StoreStatus;

import java.util.List;

@Dao
public interface UserDao {
    // Thêm một người dùng
    @Insert
    long insertUser(User user);

    // Thêm nhiều người dùng
    @Insert
    void insertUsers(List<User> users);

    // Cập nhật người dùng
    @Update
    void updateUser(User user);

    // Xóa người dùng
    @Query("DELETE FROM Users WHERE user_id = :userId")
    void deleteUser(int userId);

    // Lấy người dùng theo ID
    @Query("SELECT * FROM Users WHERE user_id = :userId")
    LiveData<User> getUserById(int userId);
    @Query("SELECT * FROM Users WHERE user_id = :userId")
    User getUserByIdd(int userId);

    // Lấy người dùng theo email
    @Query("SELECT * FROM Users WHERE email = :email")
    LiveData<User> getUserByEmail(String email);

    // Lấy tất cả người dùng theo vai trò
    @Query("SELECT * FROM Users WHERE role = :role")
    LiveData<List<User>> getUsersByRole(Role role);

    // Lấy tất cả người dùng
    @Query("SELECT * FROM Users")
    LiveData<List<User>> getAllUsers();

    // Lấy danh sách người bán theo trạng thái
    @Query("SELECT * FROM Users WHERE role = 'SELLER' AND store_status = :status")
    LiveData<List<User>> getSellersByStatus(StoreStatus status);
    // Ktra đăng nhập
    @Query("SELECT * FROM Users WHERE phone_number = :phoneNumber AND password = :password")
    User getUserByPhoneAndPassword(String phoneNumber, String password);

    @Query("SELECT * FROM Users WHERE user_id = :userId")
    User getUserByIdNow(int userId); // <-- trả về trực tiếp, dùng trong background thread

}
