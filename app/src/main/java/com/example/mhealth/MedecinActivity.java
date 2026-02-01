package com.example.mhealth;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedecinActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private DatabaseHelper dbHelper;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Initialiser les vues
        recyclerView = findViewById(R.id.recyclerUsers);
        btnBack = findViewById(R.id.btnBack);

        // Configurer RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Initialiser DatabaseHelper
        dbHelper = DatabaseHelper.getInstance(this);

        // Charger les médecins
        loadMedecinsFromDatabase();

        // Bouton retour
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadMedecinsFromDatabase() {
        userList = dbHelper.getUsersByRole("medecin");

        if (userList == null || userList.isEmpty()) {
            Toast.makeText(this, "Aucun médecin trouvé", Toast.LENGTH_SHORT).show();
        }

        adapter = new UserAdapter(this, userList, dbHelper);
        recyclerView.setAdapter(adapter);

        Toast.makeText(this, userList.size() + " médecin(s)", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMedecinList();
    }

    private void refreshMedecinList() {
        if (dbHelper != null && adapter != null) {
            List<User> updatedList = dbHelper.getUsersByRole("medecin");
            userList.clear();
            userList.addAll(updatedList);
            adapter.notifyDataSetChanged();
        }
    }
}
