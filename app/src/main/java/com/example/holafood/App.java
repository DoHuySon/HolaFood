package com.example.holafood;


import android.app.Application;

import androidx.room.Room;

import com.example.holafood.database.AppDatabase;

public class App extends Application {
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "app_database")
                .build();
    }

    public static AppDatabase getDatabase() {
        return database;
    }
}