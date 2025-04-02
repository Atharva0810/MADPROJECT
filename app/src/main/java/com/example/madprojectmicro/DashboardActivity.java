package com.example.madprojectmicro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView checkInCard, checkOutCard, databaseCard, billCard;
    private ImageButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        checkInCard = findViewById(R.id.checkInCard);
        checkOutCard = findViewById(R.id.checkOutCard);
        databaseCard = findViewById(R.id.databaseCard);
        billCard = findViewById(R.id.billCard);
        logoutButton = findViewById(R.id.logoutButton);

        // Set click listeners
        checkInCard.setOnClickListener(this);
        checkOutCard.setOnClickListener(this);
        databaseCard.setOnClickListener(this);
        billCard.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        if (v.getId() == R.id.checkInCard) {
            intent = new Intent(this, CheckInActivity.class);
        } else if (v.getId() == R.id.checkOutCard) {
            intent = new Intent(this, CheckOutActivity.class);
        } else if (v.getId() == R.id.databaseCard) {
            intent = new Intent(this, DatabaseActivity.class);
        } else if (v.getId() == R.id.billCard) {
            intent = new Intent(this, BillDetailsActivity.class);
        } else if (v.getId() == R.id.logoutButton) {
            intent = new Intent(this, LoginActivity.class);
            finish(); // Close the dashboard activity
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}

