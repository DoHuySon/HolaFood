package com.example.holafood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.adapter.OrderAdapter;
import com.example.holafood.database.AppDatabase;
import com.example.holafood.model.Order;
import com.example.holafood.model.Product;
import com.example.holafood.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderListFragment extends Fragment {

    private Spinner spinnerStore;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private AppDatabase db;
    private List<Order> allOrders = new ArrayList<>();
    private List<Order> filteredOrders = new ArrayList<>();

    public OrderListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        spinnerStore = view.findViewById(R.id.spinner_order_store_filter);
        recyclerView = view.findViewById(R.id.recycler_order);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(getContext(), filteredOrders);
        recyclerView.setAdapter(adapter);

        // Lấy toàn bộ đơn hàng từ DB
        AppDatabase db = AppDatabase.getDatabase(getContext());
        allOrders = db.orderDao()
                .getAllOrders();

        // Tạo danh sách quán từ dữ liệu đơn hàng
        List<Integer> sellerIds = new ArrayList<>();
        for (Order o : allOrders) {
            sellerIds.add(o.getSellerId());
        }

        Set<String> storeSet = db.userDao()
                .getStoreSet(sellerIds);

        List<String> storeList = new ArrayList<>();
        storeList.add("Tất cả");
        if (storeSet != null
                && !storeSet.isEmpty()) {
            storeList.addAll(storeSet);
        }

        // Spinner adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                storeList
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStore.setAdapter(spinnerAdapter);

        spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                String selectedStore = parent.getItemAtPosition(position).toString();
                filteredOrders.clear();
                for (Order order : allOrders) {
                    User user = db.userDao()
                            .getUserById(order.getSellerId())
                            .getValue();
                    if (selectedStore.equals("Tất cả") || user.getStoreName().equalsIgnoreCase(selectedStore)) {
                        filteredOrders.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }
}