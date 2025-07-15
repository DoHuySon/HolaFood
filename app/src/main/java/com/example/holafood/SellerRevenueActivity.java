package com.example.holafood;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.adapter.RevenueStatAdapter;
import com.example.holafood.database.AppDatabase;
import com.example.holafood.model.RevenueStat;
import com.example.holafood.model.Role;
import com.example.holafood.model.User;
import com.example.holafood.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SellerRevenueActivity extends AppCompatActivity {

    private static final String TAG = "SellerRevenueActivity";
    private AppDatabase db;
    private User currentUser;
    private TextView totalRevenueText;
    private RecyclerView rvRevenueStats;
    private RevenueStatAdapter revenueStatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_revenue);

        db = AppDatabase.getDatabase(this);
        totalRevenueText = findViewById(R.id.total_revenue_text);
        rvRevenueStats = findViewById(R.id.rv_revenue_stats);

        // Cấu hình RecyclerView
        rvRevenueStats.setLayoutManager(new LinearLayoutManager(this));
        revenueStatAdapter = new RevenueStatAdapter(new ArrayList<>());
        rvRevenueStats.setAdapter(revenueStatAdapter);

        // Lấy userId từ Session
        SessionManager sessionManager = new SessionManager(this);
        int userId = sessionManager.getUserId();

        // Tải thống kê doanh thu
        if (userId != -1) {
            new LoadRevenueStatsTask().execute(userId);
        } else {
            Toast.makeText(this, "Lỗi: Không xác định người bán", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class LoadRevenueStatsTask extends AsyncTask<Integer, Void, Pair<Double, List<RevenueStat>>> {
        @Override
        protected Pair<Double, List<RevenueStat>> doInBackground(Integer... params) {
            try {
                int sellerId = params[0];
                Log.d(TAG, "Bắt đầu tải thống kê doanh thu cho sellerId: " + sellerId);

                currentUser = db.userDao().getUserByIdNow(sellerId);
                Log.d(TAG, "Người dùng hiện tại: " + (currentUser != null ? "Tìm thấy, Vai trò: " + currentUser.getRole() : "Không tìm thấy"));

                if (currentUser != null && currentUser.getRole() == Role.SELLER) {
                    // Tính tổng doanh thu từ tất cả các đơn hàng DELIVERED
                    Double totalRevenue = db.orderDao().getTotalRevenueBySellerSync(sellerId); // Sử dụng phương thức đồng bộ
                    Log.d(TAG, "Tổng doanh thu tính được: " + (totalRevenue != null ? totalRevenue : "null"));

                    // Lấy danh sách thống kê từ RevenueStat
                    List<RevenueStat> stats = db.revenueStatDao().getRevenueStatsBySeller(sellerId).getValue();
                    Log.d(TAG, "Số lượng thống kê doanh thu: " + (stats != null ? stats.size() : 0));

                    return new Pair<>(totalRevenue != null ? totalRevenue : 0.0, stats != null ? stats : new ArrayList<>());
                }
                Log.w(TAG, "Người dùng null hoặc không phải SELLER, trả về giá trị mặc định");
                return new Pair<>(0.0, new ArrayList<>());
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi tải thống kê doanh thu: " + e.getMessage(), e);
                return new Pair<>(0.0, new ArrayList<>());
            }
        }

        @Override
        protected void onPostExecute(Pair<Double, List<RevenueStat>> result) {
            double totalRevenue = result.first;
            List<RevenueStat> stats = result.second;

            Log.d(TAG, "Cập nhật giao diện - Tổng doanh thu: " + totalRevenue + ", Số lượng thống kê: " + (stats != null ? stats.size() : 0));
            totalRevenueText.setText(String.format("Tổng doanh thu: %.2fđ", totalRevenue));
            revenueStatAdapter.setRevenueStats(stats);
        }
    }
}