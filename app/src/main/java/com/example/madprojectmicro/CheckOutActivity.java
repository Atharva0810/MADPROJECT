package com.example.madprojectmicro;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class CheckOutActivity extends AppCompatActivity {
    private EditText roomNumberEditText;
    private TextInputLayout roomNumberInputLayout;
    private Button submitButton;
    private ImageButton homeButton;
    private sqlitemanager dbManager;
    private static final String TAG = "CheckOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        // Initialize database manager
        dbManager = sqlitemanager.getInstance(this);

        // Initialize views
        roomNumberEditText = findViewById(R.id.roomNumberEditText);
        roomNumberInputLayout = findViewById(R.id.roomNumberInputLayout);
        submitButton = findViewById(R.id.submitButton);
        homeButton = findViewById(R.id.homeButton);

        // Set click listeners
        submitButton.setOnClickListener(v -> checkoutRoom());
        homeButton.setOnClickListener(v -> finish()); // Go back to dashboard
    }

    private void checkoutRoom() {
        String roomNumberStr = roomNumberEditText.getText().toString();

        // Validate room number
        if (roomNumberStr.isEmpty()) {
            roomNumberInputLayout.setError("Room number is required");
            return;
        }

        int roomNumber;
        try {
            roomNumber = Integer.parseInt(roomNumberStr);
        } catch (NumberFormatException e) {
            roomNumberInputLayout.setError("Invalid room number");
            return;
        }

        // Check if room number is valid (between 1 and 50)
        if (roomNumber < 1 || roomNumber > 50) {
            roomNumberInputLayout.setError("Room number must be between 1 and 50");
            return;
        }

        roomNumberInputLayout.setError(null);

        // Verify room is actually occupied before checkout
        verifyAndProcessCheckout(roomNumber);
    }

    private void verifyAndProcessCheckout(int roomNumber) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Updated query to find active booking and room details using a JOIN
            String query = "SELECT b.customer_id, r.id as room_id " +
                    "FROM rooms r " +
                    "INNER JOIN bookings b ON b.room_id = r.id " +
                    "WHERE r.room_number = ? " +
                    "AND r.status = 'occupied' " +
                    "AND b.status = 'checked_in'";  // Changed from booking_status to status

            cursor = db.rawQuery(query, new String[]{String.valueOf(roomNumber)});

            if (cursor != null && cursor.moveToFirst()) {
                // Room is occupied, proceed with checkout
                int customerIdIndex = cursor.getColumnIndex("customer_id");
                int roomIdIndex = cursor.getColumnIndex("room_id");

                int customerId = cursor.getInt(customerIdIndex);
                int roomId = cursor.getInt(roomIdIndex);

                Log.d(TAG, "Verified occupied room: " + roomNumber + ", Customer ID: " + customerId + ", Room ID: " + roomId);

                // Store customer_id and room_id for later use
                getSharedPreferences("HotelApp", MODE_PRIVATE)
                        .edit()
                        .putInt("last_customer_id", customerId)
                        .putInt("last_room_id", roomId)
                        .apply();

                new CheckoutTask(roomNumber, customerId, roomId).execute();
            } else {
                // Room is not occupied or no active booking
                Toast.makeText(this,
                        "Room " + roomNumber + " is not currently occupied or has no active booking",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error verifying room status", e);
            Toast.makeText(this,
                    "Error checking room status: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private class CheckoutTask extends AsyncTask<Void, Void, String> {
        private int roomNumber;
        private int customerId;
        private int roomId;
        private String errorMessage;

        public CheckoutTask(int roomNumber, int customerId, int roomId) {
            this.roomNumber = roomNumber;
            this.customerId = customerId;
            this.roomId = roomId;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                final String[] billNoHolder = new String[1];
                final boolean[] completed = {false};

                dbManager.checkoutCustomer(roomNumber, new sqlitemanager.CheckoutCallback() {
                    @Override
                    public void onSuccess(int roomNo, String billNo) {
                        billNoHolder[0] = billNo;
                        completed[0] = true;
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        CheckoutTask.this.errorMessage = errorMessage;
                        completed[0] = true;
                    }
                });

                // Wait for callback to complete
                long startTime = System.currentTimeMillis();
                while (!completed[0] && (System.currentTimeMillis() - startTime) < 5000) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Thread interrupted", e);
                        break;
                    }
                }

                return billNoHolder[0];
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e(TAG, "Error during checkout", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String billNo) {
            if (billNo != null) {
                Toast.makeText(CheckOutActivity.this,
                        "Room " + roomNumber + " has been vacated. Thank you! Your bill number is " + billNo,
                        Toast.LENGTH_LONG).show();

                // Store the bill number in SharedPreferences
                getSharedPreferences("HotelApp", MODE_PRIVATE)
                        .edit()
                        .putString("last_bill_number", billNo)
                        .apply();

                // Navigate to BillDetailsActivity
                Intent intent = new Intent(CheckOutActivity.this, BillDetailsActivity.class);
                intent.putExtra("customerId", customerId);
                intent.putExtra("roomId", roomId);
                intent.putExtra("billNumber", billNo);
                startActivity(intent);

                // Clear the input field
                roomNumberEditText.setText("");
            } else {
                Toast.makeText(CheckOutActivity.this,
                        errorMessage != null ? errorMessage : "Checkout failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

