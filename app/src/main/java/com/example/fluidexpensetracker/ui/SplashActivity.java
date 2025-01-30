package com.example.fluidexpensetracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fluidexpensetracker.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Create this layout

//        new Handler().postDelayed(() -> {
//            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(mainIntent);
//            finish(); // Close the splash activity so the user can't go back to it
//        }, 3000); // Delay for 3 seconds (adjust as needed)

        new Handler().postDelayed(() -> {
            Intent loginIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(loginIntent);
            finish(); // Close the splash activity so the user can't go back to it
        }, 3000); // Delay for 3 seconds (adjust as needed)
    }
}