package com.example.mhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MedecinDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerPatients;
    private PatientAdapter adapter;
    private List<User> patientList;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private TextView tvWelcomeMedecin, tvSecretaryName, tvPatientCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medecin_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("MHealthPrefs", MODE_PRIVATE);

        tvWelcomeMedecin = findViewById(R.id.tvWelcomeMedecin);
        tvSecretaryName = findViewById(R.id.tvSecretaryName);
        recyclerPatients = findViewById(R.id.recyclerPatients);
        recyclerPatients.setLayoutManager(new LinearLayoutManager(this));
        tvPatientCount = findViewById(R.id.tvPatientCount);

        loadMedecinInfo();
        loadSecretaryInfo();
        loadPatients();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.medecin_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMedecinInfo() {
        String medecinName = sharedPreferences.getString("USER_FULL_NAME", "Dr. inconnu");
        tvWelcomeMedecin.setText("Dr. " + medecinName);
    }

    private void loadSecretaryInfo() {
        int medecinId = sharedPreferences.getInt("USER_ID", -1);
        if (medecinId != -1) {
            User secretary = dbHelper.getSecretaryForMedecin(medecinId);
            if (secretary != null) {
                tvSecretaryName.setText(secretary.getFullName());
            } else {
                tvSecretaryName.setText("Aucune secrétaire assignée");
            }
        }
    }

    private void loadPatients() {
        int medecinId = sharedPreferences.getInt("USER_ID", -1);
        if (medecinId != -1) {
            patientList = dbHelper.getPatientsForMedecin(medecinId);
            adapter = new PatientAdapter(this, patientList);
            recyclerPatients.setAdapter(adapter);
            tvPatientCount.setText(String.valueOf(patientList.size()));
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