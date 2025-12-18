// Dans LoginActivity.java

package com.example.mhealth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignup; // Renommé pour correspondre au layout
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assurez-vous que le layout est activity_login.xml
        setContentView(R.layout.activity_login);

        // Initialisation des vues avec les bons IDs
        // VÉRIFIEZ QUE CES IDS SONT DANS activity_login.xml
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup); // ID pour le texte "Create Account"

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase(); // <-- LIGNE MANQUANTE

        // Listener pour le bouton de connexion
        btnLogin.setOnClickListener(v -> loginUser());

        // Listener pour la redirection vers l'inscription
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        // Récupération des saisies
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérification dans la base de données
        Cursor cursor = databaseHelper.checkUser(email, password);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int statusColumnIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS);
                String status = cursor.getString(statusColumnIndex);

                if ("en_attente".equals(status)) {
                    // Compte en attente -> Page d'attente
                    Intent intent = new Intent(LoginActivity.this, WaitingForConfirmationActivity.class);
                    startActivity(intent);

                } else if ("approuvé".equals(status)) {
                    // Compte approuvé -> Logique de connexion réussie
                    int roleColumnIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE);
                    String role = cursor.getString(roleColumnIndex);
                    Toast.makeText(this, "Connexion réussie ! Rôle : " + role, Toast.LENGTH_SHORT).show();
                    // ... rediriger vers les dashboards Patient/Médecin ici ...

                } else {
                    // Statut inconnu
                    Toast.makeText(this, "Le statut de votre compte est inconnu.", Toast.LENGTH_SHORT).show();
                }
            } finally {
                cursor.close(); // Fermer le curseur est crucial
            }
        } else {
            // Identifiants incorrects
            Toast.makeText(this, "Email ou mot de passe incorrect.", Toast.LENGTH_SHORT).show();
        }
    }
}
