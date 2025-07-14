package com.example.holafood;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.holafood.dao.OrderDao;
import com.example.holafood.dao.RevenueStatDao;
import com.example.holafood.model.RevenueStat;
import com.example.holafood.RevenueStatViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RevenueStatActivity extends AppCompatActivity {
    private TextView tvTotalRevenue, tvPeriod;
    private RevenueStatViewModel viewModel;
    private int sellerId; // Lấy từ Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_stat);

        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        tvPeriod = findViewById(R.id.tv_period);

        // Lấy sellerId từ Intent
        sellerId = getIntent().getIntExtra("sellerId", -1);
        if (sellerId == -1) {
            finish(); // Kết thúc nếu không có sellerId
            return;
        }

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(RevenueStatViewModel.class);
        viewModel.setDaos(App.getDatabase().orderDao(), App.getDatabase().revenueStatDao());

        // Tính doanh thu cho tháng hiện tại
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        long startTime = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        long endTime = cal.getTimeInMillis();

        viewModel.loadRevenueStats(sellerId, startTime, endTime).observe(this, revenueStat -> {
            if (revenueStat != null) {
                tvTotalRevenue.setText(String.format(Locale.getDefault(), "Doanh thu: $%.2f", revenueStat.getTotalRevenue()));
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                tvPeriod.setText("Thời gian: " + sdf.format(new Date(String.valueOf(revenueStat.getPeriodStart()))));
            } else {
                tvTotalRevenue.setText("Doanh thu: $0.00");
                tvPeriod.setText("Thời gian: Không có dữ liệu");
            }
        });
    }
}