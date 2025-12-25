package com.example.mhealth;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewAppointmentsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);

        dbHelper = DatabaseHelper.getInstance(this);
        lvAppointments = findViewById(R.id.lvAppointments);

        insertTestData(); // Ajout des données de test
        displayAppointments();
    }

    private void insertTestData() {
        // On insère des données uniquement si la table est vide
        Cursor cursor = dbHelper.getAllAppointments();
        if (cursor != null && cursor.getCount() == 0) {
            // Ajout d'un patient de test pour satisfaire la contrainte de clé étrangère
            dbHelper.addPatient("Test Patient", "test@patient.com", "12345678", "01/01/1990", "Homme", 70, 180, "password");

            // Ajout des rendez-vous pour ce patient (qui aura l'ID 1)
            dbHelper.addAppointment(1, "2024-05-20", "10:00", "Consultation générale");
            dbHelper.addAppointment(1, "2024-05-21", "14:30", "Suivi post-opératoire");
            dbHelper.addAppointment(1, "2024-05-22", "09:00", "Analyse de sang");
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void displayAppointments() {
        Cursor cursor = dbHelper.getAllAppointments();

        if (cursor != null) {
            String[] fromColumns = {DatabaseHelper.COLUMN_FULL_NAME, DatabaseHelper.COLUMN_APPOINTMENT_REASON};
            int[] toViews = {android.R.id.text1, android.R.id.text2};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2, // Un layout simple avec deux lignes de texte
                    cursor,
                    fromColumns,
                    toViews,
                    0
            );

            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (view.getId() == android.R.id.text1) {
                        int dateColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_APPOINTMENT_DATE);
                        if(dateColumnIndex != -1) {
                             String name = cursor.getString(columnIndex);
                             String date = cursor.getString(dateColumnIndex);
                             TextView tv = (TextView) view;
                             tv.setText(name + " - " + date);
                             return true;
                        }
                    }
                    return false;
                }
            });


            lvAppointments.setAdapter(adapter);
        }
    }
}
