package com.example.mhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BookAppointmentActivity extends AppCompatActivity {

    private EditText etAppointmentDate, etAppointmentTime, etAppointmentReason;
    private Button btnValidateAppointment, btnBackToDashboard;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        dbHelper = DatabaseHelper.getInstance(this);

        // Récupérer l'ID de l'utilisateur depuis l'intent
        userId = getIntent().getIntExtra("USER_ID", -1);

        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        etAppointmentTime = findViewById(R.id.etAppointmentTime);
        etAppointmentReason = findViewById(R.id.etAppointmentReason);
        btnValidateAppointment = findViewById(R.id.btnValidateAppointment);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);

        btnValidateAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointment();
            }
        });

        btnBackToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Ferme simplement l'activité actuelle pour revenir à la précédente (le dashboard)
            }
        });
    }

    private void saveAppointment() {
        String date = etAppointmentDate.getText().toString().trim();
        String time = etAppointmentTime.getText().toString().trim();
        String reason = etAppointmentReason.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == -1) {
            Toast.makeText(this, "Erreur : ID utilisateur non trouvé", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = dbHelper.addAppointment(userId, date, time, reason);

        if (isInserted) {
            Toast.makeText(this, "Rendez-vous enregistré avec succès", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BookAppointmentActivity.this, PatientDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish(); // On ferme l'activité de prise de RDV
        } else {
            Toast.makeText(this, "Erreur lors de l'enregistrement du rendez-vous", Toast.LENGTH_SHORT).show();
        }
    }
}
