// Créez ce fichier dans votre package (ex: com.example.mhealth)
package com.example.mhealth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Constantes pour la base de données
    private static final String DATABASE_NAME = "mHealth.db";
    private static final int DATABASE_VERSION = 1;

    // Constantes pour la table des utilisateurs (users)
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role"; // ex: "patient", "medecin", "pending"
    public static final String COLUMN_STATUS = "status"; // ex: "pending", "approved"

    // Requête de création de la table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FULL_NAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE + " TEXT, " +
                    COLUMN_STATUS + " TEXT" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE); // Exécute la requête pour créer la table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Méthode pour ajouter un nouvel utilisateur en attente de confirmation
    public boolean addUser(String fullName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password); // Note : Dans une vraie app, il faut hasher le mot de passe !
        values.put(COLUMN_ROLE, "non-assigné"); // Rôle non encore défini
        values.put(COLUMN_STATUS, "en_attente"); // Statut par défaut

        // Insérer la ligne. Retourne -1 en cas d'erreur.
        long result = db.insert(TABLE_USERS, null, values);

        return result != -1; // Retourne true si l'insertion a réussi
    }

    // Méthode pour vérifier les identifiants et le statut d'un utilisateur
    public Cursor checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COLUMN_ID, COLUMN_EMAIL, COLUMN_STATUS, COLUMN_ROLE };
        String selection = COLUMN_EMAIL + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { email, password };

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        return cursor;
    }
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, full_name, email, status FROM users",
                null
        );

        while (c.moveToNext()) {
            list.add(new User(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3)
            ));
        }
        c.close();
        return list;
    }
}
