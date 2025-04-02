package com.example.madprojectmicro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BillDetailsActivity extends AppCompatActivity {

    private TextView tvCustomerName, tvRoomNumber, tvCheckInDate, tvCheckOutDate, tvTotalDays, tvRoomRate, tvTotalAmount;
    private Button btnPay;
    private sqlitemanager dbManager;
    private int customerId, roomId;
    private String customerName, roomNumber, checkInDate, checkOutDate;
    private int totalDays;
    private double roomRate, totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);

        // Initialize views
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvRoomNumber = findViewById(R.id.tvRoomNumber);
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        tvTotalDays = findViewById(R.id.tvTotalDays);
        tvRoomRate = findViewById(R.id.tvRoomRate);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPay = findViewById(R.id.btnPay);

        // Get data from intent
        Intent intent = getIntent();
        customerId = intent.getIntExtra("customerId", 0);
        roomId = intent.getIntExtra("roomId", 0);

        // Initialize SQLite manager
        dbManager = sqlitemanager.getInstance(this);

        // Load bill details
        loadBillDetails();

        // Set button click listener
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePaymentStatus();
            }
        });
    }

    private void loadBillDetails() {
        dbManager.getBillDetails(customerId, roomId, new sqlitemanager.BillDetailsCallback() {
            @Override
            public void onSuccess(String customerName, String roomNumber, String checkInDate,
                                  String checkOutDate, double roomRate, int totalDays, double totalAmount) {
                // Set values to views
                tvCustomerName.setText(customerName);
                tvRoomNumber.setText(roomNumber);
                tvCheckInDate.setText(checkInDate);
                tvCheckOutDate.setText(checkOutDate);
                tvTotalDays.setText(String.valueOf(totalDays));
                tvRoomRate.setText(String.format(Locale.getDefault(), "%.2f", roomRate));
                tvTotalAmount.setText(String.format(Locale.getDefault(), "%.2f", totalAmount));

                // Save the values for later use
                BillDetailsActivity.this.customerName = customerName;
                BillDetailsActivity.this.roomNumber = roomNumber;
                BillDetailsActivity.this.checkInDate = checkInDate;
                BillDetailsActivity.this.checkOutDate = checkOutDate;
                BillDetailsActivity.this.roomRate = roomRate;
                BillDetailsActivity.this.totalDays = totalDays;
                BillDetailsActivity.this.totalAmount = totalAmount;
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(BillDetailsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updatePaymentStatus() {
        dbManager.updatePaymentStatus(customerId, roomId, new sqlitemanager.PaymentCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(BillDetailsActivity.this, "Payment successful", Toast.LENGTH_SHORT).show();

                // Navigate back to dashboard
                Intent intent = new Intent(BillDetailsActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(BillDetailsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}