package com.example.mhealth;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final Context context;
    private final DatabaseHelper dbHelper;

    // Updated constructor with 3 parameters
    public UserAdapter(Context context, List<User> userList, DatabaseHelper dbHelper) {
        this.context = context;
        this.userList = userList;
        this.dbHelper = dbHelper;

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Afficher l'initiale du nom
        String initial = user.getName() != null && !user.getName().isEmpty()
                ? user.getName().substring(0, 1).toUpperCase()
                : "?";
        holder.txtInitial.setText(initial);

        // Afficher les informations
        holder.txtName.setText(user.getName());
        holder.txtEmail.setText(user.getEmail());
        // Updated: Using hardcoded "Patient" since User class doesn't have getRole() method
        holder.txtRole.setText("Patient");
        holder.txtStatus.setText(user.getStatus());

        // Changer la couleur du statut selon l'état
        switch (user.getStatus().toLowerCase()) {
            case "en_attente":
            case "pending":
                holder.txtStatus.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
                holder.txtStatus.setText("En attente");
                break;
            case "approuve":
            case "approved":
                holder.txtStatus.setBackgroundColor(Color.parseColor("#4CAF50")); // Vert
                holder.txtStatus.setText("Approuvé");
                break;
            case "refuse":
            case "rejected":
                holder.txtStatus.setBackgroundColor(Color.parseColor("#F44336")); // Rouge
                holder.txtStatus.setText("Refusé");
                break;
            default:
                holder.txtStatus.setBackgroundColor(Color.parseColor("#757575")); // Gris
                break;
        }

        // Masquer les boutons si déjà traité
        if (user.getStatus().equalsIgnoreCase("approuve") ||
                user.getStatus().equalsIgnoreCase("approved")) {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        } else if (user.getStatus().equalsIgnoreCase("refuse") ||
                user.getStatus().equalsIgnoreCase("rejected")) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.GONE);
        } else {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        }

        // Bouton Approuver
        holder.btnApprove.setOnClickListener(v -> {
            boolean success = dbHelper.updateUserStatus(user.getId(), "approuve");
            if (success) {
                user.setStatus("approuve");
                notifyItemChanged(position);
                Toast.makeText(context, user.getName() + " approuvé ✓", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Erreur lors de l'approbation", Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton Refuser
        holder.btnReject.setOnClickListener(v -> {
            boolean success = dbHelper.updateUserStatus(user.getId(), "refuse");
            if (success) {
                user.setStatus("refuse");
                notifyItemChanged(position);
                Toast.makeText(context, user.getName() + " refusé", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Erreur lors du refus", Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton Détails
        holder.btnDetails.setOnClickListener(v -> {
            // Afficher les détails de l'utilisateur
            showUserDetails(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void showUserDetails(User user) {
        String details = "Nom: " + user.getName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Rôle: Patient\n" + // Hardcoded since User doesn't have getRole()
                "Statut: " + user.getStatus();

        Toast.makeText(context, details, Toast.LENGTH_LONG).show();

        // TODO: Créer une activité ou dialog pour afficher plus de détails
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtInitial, txtName, txtEmail, txtRole, txtStatus;
        Button btnApprove, btnReject, btnDetails;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInitial = itemView.findViewById(R.id.txtInitial);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtRole = itemView.findViewById(R.id.txtRole);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}