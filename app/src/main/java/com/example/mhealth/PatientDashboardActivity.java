package com.example.mhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PatientDashboardActivity extends AppCompatActivity {

    private TextView tvWelcomeName, tvDay, tvMonth, tvTime, tvRdvMotif, tvRdvDoctor, tvNotificationText;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private MaterialCardView cardBookAppt, btnLogoutPatient, notificationCard;
    private ImageView ivNotificationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dash);

        dbHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("MHealthPrefs", MODE_PRIVATE);

        // Initialisation des vues
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvDay = findViewById(R.id.tvDay);
        tvMonth = findViewById(R.id.tvMonth);
        tvTime = findViewById(R.id.tvTime);
        tvRdvMotif = findViewById(R.id.tvRdvMotif);
        tvRdvDoctor = findViewById(R.id.tvRdvDoctor);
        cardBookAppt = findViewById(R.id.cardBookAppt);
        btnLogoutPatient = findViewById(R.id.btnLogoutPatient);
        notificationCard = findViewById(R.id.notificationCard);
        ivNotificationIcon = findViewById(R.id.ivNotificationIcon);
        tvNotificationText = findViewById(R.id.tvNotificationText);

        loadPatientInfo();
        loadNextAppointment();
        loadNotification();

        // Listeners
        cardBookAppt.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDashboardActivity.this, BookAppointmentActivity.class);
            startActivity(intent);
        });

        btnLogoutPatient.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPatientInfo();
        loadNextAppointment();
        loadNotification();
    }

    private void loadPatientInfo() {
        String patientName = sharedPreferences.getString("USER_FULL_NAME", "Patient");
        tvWelcomeName.setText("Bonjour, " + patientName);
    }

    private void loadNotification() {
        int patientId = sharedPreferences.getInt("USER_ID", -1);
        if (patientId == -1) return;

        RendezVous lastProcessed = dbHelper.getLatestProcessedAppointment(patientId);

        if (lastProcessed != null) {
            notificationCard.setVisibility(View.VISIBLE);
            String status = lastProcessed.getStatus();
            String date = lastProcessed.getDate();

            if ("confirmé".equalsIgnoreCase(status)) {
                tvNotificationText.setText("Bonne nouvelle ! Votre RDV pour le " + date + " a été confirmé.");
                notificationCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.success_bg));
                tvNotificationText.setTextColor(ContextCompat.getColor(this, R.color.success_text));
                ivNotificationIcon.setImageResource(R.drawable.ic_check);
            } else if ("refusé".equalsIgnoreCase(status)) {
                tvNotificationText.setText("Malheureusement, votre RDV pour le " + date + " a été refusé.");
                notificationCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.error_bg));
                tvNotificationText.setTextColor(ContextCompat.getColor(this, R.color.error_text));
                ivNotificationIcon.setImageResource(R.drawable.ic_close);
            }
        } else {
            notificationCard.setVisibility(View.GONE);
        }
    }

    private void loadNextAppointment() {
        int patientId = sharedPreferences.getInt("USER_ID", -1);
        if (patientId == -1) return;

        RendezVous nextAppointment = dbHelper.getNextAppointmentForPatient(patientId);

        if (nextAppointment != null) {
            try {
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = dbFormat.parse(nextAppointment.getDate());

                if (date != null) {
                    String day = new SimpleDateFormat("dd", Locale.getDefault()).format(date);
                    String month = new SimpleDateFormat("MMM", Locale.getDefault()).format(date).toUpperCase();
                    tvDay.setText(day);
                    tvMonth.setText(month);
                }
            } catch (ParseException e) {
                tvDay.setText("-");
                tvMonth.setText("-");
            }

            tvTime.setText(nextAppointment.getHeure());
            tvRdvMotif.setText(nextAppointment.getMotif());

            User doctor = dbHelper.getUserById(nextAppointment.getMedecinId());
            if (doctor != null) {
                tvRdvDoctor.setText("Dr. " + doctor.getFullName());
            }

        } else {
            tvDay.setText("-");
            tvMonth.setText("-");
            tvTime.setText("");
            tvRdvMotif.setText("Pas de rendez-vous à venir");
            tvRdvDoctor.setText("");
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}