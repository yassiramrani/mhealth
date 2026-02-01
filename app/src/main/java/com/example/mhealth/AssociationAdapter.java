package com.example.mhealth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

public class AssociationAdapter extends RecyclerView.Adapter<AssociationAdapter.AssociationViewHolder> {

    private Context context;
    private List<User> secretaires;
    private List<User> medecins;
    private DatabaseHelper dbHelper;

    public AssociationAdapter(Context context, List<User> secretaires, List<User> medecins) {
        this.context = context;
        this.secretaires = secretaires;
        this.medecins = medecins;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    @NonNull
    @Override
    public AssociationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_association, parent, false);
        return new AssociationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssociationViewHolder holder, int position) {
        User secretaire = secretaires.get(position);
        holder.tvSecretaireName.setText(secretaire.getFullName());

        // Remplir le spinner avec les médecins
        List<String> medecinNames = medecins.stream().map(User::getFullName).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, medecinNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerMedecins.setAdapter(adapter);

        // Pré-sélectionner le médecin associé
        int medecinId = dbHelper.getAssociatedMedecinId(secretaire.getId());
        if (medecinId != -1) {
            for (int i = 0; i < medecins.size(); i++) {
                if (medecins.get(i).getId() == medecinId) {
                    holder.spinnerMedecins.setSelection(i);
                    break;
                }
            }
        }

        holder.btnSaveAssociation.setOnClickListener(v -> {
            int selectedMedecinPosition = holder.spinnerMedecins.getSelectedItemPosition();
            User selectedMedecin = medecins.get(selectedMedecinPosition);

            boolean success = dbHelper.updateSecretaireMedecin(secretaire.getId(), selectedMedecin.getId());

            if (success) {
                Toast.makeText(context, "Association mise à jour", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Erreur de mise à jour", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return secretaires.size();
    }

    static class AssociationViewHolder extends RecyclerView.ViewHolder {
        TextView tvSecretaireName;
        Spinner spinnerMedecins;
        Button btnSaveAssociation;

        public AssociationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSecretaireName = itemView.findViewById(R.id.tvSecretaireName);
            spinnerMedecins = itemView.findViewById(R.id.spinnerMedecins);
            btnSaveAssociation = itemView.findViewById(R.id.btnSaveAssociation);
        }
    }
}
