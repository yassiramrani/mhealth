package com.example.mhealth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegisterLink;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.getWritableDatabase();
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvSignUpRedirect);

        // --- Action Bouton Login ---
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Appel de la nouvelle méthode de connexion, plus propre
                loginUser();
            }
        });

        // --- Lien vers l'inscription ---
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * CORRIGÉ : Cette méthode contient la logique complète du pipeline de connexion.
     */
    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // La méthode checkUser retourne maintenant le statut, grâce à notre correction de DatabaseHelper
        Cursor cursor = dbHelper.checkUser(email, password);

        // Si le curseur a trouvé un utilisateur correspondant
        if (cursor != null && cursor.moveToFirst()) {
            try {
                // --- CORRECTION LOGIQUE ---
                // On récupère le statut depuis le curseur
                // getColumnIndexOrThrow est plus sûr car il plantera si la colonne n'existe pas
                int statusColumnIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS);
                String status = cursor.getString(statusColumnIndex);

                // On vérifie le statut de l'utilisateur
                if ("approuve".equals(status)) {
                    // --- CAS 1 : Le compte est approuvé ---
                    Toast.makeText(LoginActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

                    // Redirection vers le tableau de bord du patient
                    Intent intent = new Intent(LoginActivity.this, PatientDashboardActivity.class);
                    intent.putExtra("USER_EMAIL", email); // Passer l'email est une bonne pratique
                    startActivity(intent);
                    finish(); // Ferme l'activité de connexion

                } else if ("en_attente".equals(status)) {
                    // --- CAS 2 : Le compte est en attente ---
                    // Redirection vers l'écran d'attente
                    Intent intent = new Intent(LoginActivity.this, WaitingForConfirmationActivity.class);
                    startActivity(intent);
                    finish(); // Ferme aussi l'activité de connexion

                } else {
                    // Cas où le statut n'est ni "approuvé" ni "en_attente"
                    Toast.makeText(LoginActivity.this, "Le statut de votre compte est inconnu. Contactez le support.", Toast.LENGTH_LONG).show();
                }

            } finally {
                // Très important : toujours fermer le curseur dans un bloc finally
                cursor.close();
            }

        } else {
            // Identifiants incorrects ou utilisateur non trouvé
            Toast.makeText(LoginActivity.this, "Email ou mot de passe incorrect.", Toast.LENGTH_SHORT).show();
            // Il faut aussi fermer le curseur même en cas d'échec s'il n'est pas nul
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
