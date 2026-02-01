package com.example.mhealth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RdvAdapter extends RecyclerView.Adapter<RdvAdapter.RdvViewHolder> {

    private Context context;
    private List<RendezVous> rdvList;
    private DatabaseHelper db;

    public RdvAdapter(Context context, List<RendezVous> rdvList) {
        this.context = context;
        this.rdvList = rdvList;
        db = DatabaseHelper.getInstance(context);
    }

    @NonNull
    @Override
    public RdvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rdv, parent, false);
        return new RdvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RdvViewHolder holder, int position) {
        RendezVous rdv = rdvList.get(position);

        holder.tvMotif.setText(rdv.getMotif());
        holder.tvDateHeure.setText(rdv.getDate() + " " + rdv.getHeure());
        holder.tvStatus.setText(rdv.getStatus());

        holder.btnConfirmer.setOnClickListener(v -> {
            db.updateRdvStatus(rdv.getId(), "confirmé");
            // NOUVELLE LOGIQUE : SUPPRIMER L'ÉLÉMENT DE LA LISTE
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                rdvList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
            }
            Toast.makeText(context, "RDV confirmé", Toast.LENGTH_SHORT).show();
        });

        holder.btnRefuser.setOnClickListener(v -> {
            db.updateRdvStatus(rdv.getId(), "refusé");
            // NOUVELLE LOGIQUE : SUPPRIMER L'ÉLÉMENT DE LA LISTE
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                rdvList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
            }
            Toast.makeText(context, "RDV refusé", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return rdvList.size();
    }

    public void updateData(List<RendezVous> newRdvList) {
        this.rdvList.clear();
        this.rdvList.addAll(newRdvList);
        notifyDataSetChanged();
    }

    public static class RdvViewHolder extends RecyclerView.ViewHolder {
        TextView tvMotif, tvDateHeure, tvStatus;
        Button btnConfirmer, btnRefuser;

        public RdvViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMotif = itemView.findViewById(R.id.tvMotif);
            tvDateHeure = itemView.findViewById(R.id.tvDateHeure);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnConfirmer = itemView.findViewById(R.id.btnConfirmer);
            btnRefuser = itemView.findViewById(R.id.btnRefuser);
        }
    }
}
