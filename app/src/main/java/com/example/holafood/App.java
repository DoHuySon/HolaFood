package com.example.holafood;


import android.app.Application;

import androidx.room.Room;

import com.example.holafood.database.AppDatabase;
import com.example.holafood.model.Product;
import com.example.holafood.model.ProductStatus;

public class App extends Application {
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "app_database")
                .build();
        insertSampleProduct();
    }

    public static AppDatabase getDatabase() {
        return database;
    }
    public static void insertSampleProduct() {
        new Thread(() -> {
            Product existing = database.productDao().getProductByIdNow(1);
            if (existing == null) {
                Product sample = new Product(
                        2, // giả định sellerId là 2
                        "Pizza bò phô mai",
                        "Pizza thơm ngon với phô mai kéo sợi",
                        99000,
                        "pizza_sample", // imagePath: tên file drawable hoặc đường dẫn
                        ProductStatus.AVAILABLE
                );
                database.productDao().insertProduct(sample);
            }
        }).start();
    }

}