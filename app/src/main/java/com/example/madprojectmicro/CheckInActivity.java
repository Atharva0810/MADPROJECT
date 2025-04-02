package com.example.madprojectmicro;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Add this import
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CheckInActivity extends AppCompatActivity {
    private EditText nameEditText, mobileEditText, nationalityEditText, aadharEditText,
            daysToStayEditText, addressEditText;
    private TextView checkInDateTextView;
    private Spinner roomTypeSpinner, bedSpinner, genderSpinner;
    private Button clearButton, allocateButton;
    private ImageButton homeButton; // Changed from Button to ImageButton
    private TextInputLayout mobileInputLayout, aadharInputLayout;

    private sqlitemanager dbManager;
    private Calendar calendar;
    private Date checkInDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        // Initialize database manager
        dbManager = sqlitemanager.getInstance(this);

        // Initialize date format and calendar
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        calendar = Calendar.getInstance();
        checkInDate = calendar.getTime();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        nationalityEditText = findViewById(R.id.nationalityEditText);
        aadharEditText = findViewById(R.id.aadharEditText);
        daysToStayEditText = findViewById(R.id.daysToStayEditText);
        addressEditText = findViewById(R.id.addressEditText);
        checkInDateTextView = findViewById(R.id.checkInDateTextView);

        roomTypeSpinner = findViewById(R.id.roomTypeSpinner);
        bedSpinner = findViewById(R.id.bedSpinner);
        genderSpinner = findViewById(R.id.genderSpinner);

        clearButton = findViewById(R.id.clearButton);
        allocateButton = findViewById(R.id.allocateButton);
        homeButton = findViewById(R.id.homeButton); // Now correctly cast to ImageButton

        mobileInputLayout = findViewById(R.id.mobileInputLayout);
        aadharInputLayout = findViewById(R.id.aadharInputLayout);

        // Set current date to check-in date text view
        checkInDateTextView.setText(dateFormat.format(checkInDate));

        // Setup spinners
        ArrayAdapter<CharSequence> roomTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.room_types, android.R.layout.simple_spinner_item);
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomTypeSpinner.setAdapter(roomTypeAdapter);

        ArrayAdapter<CharSequence> bedAdapter = ArrayAdapter.createFromResource(this,
                R.array.bed_counts, android.R.layout.simple_spinner_item);
        bedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bedSpinner.setAdapter(bedAdapter);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Set click listeners
        checkInDateTextView.setOnClickListener(v -> showDatePickerDialog());
        clearButton.setOnClickListener(v -> clearForm());
        allocateButton.setOnClickListener(v -> allocateRoom());
        homeButton.setOnClickListener(v -> finish()); // Go back to dashboard
    }

    // Rest of your code remains the same
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    checkInDate = calendar.getTime();
                    checkInDateTextView.setText(dateFormat.format(checkInDate));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void clearForm() {
        nameEditText.setText("");
        mobileEditText.setText("");
        nationalityEditText.setText("");
        aadharEditText.setText("");
        daysToStayEditText.setText("");
        addressEditText.setText("");
        calendar = Calendar.getInstance();
        checkInDate = calendar.getTime();
        checkInDateTextView.setText(dateFormat.format(checkInDate));
        roomTypeSpinner.setSelection(0);
        bedSpinner.setSelection(0);
        genderSpinner.setSelection(0);
    }

    private void allocateRoom() {
        // Validate fields
        if (!validateFields()) {
            return;
        }

        String name = nameEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String nationality = nationalityEditText.getText().toString();
        String aadhar = aadharEditText.getText().toString();
        String daysToStay = daysToStayEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String roomType = roomTypeSpinner.getSelectedItem().toString();
        int noOfBeds = Integer.parseInt(bedSpinner.getSelectedItem().toString());
        String gender = genderSpinner.getSelectedItem().toString();

        // Create a customer object
        Customer customer = new Customer();
        customer.setName(name);
        customer.setMobileNo(mobile);
        customer.setGender(gender);
        customer.setNationality(nationality);
        customer.setAadhar(aadhar);
        customer.setCheckInDate(checkInDate);
        customer.setNoOfDays(Integer.parseInt(daysToStay));
        customer.setRoomType(roomType);
        customer.setAddress(address);
        customer.setNoOfBeds(noOfBeds);

        // Alternative room type handling
        String[] roomTypeOptions = {"Standard", "Deluxe", "AC", "Non-AC"};
        roomType = mapRoomType(roomType);

        // Get available room and allocate
        new GetAvailableRoomTask(roomType, customer).execute();
    }

    private String mapRoomType(String inputRoomType) {
        // Map user-friendly room types to database room types
        switch (inputRoomType.toLowerCase()) {
            case "ac room":
            case "ac":
                return "Deluxe";
            case "non-ac room":
            case "non-ac":
                return "Standard";
            case "single":
                return "Standard";
            case "double":
                return "Deluxe";
            default:
                return inputRoomType;
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError("Name is required");
            isValid = false;
        }

        String mobile = mobileEditText.getText().toString();
        if (mobile.isEmpty()) {
            mobileInputLayout.setError("Mobile number is required");
            isValid = false;
        } else if (mobile.length() != 10) {
            mobileInputLayout.setError("Mobile number must be 10 digits");
            isValid = false;
        } else {
            mobileInputLayout.setError(null);
        }

        String aadhar = aadharEditText.getText().toString();
        if (aadhar.isEmpty()) {
            aadharInputLayout.setError("Aadhar number is required");
            isValid = false;
        } else if (aadhar.length() != 12) {
            aadharInputLayout.setError("Aadhar number must be 12 digits");
            isValid = false;
        } else {
            aadharInputLayout.setError(null);
        }

        if (nationalityEditText.getText().toString().isEmpty()) {
            nationalityEditText.setError("Nationality is required");
            isValid = false;
        }

        if (daysToStayEditText.getText().toString().isEmpty()) {
            daysToStayEditText.setError("Number of days to stay is required");
            isValid = false;
        }

        return isValid;
    }

    private class GetAvailableRoomTask extends AsyncTask<Void, Void, Integer> {
        private String roomType;
        private Customer customer;
        private String errorMessage;

        public GetAvailableRoomTask(String roomType, Customer customer) {
            this.roomType = roomType;
            this.customer = customer;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            final int[] roomNoArray = new int[1];
            final boolean[] roomFound = new boolean[1];

            dbManager.getAvailableRoom(roomType, new sqlitemanager.RoomCallback() {
                @Override
                public void onRoomFound(int roomNo) {
                    roomNoArray[0] = roomNo;
                    roomFound[0] = true;
                    Log.d("RoomAllocation", "Room found: " + roomNo);
                }

                @Override
                public void onFailure(String errorMessage) {
                    GetAvailableRoomTask.this.errorMessage = errorMessage;
                    roomFound[0] = false;
                    Log.e("RoomAllocation", "Room allocation failed: " + errorMessage);
                }
            });

            // Wait for room allocation result
            long startTime = System.currentTimeMillis();
            while (!roomFound[0] && (System.currentTimeMillis() - startTime) < 5000) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.e("RoomAllocation", "Thread interrupted", e);
                    break;
                }
            }

            return roomFound[0] ? roomNoArray[0] : -1;
        }

        @Override
        protected void onPostExecute(Integer roomNo) {
            if (roomNo != -1) {
                customer.setRoomNoAllocated(roomNo);
                new AddCustomerTask(customer).execute();
            } else {
                Toast.makeText(CheckInActivity.this,
                        errorMessage != null ? errorMessage : "Failed to allocate room",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AddCustomerTask extends AsyncTask<Void, Void, Integer> {
        private Customer customer;
        private String errorMessage;

        public AddCustomerTask(Customer customer) {
            this.customer = customer;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                dbManager.addCustomer(customer, new sqlitemanager.AddCustomerCallback() {
                    @Override
                    public void onSuccess(int roomNo) {
                        // Room allocated successfully
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        AddCustomerTask.this.errorMessage = errorMessage;
                    }
                });
                return customer.getRoomNoAllocated();
            } catch (Exception e) {
                errorMessage = e.getMessage();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer roomNo) {
            if (roomNo != -1) {
                Toast.makeText(CheckInActivity.this,
                        "Room allocated successfully! Your Room Number is " + roomNo,
                        Toast.LENGTH_LONG).show();
                clearForm();
            } else {
                Toast.makeText(CheckInActivity.this,
                        errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}