package com.example.taxease;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomerDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvName, tvEmail, tvPhone, tvAddress;
    private Spinner spinnerStatus;
    private Button btnUpdateStatus;
    private GoogleMap googleMap;

    private AppDatabase db;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        // Initialize database
        db = AppDatabase.getInstance(this);

        // Get Customer ID from Intent
        int customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);
        if (customerId == -1) {
            Toast.makeText(this, "Customer not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch customer details
        customer = db.customerDao().getCustomerById(customerId);
        if (customer == null) {
            Toast.makeText(this, "Customer not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate views
        tvName.setText(customer.getName());
        tvEmail.setText(customer.getEmail());
        tvPhone.setText(customer.getPhone());
        tvAddress.setText(customer.getAddress() + ", " + customer.getCity() + ", " + customer.getZipcode());

        // Populate spinner with statuses
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.status_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Set the current status in the spinner
        int position = adapter.getPosition(customer.getProcessStatus());
        spinnerStatus.setSelection(position);

        // Handle status update
        btnUpdateStatus.setOnClickListener(v -> {
            String newStatus = spinnerStatus.getSelectedItem().toString();
            customer.setProcessStatus(newStatus);
            db.customerDao().updateCustomer(customer);
            Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
        });

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Map fragment is null. Please check your layout file.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (customer != null) {
            LatLng location = new LatLng(customer.getLatitude(), customer.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(location).title(customer.getName()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        } else {
            Toast.makeText(this, "Location not available for this customer.", Toast.LENGTH_SHORT).show();
        }
    }
}
