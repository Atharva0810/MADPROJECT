package com.example.madprojectmicro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView progressText;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private sqlitemanager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        // Initialize database manager
        dbManager = sqlitemanager.getInstance(this);

        // Initialize database tables
        dbManager.initializeDatabaseTables(new sqlitemanager.InitCallback() {
            @Override
            public void onSuccess() {
                // Start the progress animation
                startProgress();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(SplashActivity.this,
                        "Database initialization failed: " + errorMessage,
                        Toast.LENGTH_LONG).show();
                // Go to login activity anyway
                startProgress();
            }
        });
    }

    private void startProgress() {
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus++;
                // Update the progress bar and text
                handler.post(() -> {
                    progressBar.setProgress(progressStatus);
                    progressText.setText("Launching Application... " + progressStatus + "%");
                });

                try {
                    // Sleep for 50 milliseconds (adjust for speed)
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // When progress is complete, start the login activity
            handler.post(() -> {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }).start();
    }
}