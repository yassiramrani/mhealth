package com.example.mhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SecretaireDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerRdvSecretaire;
    private RdvAdapter adapter;
    private List<RendezVous> rdvList;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private int currentUserId;
    private TextView tvAssociatedMedecin, tvSecretaireWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretaire_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("MHealthPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("USER_ID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Erreur critique : Utilisateur non identifié.", Toast.LENGTH_LONG).show();
            logout();
            return;
        }

        recyclerRdvSecretaire = findViewById(R.id.recyclerRdvSecretaire);
        recyclerRdvSecretaire.setLayoutManager(new LinearLayoutManager(this));
        tvAssociatedMedecin = findViewById(R.id.tvAssociatedMedecin);
        tvSecretaireWelcome = findViewById(R.id.tvSecretaireWelcome);

        loadSecretaireInfo();
        loadMedecinInfoAndAppointments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.secretaire_dashboard_menu, menu);
        // --- CORRECTION : AJOUT D'UN LISTENER MANUEL ---
        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        logoutItem.setOnMenuItemClickListener(item -> {
            logout();
            return true;
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_profile) {
            showProfileDialog();
            return true;
        } else if (itemId == R.id.action_logout) {
            // La logique est maintenant dans le listener manuel, mais on garde ce code par sécurité
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSecretaireInfo();
        loadMedecinInfoAndAppointments();
    }

    private void showProfileDialog() {
        String secretaireName = sharedPreferences.getString("USER_FULL_NAME", "N/A");
        String secretaireEmail = sharedPreferences.getString("USER_EMAIL", "N/A");
        int medecinId = dbHelper.getAssociatedMedecinId(currentUserId);
        String medecinName = "Aucun médecin associé";

        if (medecinId != -1) {
            User medecin = dbHelper.getUserById(medecinId);
            if (medecin != null) {
                medecinName = medecin.getFullName();
            }
        }

        StringBuilder profileInfo = new StringBuilder();
        profileInfo.append("Nom : ").append(secretaireName).append("\n\n");
        profileInfo.append("Email : ").append(secretaireEmail).append("\n\n");
        profileInfo.append("Gère le médecin : ").append(medecinName);

        new AlertDialog.Builder(this)
                .setTitle("Mon Profil")
                .setMessage(profileInfo.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void loadSecretaireInfo() {
        String secretaireName = sharedPreferences.getString("USER_FULL_NAME", "Utilisateur");
        tvSecretaireWelcome.setText("Bienvenue, " + secretaireName);
    }

    private void loadMedecinInfoAndAppointments() {
        int medecinId = dbHelper.getAssociatedMedecinId(currentUserId);
        rdvList = new ArrayList<>();

        if (medecinId != -1) {
            User medecin = dbHelper.getUserById(medecinId);
            if (medecin != null) {
                tvAssociatedMedecin.setText("Rendez-vous pour Dr. " + medecin.getFullName());

                Cursor cursor = dbHelper.getRendezVousForMedecin(medecinId);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RDV_ID));
                        int patientId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PATIENT_ID));
                        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_RDV));
                        String heure = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEURE_RDV));
                        String motif = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOTIF));
                        String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RDV_STATUS));
                        int rdvMedecinId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RDV_MEDECIN_ID_FK)); // Correction
                        rdvList.add(new RendezVous(id, patientId, date, heure, motif, status, rdvMedecinId)); // Correction
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            } else {
                 tvAssociatedMedecin.setText("Médecin associé non trouvé.");
            }
        } else {
            tvAssociatedMedecin.setText("Aucun médecin associé. Veuillez contacter l\'administrateur.");
        }

        if (adapter == null) {
            adapter = new RdvAdapter(this, rdvList);
            recyclerRdvSecretaire.setAdapter(adapter);
        } else {
            adapter.updateData(rdvList);
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
