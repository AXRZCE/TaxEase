package com.example.taxease;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AdminDao {
    @Insert
    void insertAdmin(Admin admin);

    @Query("SELECT * FROM Admin WHERE username = :username AND password = :password")
    Admin getAdmin(String username, String password);
}
