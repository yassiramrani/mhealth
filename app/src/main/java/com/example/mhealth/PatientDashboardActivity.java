package com.example.mhealth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View; // Importez la classe View
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Importez la classe MaterialCardView
import com.google.android.material.card.MaterialCardView;

public class PatientDashboardActivity extends AppCompatActivity {

    // Vues
    private TextView tvWelcomeName;
    private MaterialCardView cardBookAppointment; // **NOUVEAU** : Déclaration de la carte

    // Base de données
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dash);

        // Initialisation de la base de données
        // On utilise le constructeur standard, pas de singleton pour l'instant
        dbHelper = DatabaseHelper.getInstance(this);

        // Liaison avec les vues du layout
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        cardBookAppointment = findViewById(R.id.cardBookAppt); // **NOUVEAU** : Liaison de la carte

        // Gestion du mode Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Charger les données du patient
        loadPatientData();

        // **NOUVEAU** : Définir l'action de clic sur la carte
        cardBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour démarrer l'activité de prise de rendez-vous
                Intent intent = new Intent(PatientDashboardActivity.this, BookAppointmentActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Récupère l'email depuis LoginActivity,
     * cherche le patient dans la base de données
     * et affiche son nom.
     */
    private void loadPatientData() {
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("USER_EMAIL")) {
            String userEmail = intent.getStringExtra("USER_EMAIL");

            Cursor cursor = dbHelper.getUserByEmail(userEmail);

            if (cursor != null) {
                try { // Utiliser un bloc try-finally pour garantir la fermeture du curseur
                    if (cursor.moveToFirst()) {
                        String fullName = cursor.getString(
                                cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULL_NAME)
                        );
                        tvWelcomeName.setText("Bonjour, " + fullName);
                    } else {
                        tvWelcomeName.setText("Bonjour, Patient");
                    }
                } finally {
                    cursor.close(); // Le curseur est fermé ici
                }
            }
        } else {
            tvWelcomeName.setText("Bonjour, Patient");
        }
    }
}
