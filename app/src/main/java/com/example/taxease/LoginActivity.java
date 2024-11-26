package com.example.taxease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister); // Initialize Register button

        // Initialize Room database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taxease-db")
                .allowMainThreadQueries() // For simplicity; use background threads in production
                .build();

        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Registration Screen
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String emailOrUsername = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if Admin
        Admin admin = db.adminDao().getAdmin(emailOrUsername, password); // Query Admin table
        if (admin != null) {
            Toast.makeText(this, "Admin logged in successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class); // Navigate to AdminHomeActivity
            startActivity(intent);
            finish();
            return;
        }

        // Check if Customer
        Customer customer = db.customerDao().getCustomer(emailOrUsername, password); // Query Customer table
        if (customer != null) {
            Toast.makeText(this, "Customer logged in successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, CustomerHomeActivity.class); // Navigate to CustomerHomeActivity
            intent.putExtra("CUSTOMER_ID", customer.getId()); // Pass customer ID
            startActivity(intent);
            finish();
            return;
        }

        // Invalid credentials
        Toast.makeText(this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
    }

}
