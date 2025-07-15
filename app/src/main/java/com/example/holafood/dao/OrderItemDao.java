package com.example.holafood.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.holafood.model.OrderItem;

import java.util.List;

@Dao
public interface OrderItemDao {
    @Insert
    long insertOrderItem(OrderItem orderItem);

    @Insert
    void insertOrderItems(List<OrderItem> orderItems);

    @Query("DELETE FROM Order_Items WHERE order_item_id = :orderItemId")
    void deleteOrderItem(int orderItemId);

    @Query("SELECT * FROM Order_Items WHERE order_id = :orderId")
    LiveData<List<OrderItem>> getOrderItemsByOrder(int orderId);

    @Query("SELECT * FROM Order_Items WHERE order_id = :orderId")
    List<OrderItem> getOrderItemsByOrderSync(int orderId); // Thêm cho thread
}
