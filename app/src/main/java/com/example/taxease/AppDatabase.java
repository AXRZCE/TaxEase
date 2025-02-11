package com.example.taxease;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Admin.class, Customer.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract AdminDao adminDao();
    public abstract CustomerDao customerDao();

    // Singleton instance
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "taxease-db")
                    .fallbackToDestructiveMigration() // Automatically handle schema changes
                    .allowMainThreadQueries() // For simplicity; use background threads in production
                    .build();
        }
        return instance;
    }
}
