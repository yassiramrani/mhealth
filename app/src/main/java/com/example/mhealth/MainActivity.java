// Dans MainActivity.java
package com.example.mhealth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Assurez-vous d'importer vos vues (Button, EditText, etc.)
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    // ... autres vues comme le lien d'inscription (par exemple, tvSignUpRedirect)

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assurez-vous que c'est le bon layout de connexion
        setContentView(R.layout.activity_main);

        // Assurez-vous que ces IDs correspondent à votre layout activity_main.xml
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());

        // N'oubliez pas le listener pour rediriger vers SignUpActivity si nécessaire
        // findViewById(R.id.tvSignUpRedirect).setOnClickListener(...);
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier dans la base de données
        Cursor cursor = databaseHelper.checkUser(email, password);

        // Si le curseur n'est pas nul et contient au moins un résultat
        if (cursor != null && cursor.moveToFirst()) {

            // L'utilisateur existe, vérifions son statut
            // Assurez-vous que la colonne COLUMN_STATUS est bien récupérée dans votre méthode checkUser
            int statusColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS);
            String status = cursor.getString(statusColumnIndex);

            // Vérifiez le statut et redirigez en conséquence
            if ("approuvé".equals(status)) {
                // Le compte est approuvé, procédez à la connexion normale
                int roleColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ROLE);
                String role = cursor.getString(roleColumnIndex);

                Toast.makeText(this, "Connexion réussie ! Rôle : " + role, Toast.LENGTH_SHORT).show();

                // Ajoutez ici la logique de redirection en fonction du rôle
                if ("patient".equals(role)) {
                    // Intent intent = new Intent(MainActivity.this, PatientDashboardActivity.class);
                    // startActivity(intent);
                    // finish(); // Optionnel : fermer l'activité de connexion
                } else if ("medecin".equals(role)) {
                    // Intent intent = new Intent(MainActivity.this, DoctorDashboardActivity.class);
                    // startActivity(intent);
                    // finish();
                }

            } else if ("en_attente".equals(status)) {
                // Le compte est en attente, redirigez vers la page d'attente
                Intent intent = new Intent(MainActivity.this, WaitingForConfirmationActivity.class);
                startActivity(intent);
            } else {
                // Cas où le statut n'est ni "approuvé" ni "en_attente"
                Toast.makeText(this, "Le statut de votre compte est inconnu.", Toast.LENGTH_SHORT).show();
            }

            cursor.close(); // Très important de fermer le curseur

        } else {
            // Identifiants incorrects ou utilisateur non trouvé
            Toast.makeText(this, "Email ou mot de passe incorrect.", Toast.LENGTH_SHORT).show();
        }
    }
}
