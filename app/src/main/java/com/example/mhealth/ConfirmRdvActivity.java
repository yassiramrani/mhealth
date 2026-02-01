package com.example.mhealth;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ConfirmRdvActivity extends AppCompatActivity {

    RecyclerView recyclerRdv;
    RdvAdapter adapter;
    List<RendezVous> rdvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rdv);

        // 🔹 RecyclerView
        recyclerRdv = findViewById(R.id.recyclerRdv);
        recyclerRdv.setLayoutManager(new LinearLayoutManager(this));
        loadRdv();

        // 🔹 Bouton Retour
        Button btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> {
            finish(); // ferme l'Activity et retourne à AdminDashboardActivity
        });
    }

    private void loadRdv() {
        rdvList = new ArrayList<>();
        DatabaseHelper db = DatabaseHelper.getInstance(this);

        Cursor cursor = db.getRendezVousEnAttente();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RDV_ID));
                int patientId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PATIENT_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_RDV));
                String heure = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEURE_RDV));
                String motif = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOTIF));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RDV_STATUS));
                int medecinId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RDV_MEDECIN_ID_FK)); // Correction

                rdvList.add(new RendezVous(id, patientId, date, heure, motif, status, medecinId)); // Correction
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new RdvAdapter(this, rdvList);
        recyclerRdv.setAdapter(adapter);
    }
}
