package com.example.holafood;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.example.holafood.adapter.ProductAdapter;
import com.example.holafood.database.AppDatabase;
import com.example.holafood.model.Product;
import com.example.holafood.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressLint("ValidFragment")
public class ProductListFragment extends Fragment {

    private Spinner spinnerStore;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private AppDatabase db;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();

    public ProductListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        spinnerStore = view.findViewById(R.id.store_spinner);
        recyclerView = view.findViewById(R.id.recycler_product);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter(getContext(), filteredProducts);
        recyclerView.setAdapter(adapter);

        // Lấy toàn bộ sản phẩm
        AppDatabase db = AppDatabase.getDatabase(getContext());
        allProducts = db.productDao().getAllProducts();

        // Tạo danh sách quán (storeName)
        List<Integer> sellerIds = new ArrayList<>();
        for (Product p : allProducts) {
            sellerIds.add(p.getSellerId());
        }

        Set<String> storeSet = db.userDao()
                .getStoreSet(sellerIds);

        List<String> storeList = new ArrayList<>();
        storeList.add("Tất cả");
        if (storeSet != null
                && !storeSet.isEmpty()) {
            storeList.addAll(storeSet);
        }


        // Gán dữ liệu vào Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                storeList
        );
        spinnerStore = view.findViewById(R.id.store_spinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStore.setAdapter(spinnerAdapter);

        spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                String selectedStore = parent.getItemAtPosition(position).toString();
                filteredProducts.clear();
                for (Product product : allProducts) {
                    User user = db.userDao()
                            .getUserById(product.getSellerId())
                            .getValue();
                    if (selectedStore.equals("Tất cả")
                            || user.getStoreName().equalsIgnoreCase(selectedStore)) {
                        filteredProducts.add(product);
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