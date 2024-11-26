package com.example.taxease;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CustomerDao {
    @Insert
    void insertCustomer(Customer customer);

    @Query("SELECT * FROM Customer")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM Customer WHERE id = :id")
    Customer getCustomerById(int id);

    @Update
    void updateCustomer(Customer customer);

    @Delete
    void deleteCustomer(Customer customer);

    @Query("SELECT * FROM Customer WHERE email = :email AND password = :password")
    Customer getCustomer(String email, String password);





}
