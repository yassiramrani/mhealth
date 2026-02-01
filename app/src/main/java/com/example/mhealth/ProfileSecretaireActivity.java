package com.example.mhealth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileSecretaireActivity extends AppCompatActivity {

    private TextView tvSecretaireName, tvSecretaireEmail, tvAssociatedMedecinName;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_secretaire);

        dbHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("MHealthPrefs", MODE_PRIVATE);

        tvSecretaireName = findViewById(R.id.tvSecretaireName);
        tvSecretaireEmail = findViewById(R.id.tvSecretaireEmail);
        tvAssociatedMedecinName = findViewById(R.id.tvAssociatedMedecinName);

        loadProfileInfo();
    }

    private void loadProfileInfo() {
        // Informations de la secrétaire
        String secretaireName = sharedPreferences.getString("USER_FULL_NAME", "N/A");
        String secretaireEmail = sharedPreferences.getString("USER_EMAIL", "N/A");
        tvSecretaireName.setText(secretaireName);
        tvSecretaireEmail.setText(secretaireEmail);

        // Informations du médecin associé
        int currentUserId = sharedPreferences.getInt("USER_ID", -1);
        int medecinId = dbHelper.getAssociatedMedecinId(currentUserId);
        if (medecinId != -1) {
            User medecin = dbHelper.getUserById(medecinId);
            if (medecin != null) {
                tvAssociatedMedecinName.setText(medecin.getFullName());
            } else {
                tvAssociatedMedecinName.setText("Médecin non trouvé");
            }
        } else {
            tvAssociatedMedecinName.setText("Aucun médecin associé");
        }
    }
}
