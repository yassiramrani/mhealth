package com.example.mhealth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity {

    private Spinner spinnerSpecialty, spinnerDoctor;
    private Button btnSelectDate, btnConfirmAppt;
    private TextView tvSelectedDate;
    private EditText etReason;

    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Lier les vues
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty);
        spinnerDoctor = findViewById(R.id.spinnerDoctor);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnConfirmAppt = findViewById(R.id.btnConfirmAppt);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        etReason = findViewById(R.id.etReason);

        // Charger les données de test
        setupSpinners();

        // Sélection de date
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Confirmation
        btnConfirmAppt.setOnClickListener(v -> confirmAppointment());
    }

    // -----------------------------
    // Spinners avec données de test
    // -----------------------------
    private void setupSpinners() {

        // Liste des spécialités
        String[] specialties = {
                "Choisir une spécialité",
                "Cardiologie",
                "Dermatologie",
                "Pédiatrie",
                "Médecine Générale"
        };

        ArrayAdapter<String> specialtyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                specialties
        );
        specialtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialty.setAdapter(specialtyAdapter);

        // Médecins par spécialité (FAKE DATA pour test)
        Map<String, String[]> doctorsMap = new HashMap<>();
        doctorsMap.put("Cardiologie", new String[]{"Dr Ahmed", "Dr Salma"});
        doctorsMap.put("Dermatologie", new String[]{"Dr Lina"});
        doctorsMap.put("Pédiatrie", new String[]{"Dr Youssef"});
        doctorsMap.put("Médecine Générale", new String[]{"Dr Karim", "Dr Nadia"});

        // Listener sur la spécialité
        spinnerSpecialty.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedSpecialty = spinnerSpecialty.getSelectedItem().toString();

                String[] doctors = doctorsMap.get(selectedSpecialty);

                if (doctors == null) {
                    doctors = new String[]{"Choisir un médecin"};
                }

                ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(
                        BookAppointmentActivity.this,
                        android.R.layout.simple_spinner_item,
                        doctors
                );
                doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDoctor.setAdapter(doctorAdapter);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    // -----------------------------
    // DatePicker
    // -----------------------------
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    selectedDate = d + "/" + (m + 1) + "/" + y;
                    tvSelectedDate.setText(selectedDate);
                },
                year, month, day
        );

        dialog.show();
    }

    // -----------------------------
    // Confirmation (test)
    // -----------------------------
    private void confirmAppointment() {

        String specialty = spinnerSpecialty.getSelectedItem().toString();
        String doctor = spinnerDoctor.getSelectedItem().toString();
        String reason = etReason.getText().toString();

        if (specialty.equals("Choisir une spécialité") ||
                doctor.equals("Choisir un médecin") ||
                selectedDate.isEmpty() ||
                reason.isEmpty()) {

            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        String patientEmail = prefs.getString("email", null);

        if (patientEmail == null) {
            Toast.makeText(this, "Session expirée. Veuillez vous reconnecter.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = DatabaseHelper.getInstance(this);

        boolean success = db.addAppointment(
                patientEmail,
                specialty,
                doctor,
                selectedDate,
                reason
        );

        if (success) {
            Toast.makeText(this, "Rendez-vous enregistré avec succès", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
        }
    }

}
