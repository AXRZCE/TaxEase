package com.example.taxease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private final Context context;
    private final List<Customer> customerList;
    private final OnItemClickListener onItemClickListener;

    // Constructor with click listener
    public CustomerAdapter(Context context, List<Customer> customerList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.customerList = new ArrayList<>(customerList); // Use a copy of the list to avoid external modifications
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.tvName.setText(customer.getName());
        holder.tvPhone.setText(customer.getPhone());
        holder.tvCity.setText(customer.getCity());

        // Set background color based on process status
        int color;
        switch (customer.getProcessStatus()) {
            case "AWAITED":
                color = context.getResources().getColor(R.color.awaited_yellow);
                break;
            case "FAILEDTOREACH":
                color = context.getResources().getColor(R.color.failed_red);
                break;
            case "ONBOARDED":
                color = context.getResources().getColor(R.color.light_green);
                break;
            case "INPROCESS":
                color = context.getResources().getColor(R.color.mid_green);
                break;
            case "COMPLETED":
                color = context.getResources().getColor(R.color.dark_green);
                break;
            case "DENIED":
                color = context.getResources().getColor(R.color.denied_red);
                break;
            default:
                color = context.getResources().getColor(android.R.color.white);
        }
        holder.itemView.setBackgroundColor(color);

        // Set click listener for each item
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(customer));
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    // ViewHolder Class
    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvCity;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvCity = itemView.findViewById(R.id.tvCity);
        }
    }

    // Interface for click handling
    public interface OnItemClickListener {
        void onItemClick(Customer customer);
    }

    // Method to update customer list
    public void updateCustomerList(List<Customer> newCustomerList) {
        customerList.clear(); // Clear the old list
        customerList.addAll(newCustomerList); // Add new data
        notifyDataSetChanged(); // Notify the RecyclerView to refresh
    }

    // Method to remove customer at position
    public void removeCustomer(int position) {
        customerList.remove(position); // Remove the customer from the list
        notifyItemRemoved(position); // Notify the RecyclerView about the removal
    }

    // Method to get customer at position
    public Customer getCustomerAtPosition(int position) {
        return customerList.get(position); // Return the customer at the specified position
    }
}
