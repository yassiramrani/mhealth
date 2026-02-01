package com.example.mhealth;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BookAppointmentActivity extends AppCompatActivity {

    private Spinner spinnerMedecins;
    private EditText etMotif, etDate, etHeure;
    private Button btnBookAppointment;

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private List<User> medecinsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        dbHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("MHealthPrefs", MODE_PRIVATE);

        spinnerMedecins = findViewById(R.id.spinnerMedecins);
        etMotif = findViewById(R.id.etMotif);
        etDate = findViewById(R.id.etDate);
        etHeure = findViewById(R.id.etHeure);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);

        loadMedecinsSpinner();

        etDate.setOnClickListener(v -> showDatePickerDialog());
        etHeure.setOnClickListener(v -> showTimePickerDialog());

        btnBookAppointment.setOnClickListener(v -> bookAppointment());
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
            etDate.setText(formattedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            // --- NOUVELLE RÈGLE : CRÉNEAUX DE 30 MINUTES ---
            if (minute1 == 0 || minute1 == 30) {
                if (hourOfDay >= 10 && hourOfDay < 16) {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    etHeure.setText(formattedTime);
                } else {
                    Toast.makeText(this, "Les heures de consultation sont de 10:00 à 16:00", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Veuillez choisir un créneau de 30 minutes (ex: 10:00, 10:30)", Toast.LENGTH_LONG).show();
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void loadMedecinsSpinner() {
        medecinsList = dbHelper.getUsersByRole("medecin");
        List<String> medecinNames = medecinsList.stream().map(User::getFullName).collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, medecinNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedecins.setAdapter(adapter);
    }

    private void bookAppointment() {
        int selectedMedecinPosition = spinnerMedecins.getSelectedItemPosition();
        if (selectedMedecinPosition < 0) {
            Toast.makeText(this, "Veuillez sélectionner un médecin", Toast.LENGTH_SHORT).show();
            return;
        }

        User selectedMedecin = medecinsList.get(selectedMedecinPosition);
        int medecinId = selectedMedecin.getId();

        int patientId = sharedPreferences.getInt("USER_ID", -1);
        if (patientId == -1) {
            Toast.makeText(this, "Erreur: Patient non identifié", Toast.LENGTH_SHORT).show();
            return;
        }

        String motif = etMotif.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String heure = etHeure.getText().toString().trim();

        if (motif.isEmpty() || date.isEmpty() || heure.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.addRendezVous(patientId, medecinId, date, heure, motif);

        if (success) {
            Toast.makeText(this, "Rendez-vous pris avec succès !", Toast.LENGTH_SHORT).show();
            finish(); // Retourner à l'écran précédent
        } else {
            Toast.makeText(this, "Erreur lors de la prise de rendez-vous", Toast.LENGTH_SHORT).show();
        }
    }
}
