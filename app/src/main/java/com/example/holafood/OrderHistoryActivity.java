package com.example.holafood;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.adapter.OrderHistoryAdapter;
import com.example.holafood.database.AppDatabase;
import com.example.holafood.model.Order;
import com.example.holafood.model.User;
import com.example.holafood.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    private static final String TAG = "OrderHistoryActivity";
    private AppDatabase db;
    private User currentUser;
    private RecyclerView rvOrderHistory;
    private OrderHistoryAdapter orderHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        db = AppDatabase.getDatabase(this);
        rvOrderHistory = findViewById(R.id.rv_order_history);

        // Cấu hình RecyclerView
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        orderHistoryAdapter = new OrderHistoryAdapter(new ArrayList<>());
        rvOrderHistory.setAdapter(orderHistoryAdapter);

        // Lấy userId từ Session
        SessionManager sessionManager = new SessionManager(this);
        int userId = sessionManager.getUserId();

        // Tải danh sách đơn hàng
        new LoadOrdersTask().execute(userId);
    }

    private class LoadOrdersTask extends AsyncTask<Integer, Void, List<Order>> {
        @Override
        protected List<Order> doInBackground(Integer... params) {
            try {
                int userId = params[0];
                currentUser = db.userDao().getUserByIdNow(userId);
                if (currentUser != null) {
                    return db.orderDao().getOrdersByCustomerNow(userId);
                }
                return new ArrayList<>();
            } catch (Exception e) {
                Log.e(TAG, "Error loading orders: " + e.getMessage());
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<Order> orders) {
            if (orders != null && !orders.isEmpty()) {
                orderHistoryAdapter.setOrders(orders);
                Log.d(TAG, "Loaded " + orders.size() + " orders");
            } else {
                Toast.makeText(OrderHistoryActivity.this, "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
            }
        }
    }
}