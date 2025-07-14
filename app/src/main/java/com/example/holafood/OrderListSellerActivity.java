package com.example.holafood;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.adapter.OrderSellerAdapter;
import com.example.holafood.dao.OrderDao;
import com.example.holafood.dao.UserDao;
import com.example.holafood.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderListSellerActivity extends AppCompatActivity {

    private static final String TAG = "OrderListSellerActivity";
    private RecyclerView recyclerView;
    private OrderDao orderDao;
    private UserDao userDao;
    private OrderSellerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_seller);

        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderDao = App.getDatabase().orderDao();
        userDao = App.getDatabase().userDao();

        adapter = new OrderSellerAdapter(this, new ArrayList<>(), orderDao, userDao);
        recyclerView.setAdapter(adapter);

        orderDao.getAllOrders().observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                Log.d(TAG, "Đã tải " + (orders != null ? orders.size() : 0) + " đơn hàng");
                if (orders != null) {
                    adapter = new OrderSellerAdapter(OrderListSellerActivity.this, orders, orderDao, userDao);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }
}