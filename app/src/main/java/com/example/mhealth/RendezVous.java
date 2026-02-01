package com.example.mhealth;

public class RendezVous {
    private int id;
    private int patientId;
    private String date;
    private String heure;
    private String motif;
    private String status;
    private int medecinId; // Nouveau champ

    // Ancien constructeur (gardé pour la compatibilité si nécessaire)
    public RendezVous(int id, int patientId, String date, String heure, String motif, String status) {
        this.id = id;
        this.patientId = patientId;
        this.date = date;
        this.heure = heure;
        this.motif = motif;
        this.status = status;
        this.medecinId = -1; // Valeur par défaut
    }

    // NOUVEAU CONSTRUCTEUR
    public RendezVous(int id, int patientId, String date, String heure, String motif, String status, int medecinId) {
        this.id = id;
        this.patientId = patientId;
        this.date = date;
        this.heure = heure;
        this.motif = motif;
        this.status = status;
        this.medecinId = medecinId;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public String getDate() { return date; }
    public String getHeure() { return heure; }
    public String getMotif() { return motif; }
    public String getStatus() { return status; }
    public int getMedecinId() { return medecinId; } // Nouvelle méthode

    public void setStatus(String status) { this.status = status; }
}
