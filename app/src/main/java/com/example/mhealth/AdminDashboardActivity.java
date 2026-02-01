package com.example.mhealth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminWelcome;
    private Button btnManageUsers, btnAddUser;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = DatabaseHelper.getInstance(this);

        // Initialisation des vues
        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);
        btnManageUsers = findViewById(R.id.cardManageUsers);
        btnAddUser = findViewById(R.id.cardAddUser);

        // Listeners
        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, UserActivity.class);
            startActivity(intent);
        });

        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddUserActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}