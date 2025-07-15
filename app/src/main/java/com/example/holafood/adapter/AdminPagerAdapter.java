package com.example.holafood.adapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.holafood.OrderListFragment;
import com.example.holafood.ProductListFragment;
import com.example.holafood.UserListFragment;

public class AdminPagerAdapter extends FragmentStateAdapter {
    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new UserListFragment();
            case 1: return new ProductListFragment();
            case 2: return new OrderListFragment();
            default: return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
