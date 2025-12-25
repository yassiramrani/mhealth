package com.example.mhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation DB
        dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.getWritableDatabase();

        // Lier les vues
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvSignUpRedirect);

        // Bouton Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Lien vers inscription
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    /**
     * Méthode complète de connexion
     */
    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.checkUser(email, password);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Récupérer statut et rôle
                String status = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS)
                );

                String role = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE)
                );

                // ===============================
                // CRÉATION SESSION UTILISATEUR
                // ===============================
                SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", email);
                editor.putString("role", role);
                editor.apply();

                // ===============================
                // REDIRECTION SELON STATUT / RÔLE
                // ===============================
                if ("approuve".equals(status)) {

                    Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();

                    if ("patient".equals(role)) {
                        startActivity(new Intent(this, PatientDashboardActivity.class));
                    }
                    // (plus tard : medecin / admin)

                    finish();

                } else if ("en_attente".equals(status)) {

                    startActivity(new Intent(this, WaitingForConfirmationActivity.class));
                    finish();

                } else {
                    Toast.makeText(
                            this,
                            "Statut du compte inconnu. Contactez l'administration.",
                            Toast.LENGTH_LONG
                    ).show();
                }

            } finally {
                cursor.close(); // OBLIGATOIRE
            }

        } else {
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
        }
    }
}
