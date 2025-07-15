package com.example.holafood.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.holafood.model.RevenueStat;

import java.util.Date;
import java.util.List;

@Dao
public interface RevenueStatDao {
    @Insert
    long insertRevenueStat(RevenueStat stat);

    @Insert
    void insertRevenueStats(List<RevenueStat> stats);

    @Query("SELECT * FROM Revenue_Stats WHERE seller_id = :sellerId ORDER BY period_start DESC")
    LiveData<List<RevenueStat>> getRevenueStatsBySeller(int sellerId);

    @Query("SELECT * FROM Revenue_Stats WHERE seller_id = :sellerId AND period_start = :periodStart AND period_end = :periodEnd")
    RevenueStat getRevenueStatByPeriod(int sellerId, long periodStart, long periodEnd);
}
