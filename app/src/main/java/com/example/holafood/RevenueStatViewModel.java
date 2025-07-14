package com.example.holafood;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.holafood.dao.OrderDao;
import com.example.holafood.dao.RevenueStatDao;
import com.example.holafood.model.RevenueStat;

import java.util.Date;
import java.util.concurrent.Executors;

public class RevenueStatViewModel extends ViewModel {
    private OrderDao orderDao;
    private RevenueStatDao revenueStatDao;
    private MutableLiveData<RevenueStat> revenueStat = new MutableLiveData<>();

    public void setDaos(OrderDao orderDao, RevenueStatDao revenueStatDao) {
        this.orderDao = orderDao;
        this.revenueStatDao = revenueStatDao;
    }

    public LiveData<RevenueStat> loadRevenueStats(int sellerId, long startTime, long endTime) {
        revenueStat = new MutableLiveData<>();

        // Sử dụng Executor để chạy trên thread khác
        Executors.newSingleThreadExecutor().execute(() -> {
            // Kiểm tra RevenueStat hiện có
            RevenueStat existingStat = revenueStatDao.getRevenueStatByPeriod(sellerId, startTime, endTime);
            if (existingStat != null) {
                revenueStat.postValue(existingStat); // postValue để cập nhật trên main thread
            } else {
                // Tính toán doanh thu mới
                Double totalRevenue = orderDao.getTotalRevenueBySeller(sellerId, startTime, endTime).getValue();
                if (totalRevenue != null) {
                    RevenueStat newStat = new RevenueStat(sellerId, totalRevenue, new Date(startTime), new Date(endTime));
                    revenueStatDao.insertRevenueStat(newStat);
                    revenueStat.postValue(newStat);
                } else {
                    revenueStat.postValue(null);
                }
            }
        });

        return revenueStat;
    }
}