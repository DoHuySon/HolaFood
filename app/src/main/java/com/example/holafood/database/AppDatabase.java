package com.example.holafood.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.holafood.dao.OrderDao;
import com.example.holafood.dao.OrderItemDao;
import com.example.holafood.dao.ProductDao;
import com.example.holafood.dao.ReviewDao;
import com.example.holafood.dao.RevenueStatDao;
import com.example.holafood.dao.UserDao;
import com.example.holafood.model.Converters;
import com.example.holafood.model.Order;
import com.example.holafood.model.OrderItem;
import com.example.holafood.model.Product;
import com.example.holafood.model.Review;
import com.example.holafood.model.RevenueStat;
import com.example.holafood.model.User;

@Database(entities = {User.class, Product.class, Order.class, OrderItem.class, Review.class, RevenueStat.class},
        version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderItemDao orderItemDao();
    public abstract ReviewDao reviewDao();
    public abstract RevenueStatDao revenueStatDao();
}
