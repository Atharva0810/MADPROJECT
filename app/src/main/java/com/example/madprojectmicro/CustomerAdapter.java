package com.example.madprojectmicro;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private List<Customer> customerList;
    private static final String TAG = "CustomerAdapter";

    public CustomerAdapter(List<Customer> customerList) {
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        // Log the customer information for debugging
        Log.d(TAG, "Customer at position " + position + ": " + customer.getName() +
                ", isOccupied: " + customer.isOccupiedOrNot());

        holder.nameTextView.setText(customer.getName());
        holder.mobileTextView.setText(customer.getMobileNo());
        holder.roomTypeTextView.setText(customer.getRoomType());
        holder.roomNoTextView.setText("Room No:"+String.valueOf(customer.getRoomNoAllocated()));

        // Check if the customer is checked in
        boolean isOccupied = customer.isOccupiedOrNot();
        if (isOccupied) {
            holder.statusTextView.setText("Checked In");
            holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else {
            holder.statusTextView.setText("Checked Out");
            holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    // Method to update the customer list
    public void updateCustomerList(List<Customer> newCustomerList) {
        this.customerList = newCustomerList;
        notifyDataSetChanged();
    }

    // Method to update a single customer
    public void updateCustomer(Customer customer, int position) {
        if (position >= 0 && position < customerList.size()) {
            customerList.set(position, customer);
            notifyItemChanged(position);
        }
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, mobileTextView, roomTypeTextView, roomNoTextView, statusTextView;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            mobileTextView = itemView.findViewById(R.id.mobileTextView);
            roomTypeTextView = itemView.findViewById(R.id.roomTypeTextView);
            roomNoTextView = itemView.findViewById(R.id.roomNoTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}