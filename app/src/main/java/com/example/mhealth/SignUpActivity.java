// Dans SignUpActivity.java
package com.example.mhealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLoginRedirect;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        databaseHelper = new DatabaseHelper(this); // Initialiser le helper

        btnRegister.setOnClickListener(v -> registerUser());

        tvLoginRedirect.setOnClickListener(v -> {
            // Rediriger vers l'activité de connexion (MainActivity)
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validations
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ajouter l'utilisateur à la base de données avec le statut "en_attente"
        boolean isInserted = databaseHelper.addUser(fullName, email, password);

        if (isInserted) {
            // Afficher la boîte de dialogue de confirmation
            showConfirmationDialog();

            // *** SIMULATION DE NOTIFICATION POUR L'ADMINISTRATEUR ***
            // Dans une application réelle, vous enverriez une push notification
            // ou un email à l'administrateur.
            // Ici, nous simulons avec un Toast.
            Toast.makeText(this, "Notification envoyée à l'admin pour le compte: " + email, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Erreur lors de l'inscription. L'email existe peut-être déjà.", Toast.LENGTH_LONG).show();
        }
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Inscription Réussie")
                .setMessage("Votre compte est créé et en attente de confirmation par un administrateur. Vous serez notifié une fois votre compte approuvé.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Rediriger vers la page de connexion après que l'utilisateur ait cliqué sur OK
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    finish(); // Termine l'activité d'inscription
                })
                .setCancelable(false) // L'utilisateur doit cliquer sur OK
                .show();
    }
}
