package com.example.holafood.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Products RENAME TO temp_products");
            database.execSQL("CREATE TABLE Products (" +
                    "product_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "seller_id INTEGER," +
                    "name TEXT," +
                    "description TEXT," +
                    "price REAL," +
                    "image_resource_name TEXT DEFAULT ''," + // Cột mới
                    "status TEXT," +
                    "created_at INTEGER," +
                    "updated_at INTEGER," +
                    "FOREIGN KEY (seller_id) REFERENCES users(user_id) ON DELETE CASCADE)");
            database.execSQL("INSERT INTO Products (seller_id, name, description, price, status, created_at, updated_at) " +
                    "SELECT seller_id, name, description, price, status, created_at, updated_at FROM temp_products");
            database.execSQL("DROP TABLE temp_products");
        }
    };
}
