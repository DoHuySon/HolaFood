package com.example.holafood.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.holafood.model.Product;
import com.example.holafood.model.ProductStatus;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insertProduct(Product product);

    @Insert
    void insertProducts(List<Product> products);

    @Update
    void updateProduct(Product product);

    @Query("DELETE FROM Products WHERE product_id = :productId")
    void deleteProduct(int productId);

    @Query("SELECT * FROM Products WHERE product_id = :productId")
    LiveData<Product> getProductById(int productId);

    @Query("SELECT * FROM Products WHERE seller_id = :sellerId")
    LiveData<List<Product>> getProductsBySeller(int sellerId);

    @Query("SELECT * FROM Products WHERE status = :status")
    LiveData<List<Product>> getProductsByStatus(ProductStatus status);
}
