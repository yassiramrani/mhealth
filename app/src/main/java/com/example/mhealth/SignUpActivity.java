package com.example.mhealth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    // Déclaration des variables UI
    TextInputEditText etFullName, etEmail, etPhone, etDob, etWeight, etHeight, etPassword;
    RadioGroup rgGender;
    Button btnRegister;
    TextView tvLoginRedirect;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up); // Assurez-vous que le nom du XML est correct

        // Initialisation de la BDD
        dbHelper = DatabaseHelper.getInstance(this);

        // Liaison avec les IDs du XML
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etPassword = findViewById(R.id.etPassword);
        rgGender = findViewById(R.id.rgGender);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        // --- GESTION DU CALENDRIER (DATE PICKER) ---
        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Le format sera JJ/MM/AAAA
                                etDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // --- CLIC SUR S'INSCRIRE ---
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPatient();
            }
        });

        // --- REDIRECTION VERS LOGIN ---
        tvLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class); // Changez LoginActivity par votre classe
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerPatient() {
        // 1. Récupération des valeurs
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Gestion du Genre (Radio Group)
        String gender = "";
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            gender = selectedRadioButton.getText().toString();
        }

        // 2. Validation simple
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(dob) || TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gender.isEmpty()) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }

        // Conversion Poids/Taille en nombres
        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);

        // 3. Insertion dans la base de données
        // Dans le listener du bouton d'inscription
        boolean isInserted = dbHelper.addPatient(fullName, email, phone, dob, gender, weight, height, password);
        // oubaseHelper.addUser(fullName, email, password, ...); // Autres champs

        if (isInserted) {Toast.makeText(this, "Inscription réussie ! Votre compte est en attente de confirmation.", Toast.LENGTH_LONG).show();
            // Rediriger vers la page de connexion
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'inscription.", Toast.LENGTH_SHORT).show();
        }

    }
}