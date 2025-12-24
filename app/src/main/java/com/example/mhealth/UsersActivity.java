package com.example.mhealth;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> userList;
    DatabaseHelper db;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Initialiser le RecyclerView
        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialiser la base de données
        db = new DatabaseHelper(this);
        userList = db.getAllUsers();

        // Configurer l'adaptateur
        adapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(adapter);

        // Bouton retour
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Retour à l'activité précédente
        });
    }
}