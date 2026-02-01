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

        String initial = user.getFullName() != null && !user.getFullName().isEmpty()
                ? user.getFullName().substring(0, 1).toUpperCase()
                : "?";
        holder.txtInitial.setText(initial);

        holder.txtName.setText(user.getFullName());
        holder.txtEmail.setText(user.getEmail());
        holder.txtRole.setText(user.getRole());
        holder.txtStatus.setText(user.getStatus());

        switch (user.getStatus().toLowerCase()) {
            case "en_attente":
                holder.txtStatus.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
                break;
            case "actif":
                holder.txtStatus.setBackgroundColor(Color.parseColor("#4CAF50")); // Vert
                holder.btnApprove.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.VISIBLE); // Peut-être pour désactiver
                break;
            case "refuse":
                holder.txtStatus.setBackgroundColor(Color.parseColor("#F44336")); // Rouge
                holder.btnApprove.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                break;
            default:
                holder.txtStatus.setBackgroundColor(Color.parseColor("#757575")); // Gris
                break;
        }

        holder.btnApprove.setOnClickListener(v -> {
            boolean success = dbHelper.updateUserStatus(user.getId(), "actif"); // CHANGÉ ICI
            if (success) {
                user.setStatus("actif");
                notifyItemChanged(position);
                Toast.makeText(context, user.getFullName() + " est maintenant actif.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Erreur lors de l\'approbation", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            boolean success = dbHelper.updateUserStatus(user.getId(), "refuse");
            if (success) {
                user.setStatus("refuse");
                notifyItemChanged(position);
                Toast.makeText(context, user.getFullName() + " a été refusé.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Erreur lors du refus", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDetails.setOnClickListener(v -> {
            String details = "Nom: " + user.getFullName() + "\n" +
                    "Email: " + user.getEmail() + "\n" +
                    "Rôle: " + user.getRole() + "\n" +
                    "Statut: " + user.getStatus();
            Toast.makeText(context, details, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
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