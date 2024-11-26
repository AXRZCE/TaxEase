package com.example.taxease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class CustomerHomeActivity extends AppCompatActivity {

    private EditText etName, etPhone, etAddress, etCity, etZipcode;
    private TextView tvProcessStatus;
    private Button btnSaveDetails, btnLogout;

    private AppDatabase db;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        // Initialize views
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etZipcode = findViewById(R.id.etZipcode);
        tvProcessStatus = findViewById(R.id.tvProcessStatus);
        btnSaveDetails = findViewById(R.id.btnSaveDetails);
        btnLogout = findViewById(R.id.btnLogout);

        // Initialize database
        db = AppDatabase.getInstance(this);

        // Get customer ID from Intent
        int customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);
        if (customerId == -1) {
            Toast.makeText(this, "Customer not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch customer details from database
        customer = db.customerDao().getCustomerById(customerId);
        if (customer == null) {
            Toast.makeText(this, "Customer not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate fields with customer details
        etName.setText(customer.getName());
        etPhone.setText(customer.getPhone());
        etAddress.setText(customer.getAddress());
        etCity.setText(customer.getCity());
        etZipcode.setText(customer.getZipcode());
        tvProcessStatus.setText("Process Status: " + customer.getProcessStatus());

        // Set process status background color
        int color;
        switch (customer.getProcessStatus()) {
            case "AWAITED":
                color = getResources().getColor(R.color.awaited_yellow);
                break;
            case "FAILEDTOREACH":
                color = getResources().getColor(R.color.failed_red);
                break;
            case "ONBOARDED":
                color = getResources().getColor(R.color.light_green);
                break;
            case "INPROCESS":
                color = getResources().getColor(R.color.mid_green);
                break;
            case "COMPLETED":
                color = getResources().getColor(R.color.dark_green);
                break;
            case "DENIED":
                color = getResources().getColor(R.color.denied_red);
                break;
            default:
                color = getResources().getColor(android.R.color.white);
        }
        tvProcessStatus.setBackgroundColor(color);

        // Save details functionality
        btnSaveDetails.setOnClickListener(v -> {
            customer.setName(etName.getText().toString());
            customer.setPhone(etPhone.getText().toString());
            customer.setAddress(etAddress.getText().toString());
            customer.setCity(etCity.getText().toString());
            customer.setZipcode(etZipcode.getText().toString());

            db.customerDao().updateCustomer(customer);

            Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
        });

        // Logout functionality
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
