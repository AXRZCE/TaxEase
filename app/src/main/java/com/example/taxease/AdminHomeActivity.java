package com.example.taxease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private CustomerAdapter adapter;
    private Button btnLogout; // Declare the logout button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewCustomers);
        btnLogout = findViewById(R.id.btnLogout); // Initialize the logout button

        // Initialize database
        db = AppDatabase.getInstance(this);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set Logout Button Logic
        btnLogout.setOnClickListener(v -> {
            // Show confirmation dialog before logging out
            new AlertDialog.Builder(AdminHomeActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Navigate to LoginActivity
                        Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Close AdminHomeActivity to prevent going back
                    })
                    .setNegativeButton("No", null) // Do nothing if "No" is clicked
                    .show();
        });

        // Add swipe-to-delete functionality
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // We are not handling drag-and-drop
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Get the position of the swiped item
                int position = viewHolder.getAdapterPosition();
                Customer customer = adapter.getCustomerAtPosition(position);

                // Show confirmation dialog
                new AlertDialog.Builder(AdminHomeActivity.this)
                        .setTitle("Delete Customer")
                        .setMessage("Are you sure you want to delete this customer?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Delete customer from database
                            db.customerDao().deleteCustomer(customer);

                            // Remove customer from the adapter
                            adapter.removeCustomer(position);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Cancel the swipe
                            adapter.notifyItemChanged(position);
                        })
                        .show();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Fetch updated customer data from the database
        List<Customer> customerList = db.customerDao().getAllCustomers();

        if (adapter == null) {
            // Initialize the adapter if it's null
            adapter = new CustomerAdapter(this, customerList, customer -> {
                Intent intent = new Intent(AdminHomeActivity.this, CustomerDetailActivity.class);
                intent.putExtra("CUSTOMER_ID", customer.getId());
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        } else {
            // Update the existing adapter with new data
            adapter.updateCustomerList(customerList);
        }
    }
}
