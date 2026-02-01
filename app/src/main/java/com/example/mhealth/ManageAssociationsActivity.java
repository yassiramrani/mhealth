package com.example.mhealth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManageAssociationsActivity extends AppCompatActivity {

    private RecyclerView recyclerAssociations;
    private AssociationAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_associations);

        dbHelper = DatabaseHelper.getInstance(this);

        recyclerAssociations = findViewById(R.id.recyclerAssociations);
        recyclerAssociations.setLayoutManager(new LinearLayoutManager(this));

        loadAssociations();
    }

    private void loadAssociations() {
        List<User> secretaires = dbHelper.getUsersByRole("secretaire");
        List<User> medecins = dbHelper.getUsersByRole("medecin");

        adapter = new AssociationAdapter(this, secretaires, medecins);
        recyclerAssociations.setAdapter(adapter);
    }
}
