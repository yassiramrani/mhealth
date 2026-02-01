package com.example.mhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSignup;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        dbHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("MHealthPrefs", MODE_PRIVATE);

        // --- CODE DE TEST TEMPORAIRE POUR AJOUTER UN MÉDECIN ---
        addTestDoctor();

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void addTestDoctor() {
        // On vérifie si le médecin existe déjà pour ne pas le créer à chaque fois
        Cursor cursor = dbHelper.checkUser("john.doe@mhealth.com", "password123");
        if (cursor == null || cursor.getCount() == 0) {
            dbHelper.addUser("Dr. John Doe", "john.doe@mhealth.com", "0102030405", "1980-01-01", "Homme", 80.0, 180.0, "password123", "medecin", "actif");
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.checkUser(email, password);

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULL_NAME));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));
            String role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE));

            cursor.close();

            if ("en_attente".equalsIgnoreCase(status)) {
                Intent intent = new Intent(LoginActivity.this, WaitingForConfirmationActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            if ("refuse".equalsIgnoreCase(status)) {
                Toast.makeText(this, "Votre compte a été refusé", Toast.LENGTH_LONG).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("USER_ID", userId);
            editor.putString("USER_EMAIL", userEmail);
            editor.putString("USER_FULL_NAME", fullName);
            editor.putString("USER_ROLE", role);
            editor.apply();

            Intent intent;
            switch (role.toLowerCase()) {
                case "admin":
                    intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    break;
                case "medecin":
                    intent = new Intent(LoginActivity.this, MedecinDashboardActivity.class);
                    break;
                case "secretaire":
                    intent = new Intent(LoginActivity.this, SecretaireDashboardActivity.class);
                    break;
                case "patient":
                    if ("actif".equalsIgnoreCase(status)) {
                        intent = new Intent(LoginActivity.this, PatientDashboardActivity.class);
                    } else {
                        Toast.makeText(this, "Votre compte n\'est pas encore actif.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                default:
                    Toast.makeText(this, "Rôle utilisateur inconnu.", Toast.LENGTH_SHORT).show();
                    return;
            }

            startActivity(intent);
            finish();

        } else {
            if (cursor != null) cursor.close();
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }
    }
}