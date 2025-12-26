package com.example.mhealth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.Toast;

public class AdminDashboardActivity extends AppCompatActivity {

    CardView cardUsers, cardCourses, cardPayments, cardSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        cardUsers = findViewById(R.id.cardUsers);
        cardCourses = findViewById(R.id.cardCourses);
        cardPayments = findViewById(R.id.cardPayments);
        cardSettings = findViewById(R.id.cardSettings);

        // 🔹 Ouvrir la liste des utilisateurs
        cardUsers.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    UserActivity.class
            );
            startActivity(intent);
        });

        cardCourses.setOnClickListener(v ->
                Toast.makeText(this, "Gestion des cours", Toast.LENGTH_SHORT).show()
        );

        cardPayments.setOnClickListener(v ->
                Toast.makeText(this, "Paiements", Toast.LENGTH_SHORT).show()
        );

        cardSettings.setOnClickListener(v ->
                Toast.makeText(this, "Paramètres", Toast.LENGTH_SHORT).show()
        );
    }
}
