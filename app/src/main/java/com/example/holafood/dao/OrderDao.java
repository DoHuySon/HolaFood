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

    @Query("SELECT * FROM Orders WHERE status = :status")
    LiveData<List<Order>> getOrdersByStatus(OrderStatus status);
    @Query("SELECT * FROM Orders")
    List<Order> getAllOrders();
}
