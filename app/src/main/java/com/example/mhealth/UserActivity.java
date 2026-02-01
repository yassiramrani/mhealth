package com.example.mhealth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private DatabaseHelper dbHelper;
    private ImageButton btnBack;
    private Button btnFilterAll, btnFilterPatients, btnFilterMedecins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Initialiser les vues
        recyclerView = findViewById(R.id.recyclerUsers);
        btnBack = findViewById(R.id.btnBack);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterPatients = findViewById(R.id.btnFilterPatients);
        btnFilterMedecins = findViewById(R.id.btnFilterMedecins);

        // Configurer RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Initialiser DatabaseHelper
        dbHelper = DatabaseHelper.getInstance(this);

        // Créer l'admin UNE SEULE FOIS s'il n'existe pas
        dbHelper.createAdminIfNotExists();

        // Charger les utilisateurs
        loadUsersFromDatabase(null); // Load all users initially

        // Bouton retour
        btnBack.setOnClickListener(v -> finish());

        // Boutons de filtre
        btnFilterAll.setOnClickListener(v -> loadUsersFromDatabase(null));
        btnFilterPatients.setOnClickListener(v -> loadUsersFromDatabase("patient"));
        btnFilterMedecins.setOnClickListener(v -> loadUsersFromDatabase("medecin"));
    }

    private void loadUsersFromDatabase(String role) {
        if (role == null) {
            userList = dbHelper.getAllUsers();
        } else {
            userList = dbHelper.getUsersByRole(role);
        }

        if (userList == null || userList.isEmpty()) {
            Toast.makeText(this, "Aucun utilisateur trouvé", Toast.LENGTH_SHORT).show();
        }

        adapter = new UserAdapter(this, userList, dbHelper);
        recyclerView.setAdapter(adapter);

        Toast.makeText(this, userList.size() + " utilisateur(s)", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserList();
    }

    private void refreshUserList() {
        if (dbHelper != null && adapter != null) {
            // Re-apply the current filter
            // For simplicity, we just reload all users. A more advanced implementation
            // could store the last-used filter and re-apply it.
            loadUsersFromDatabase(null);
        }
    }
}
