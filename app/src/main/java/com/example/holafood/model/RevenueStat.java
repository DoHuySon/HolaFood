package com.example.holafood.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Revenue_Stats",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "seller_id",
                onDelete = ForeignKey.CASCADE))
public class RevenueStat {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stat_id")
    private int statId;

    @ColumnInfo(name = "seller_id")
    private int sellerId;

    @ColumnInfo(name = "total_revenue")
    private double totalRevenue;

    @ColumnInfo(name = "period_start")
    private Date periodStart;

    @ColumnInfo(name = "period_end")
    private Date periodEnd;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public RevenueStat() {
        this.createdAt = System.currentTimeMillis();
    }

    public RevenueStat(int sellerId, double totalRevenue, Date periodStart, Date periodEnd) {
        this.sellerId = sellerId;
        this.totalRevenue = totalRevenue;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.createdAt = System.currentTimeMillis();
    }

    // Getter và Setter
    public int getStatId() { return statId; }
    public void setStatId(int statId) { this.statId = statId; }
    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public Date getPeriodStart() { return periodStart; }
    public void setPeriodStart(Date periodStart) { this.periodStart = periodStart; }
    public Date getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(Date periodEnd) { this.periodEnd = periodEnd; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
