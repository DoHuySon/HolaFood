package com.example.holafood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.adapter.UserAdapter;
import com.example.holafood.database.AppDatabase;
import com.example.holafood.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {
    private RecyclerView recyclerView;
    private Spinner spinner;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        recyclerView = view.findViewById(R.id.recycler_user);
        spinner = view.findViewById(R.id.spinner_user_type);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(getContext(), userList);
        recyclerView.setAdapter(adapter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{"Tất cả", "bán", "đặt"});
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String filter = parent.getItemAtPosition(pos).toString();
                List<User> allUsers = AppDatabase.getDatabase(getContext())
                        .userDao().getAllUsers()
                        .getValue();
                userList.clear();
                for (User u : allUsers) {
                    if (filter.equals("Tất cả") || u.getRole().equals(filter)) {
                        userList.add(u);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }
}