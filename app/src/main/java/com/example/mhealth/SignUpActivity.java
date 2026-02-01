package com.example.mhealth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etDob, etWeight, etHeight, etPassword, etConfirmPassword;
    private RadioGroup rgGender;
    private MaterialButton btnRegister;
    private TextView tvLoginRedirect;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        rgGender = findViewById(R.id.rgGender);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        dbHelper = DatabaseHelper.getInstance(this);

        etDob.setOnClickListener(v -> showDatePicker());

        btnRegister.setOnClickListener(v -> registerPatient());

        // CORRECTION : Rediriger vers LoginActivity
        tvLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDob.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void registerPatient() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedGenderId);
            gender = selectedRadioButton.getText().toString();
        }

        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(dob) ||
                TextUtils.isEmpty(gender) || TextUtils.isEmpty(weightStr) ||
                TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }
        
        double weight, height;
        try {
            weight = Double.parseDouble(weightStr);
            height = Double.parseDouble(heightStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Poids et taille doivent être des nombres valides", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = dbHelper.addPatient(fullName, email, phone, dob, gender, weight, height, password);

        if (isInserted) {
            showSuccessDialog();
        } else {
            Toast.makeText(this, "Erreur lors de l'inscription. L'email existe peut-être déjà.", Toast.LENGTH_LONG).show();
        }
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Inscription Réussie")
                .setMessage("Votre compte a été créé avec succès et est en attente de validation par un administrateur.")
                .setPositiveButton("OK", (dialog, which) -> {
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}