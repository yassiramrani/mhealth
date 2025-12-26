package com.example.mhealth;

import android.os.Bundle;
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

        // Charger les utilisateurs depuis la base de données
        loadUsersFromDatabase();

        // Configurer le bouton retour
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadUsersFromDatabase() {
        // Récupérer TOUS les utilisateurs depuis la base
        userList = dbHelper.getAllUsers();

        // Vérifier si des utilisateurs existent
        if (userList == null || userList.isEmpty()) {
            Toast.makeText(this, "Aucun utilisateur dans la base de données", Toast.LENGTH_LONG).show();

            // Option 1: Ajouter des utilisateurs de test dans la base
            addTestUsersToDatabase();

            // Recharger après ajout
            userList = dbHelper.getAllUsers();
        }

        // Afficher le nombre d'utilisateurs
        Toast.makeText(this, userList.size() + " utilisateur(s) trouvé(s)", Toast.LENGTH_SHORT).show();

        // Créer et configurer l'adaptateur
        adapter = new UserAdapter(this, userList, dbHelper);
        recyclerView.setAdapter(adapter);
    }

    private void addTestUsersToDatabase() {
        // Ajouter quelques utilisateurs de test directement dans la base
        dbHelper.addPatient(
                "Jean Dupont",
                "jean.dupont@email.com",
                "0612345678",
                "1990-05-15",
                "Homme",
                75.5,
                180.0,
                "password123"
        );

        dbHelper.addPatient(
                "Marie Martin",
                "marie.martin@email.com",
                "0698765432",
                "1985-08-22",
                "Femme",
                65.0,
                165.0,
                "password456"
        );

        dbHelper.addPatient(
                "Pierre Durand",
                "pierre.durand@email.com",
                "0601020304",
                "1995-03-10",
                "Homme",
                80.0,
                175.0,
                "password789"
        );

        Toast.makeText(this, "3 utilisateurs de test ajoutés à la base", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir la liste quand l'activité revient au premier plan
        refreshUserList();
    }

    private void refreshUserList() {
        if (dbHelper != null && adapter != null) {
            List<User> updatedList = dbHelper.getAllUsers();
            userList.clear();
            userList.addAll(updatedList);
            adapter.notifyDataSetChanged();
        }
    }
}