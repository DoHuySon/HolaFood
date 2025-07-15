package com.example.holafood.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.holafood.model.Order;
import com.example.holafood.model.OrderStatus;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    void insertOrder(Order order);
    @Insert
    long insertOrders(Order order);
    @Insert
    void insertOrders(List<Order> orders);

    @Update
    void updateOrder(Order order);

    @Query("DELETE FROM Orders WHERE order_id = :orderId")
    void deleteOrder(int orderId);

    @Query("SELECT * FROM Orders WHERE order_id = :orderId")
    LiveData<Order> getOrderById(int orderId);

    @Query("SELECT * FROM Orders WHERE customer_id = :customerId")
    LiveData<List<Order>> getOrdersByCustomer(int customerId);

    @Query("SELECT * FROM Orders WHERE seller_id = :sellerId")
    LiveData<List<Order>> getOrdersBySeller(int sellerId);
    @Query("SELECT * FROM Orders WHERE customer_id = :customerId AND status = :status")
    LiveData<List<Order>> getDeliveredOrdersByCustomer(int customerId, String status);
    @Query("SELECT * FROM Orders WHERE status = :status")
    LiveData<List<Order>> getOrdersByStatus(OrderStatus status);

    @Query("SELECT * FROM Orders WHERE customer_id = :customerId ORDER BY created_at DESC")
    List<Order> getOrdersByCustomerNow(int customerId);

    @Query("SELECT * FROM Orders")
    LiveData<List<Order>> getAllOrders();

    @Query("SELECT * FROM Orders WHERE seller_id = :sellerId AND status = 'DELIVERED' ORDER BY created_at DESC")
    LiveData<List<Order>> getDeliveredOrdersBySeller(int sellerId);

    @Query("SELECT SUM(total_amount) FROM Orders WHERE seller_id = :sellerId AND status = 'DELIVERED' AND created_at BETWEEN :startTime AND :endTime")
    LiveData<Double> getTotalRevenueBySeller(int sellerId, long startTime, long endTime);
}
