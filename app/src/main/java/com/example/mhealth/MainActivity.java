package com.example.mhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // CRITICAL CHANGE: This line tells the app to show the Login Screen
        // instead of the "Hello World" screen.
        setContentView(R.layout.activity_login);

        // 1. Initialize the Login Button
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignup = findViewById(R.id.tvSignup);

        // 2. Add Click Listener for Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, we just pretend the login works and go to the Dashboard
                Toast.makeText(MainActivity.this, "Connexion réussie...", Toast.LENGTH_SHORT).show();

                // This command switches the screen from Login to Dashboard
                Intent intent = new Intent(MainActivity.this, PatientDashboardActivity.class);
                startActivity(intent);
            }
        });

        // 3. Add Click Listener for Sign Up
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}