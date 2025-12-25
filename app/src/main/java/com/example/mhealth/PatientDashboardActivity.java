package com.example.mhealth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;

public class PatientDashboardActivity extends AppCompatActivity {

    private TextView tvWelcomeName, tvDate, tvTime, tvReason;
    private MaterialCardView cardBookAppt;
    private CardView cardAppointment;
    private int userId;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dash);

        dbHelper = DatabaseHelper.getInstance(this);
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        cardBookAppt = findViewById(R.id.cardBookAppt);
        cardAppointment = findViewById(R.id.cardAppointment);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvReason = findViewById(R.id.tvReason);


        // Récupérer l'ID de l'utilisateur depuis l'intent
        userId = getIntent().getIntExtra("USER_ID", -1);

        if (userId != -1) {
            String userName = dbHelper.getUserName(userId);
            if (userName != null) {
                tvWelcomeName.setText("Bonjour, " + userName);
            }
            displayUpcomingAppointment();
        }

        cardBookAppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientDashboardActivity.this, BookAppointmentActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }

    private void displayUpcomingAppointment() {
        Cursor cursor = dbHelper.getUpcomingAppointment(userId);
        if (cursor != null && cursor.moveToFirst()) {
            cardAppointment.setVisibility(View.VISIBLE);
            tvDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_APPOINTMENT_DATE)));
            tvTime.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_APPOINTMENT_TIME)));
            tvReason.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_APPOINTMENT_REASON)));

            cardBookAppt.setEnabled(false);
            ((TextView) cardBookAppt.findViewById(R.id.cardBookApptText)).setText("Rendez-vous programmé");

            cursor.close();
        } else {
            cardAppointment.setVisibility(View.GONE);
            cardBookAppt.setEnabled(true);
            ((TextView) cardBookAppt.findViewById(R.id.cardBookApptText)).setText("Prendre RDV");
        }
    }
}
