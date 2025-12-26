package com.example.mhealth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser les vues
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        // Utiliser getInstance() au lieu de new DatabaseHelper()
        dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.getWritableDatabase();

        // Listener pour le bouton de connexion
        btnLogin.setOnClickListener(v -> loginUser());

        // Listener pour la redirection vers l'inscription
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validations
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier l'utilisateur dans la base de données
        Cursor cursor = dbHelper.checkUser(email, password);

        if (cursor != null && cursor.moveToFirst()) {
            // Récupérer les informations de l'utilisateur
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
            int statusIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS);
            int roleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ROLE);

            int userId = cursor.getInt(idIndex);
            String userEmail = cursor.getString(emailIndex);
            String status = cursor.getString(statusIndex);
            String role = cursor.getString(roleIndex);

            cursor.close();

            // Vérifier le statut du compte
            if ("en_attente".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
                Toast.makeText(this, "Votre compte est en attente d'approbation", Toast.LENGTH_LONG).show();
                return;
            }

            if ("refuse".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status)) {
                Toast.makeText(this, "Votre compte a été refusé", Toast.LENGTH_LONG).show();
                return;
            }

            // Connexion réussie - Rediriger selon le rôle
            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

            Intent intent;
            if ("admin".equalsIgnoreCase(role)) {
                // Rediriger vers le dashboard admin
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            } else if ("medecin".equalsIgnoreCase(role) || "doctor".equalsIgnoreCase(role)) {
                // Rediriger vers le dashboard médecin
                // intent = new Intent(LoginActivity.this, DoctorDashboardActivity.class);
                // Pour l'instant, utiliser un dashboard générique
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            } else {
                // Rediriger vers le dashboard patient
                intent = new Intent(LoginActivity.this, PatientDashboardActivity.class);
            }

            // Passer les informations de l'utilisateur
            intent.putExtra("USER_ID", userId);
            intent.putExtra("USER_EMAIL", userEmail);
            intent.putExtra("USER_ROLE", role);

            startActivity(intent);
            finish(); // Fermer l'activité de connexion
        } else {
            if (cursor != null) {
                cursor.close();
            }
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }
    }
}