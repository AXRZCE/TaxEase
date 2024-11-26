package com.example.taxease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPhone, etAddress, etCity, etZipcode, etPassword;
    private Button btnRegister, btnBackToLogin; // Declare both buttons here

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etZipcode = findViewById(R.id.etZipcode);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin); // Initialize the Back to Login button

        // Initialize Room database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taxease-db")
                .allowMainThreadQueries() // For simplicity; use background threads in production
                .build();

        // Register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCustomer();
            }
        });

        // Back to Login button click listener
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Login Screen
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Prevent returning to RegistrationActivity when back button is pressed
            }
        });
    }

    private void registerCustomer() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();
        String city = etCity.getText().toString();
        String zipcode = etZipcode.getText().toString();
        String password = etPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() || zipcode.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullAddress = address + ", " + city + ", " + zipcode;
        fetchCoordinates(fullAddress, (latitude, longitude) -> {
            Customer customer = new Customer(name, email, phone, address, city, zipcode, password, "AWAITED", latitude, longitude);
            db.customerDao().insertCustomer(customer);

            Toast.makeText(this, "Customer registered successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to login screen
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchCoordinates(String address, GeocodeCallback callback) {
        String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyBL3xONUGDAZVMyxsgKRA3lDbD82OBAT_8";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    if (results.length() > 0) {
                        JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                        double latitude = location.getDouble("lat");
                        double longitude = location.getDouble("lng");

                        callback.onSuccess(latitude, longitude);
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Invalid address. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this, "Error fetching coordinates. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);
    }

    interface GeocodeCallback {
        void onSuccess(double latitude, double longitude);
    }
}
