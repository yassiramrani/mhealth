package com.example.mhealth;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private Context context;
    private List<User> patientList;

    public PatientAdapter(Context context, List<User> patientList) {
        this.context = context;
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_patient_for_medecin, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        User patient = patientList.get(position);
        holder.tvPatientName.setText(patient.getFullName());

        // --- LOGIQUE ACTIVÉE ---
        holder.btnCreateOrdonnance.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrdonnanceActivity.class);
            intent.putExtra("PATIENT_ID", patient.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName;
        Button btnCreateOrdonnance;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            btnCreateOrdonnance = itemView.findViewById(R.id.btnCreateOrdonnance);
        }
    }
}