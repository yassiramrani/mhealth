package com.example.mhealth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static final String DATABASE_NAME = "mhealth.db";
    private static final int DATABASE_VERSION = 10; // Version finale stable

    // Table USERS
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_MEDECIN_ID_FK = "medecin_id";

    // Table RDV
    public static final String TABLE_RDV = "rendez_vous";
    public static final String COLUMN_RDV_ID = "id";
    public static final String COLUMN_PATIENT_ID = "patient_id";
    public static final String COLUMN_RDV_MEDECIN_ID_FK = "medecin_id";
    public static final String COLUMN_DATE_RDV = "date_rdv";
    public static final String COLUMN_HEURE_RDV = "heure_rdv";
    public static final String COLUMN_MOTIF = "motif";
    public static final String COLUMN_RDV_STATUS = "status";

    private static final String CREATE_TABLE_USERS_SQL = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FULL_NAME + " TEXT NOT NULL, " +
            COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_ROLE + " TEXT NOT NULL, " +
            COLUMN_STATUS + " TEXT NOT NULL, " +
            COLUMN_PHONE + " TEXT, " +
            COLUMN_DOB + " TEXT, " +
            COLUMN_GENDER + " TEXT, " +
            COLUMN_WEIGHT + " REAL, " +
            COLUMN_HEIGHT + " REAL, " +
            COLUMN_MEDECIN_ID_FK + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_MEDECIN_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE SET NULL);";

    private static final String CREATE_TABLE_RDV_SQL = "CREATE TABLE " + TABLE_RDV + " (" +
            COLUMN_RDV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PATIENT_ID + " INTEGER NOT NULL, " +
            COLUMN_RDV_MEDECIN_ID_FK + " INTEGER NOT NULL, " +
            COLUMN_DATE_RDV + " TEXT NOT NULL, " +
            COLUMN_HEURE_RDV + " TEXT NOT NULL, " +
            COLUMN_MOTIF + " TEXT, " +
            COLUMN_RDV_STATUS + " TEXT NOT NULL, " +
            "FOREIGN KEY(" + COLUMN_PATIENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE, " +
            "FOREIGN KEY(" + COLUMN_RDV_MEDECIN_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS_SQL);
        db.execSQL(CREATE_TABLE_RDV_SQL);
        createAdminInDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RDV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String fullName, String email, String phone, String dob, String gender, double weight, double height, String password, String role, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_PASSWORD, hashPassword(password));
        values.put(COLUMN_ROLE, role);
        values.put(COLUMN_STATUS, status);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean addPatient(String fullName, String email, String phone, String dob, String gender, double weight, double height, String password) {
        return addUser(fullName, email, phone, dob, gender, weight, height, password, "patient", "en_attente");
    }

    public Cursor checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, new String[]{COLUMN_ID, COLUMN_ROLE, COLUMN_STATUS, COLUMN_FULL_NAME, COLUMN_EMAIL}, COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, hashPassword(password)}, null, null, null);
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null)) {
            if (cursor.moveToFirst()) {
                do {
                    userList.add(new User(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))));
                } while (cursor.moveToNext());
            }
        }
        return userList;
    }

    public List<User> getUsersByRole(String role) {
        List<User> userList = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.query(TABLE_USERS, null, COLUMN_ROLE + " = ?", new String[]{role}, null, null, COLUMN_FULL_NAME)) {
            if (cursor.moveToFirst()) {
                do {
                    userList.add(new User(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))));
                } while (cursor.moveToNext());
            }
        }
        return userList;
    }

    public User getUserById(int userId) {
        User user = null;
        try (SQLiteDatabase db = getReadableDatabase(); Cursor c = db.query(TABLE_USERS, null, COLUMN_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null)) {
            if (c.moveToFirst()) {
                user = new User(c.getInt(c.getColumnIndexOrThrow(COLUMN_ID)), c.getString(c.getColumnIndexOrThrow(COLUMN_FULL_NAME)), c.getString(c.getColumnIndexOrThrow(COLUMN_EMAIL)), c.getString(c.getColumnIndexOrThrow(COLUMN_STATUS)), c.getString(c.getColumnIndexOrThrow(COLUMN_ROLE)));
            }
        }
        return user;
    }

    public boolean updateUserStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        int rows = db.update(TABLE_USERS, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public int getUserCount() {
        int count = 0;
        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null)) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }
        return count;
    }

    public boolean addRendezVous(int patientId, int medecinId, String dateRdv, String heureRdv, String motif) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PATIENT_ID, patientId);
        values.put(COLUMN_RDV_MEDECIN_ID_FK, medecinId);
        values.put(COLUMN_DATE_RDV, dateRdv);
        values.put(COLUMN_HEURE_RDV, heureRdv);
        values.put(COLUMN_MOTIF, motif);
        values.put(COLUMN_RDV_STATUS, "en_attente");
        long result = db.insert(TABLE_RDV, null, values);
        return result != -1;
    }

    public boolean updateRdvStatus(int rdvId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RDV_STATUS, newStatus);
        int rows = db.update(TABLE_RDV, values, COLUMN_RDV_ID + " = ?", new String[]{String.valueOf(rdvId)});
        return rows > 0;
    }

    public Cursor getRendezVousEnAttente() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_RDV, null, COLUMN_RDV_STATUS + " = ?", new String[]{"en_attente"}, null, null, COLUMN_DATE_RDV + " ASC");
    }

    public int getActiveRdvCount() {
        int count = 0;
        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RDV + " WHERE " + COLUMN_RDV_STATUS + " = ?", new String[]{"en_attente"})) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }
        return count;
    }

    public List<User> getPatientsForMedecin(int medecinId) {
        List<User> patientList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String rawQuery = "SELECT DISTINCT u.* FROM " + TABLE_USERS + " u INNER JOIN " + TABLE_RDV + " r ON u." + COLUMN_ID + " = r." + COLUMN_PATIENT_ID + " WHERE r." + COLUMN_RDV_MEDECIN_ID_FK + " = ?";
        try (Cursor c = db.rawQuery(rawQuery, new String[]{String.valueOf(medecinId)})) {
            if (c.moveToFirst()) {
                do {
                    patientList.add(new User(c.getInt(c.getColumnIndexOrThrow(COLUMN_ID)), c.getString(c.getColumnIndexOrThrow(COLUMN_FULL_NAME)), c.getString(c.getColumnIndexOrThrow(COLUMN_EMAIL)), c.getString(c.getColumnIndexOrThrow(COLUMN_STATUS)), c.getString(c.getColumnIndexOrThrow(COLUMN_ROLE))));
                } while (c.moveToNext());
            }
        }
        return patientList;
    }

    public RendezVous getNextAppointmentForPatient(int patientId) {
        RendezVous rdv = null;
        SQLiteDatabase db = getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try (Cursor c = db.query(TABLE_RDV, null, COLUMN_PATIENT_ID + " = ? AND " + COLUMN_DATE_RDV + " >= ? AND " + COLUMN_RDV_STATUS + " = ?", new String[]{String.valueOf(patientId), currentDate, "confirmé"}, null, null, COLUMN_DATE_RDV + " ASC, " + COLUMN_HEURE_RDV + " ASC", "1")) {
            if (c.moveToFirst()) {
                rdv = new RendezVous(c.getInt(c.getColumnIndexOrThrow(COLUMN_RDV_ID)), c.getInt(c.getColumnIndexOrThrow(COLUMN_PATIENT_ID)), c.getString(c.getColumnIndexOrThrow(COLUMN_DATE_RDV)), c.getString(c.getColumnIndexOrThrow(COLUMN_HEURE_RDV)), c.getString(c.getColumnIndexOrThrow(COLUMN_MOTIF)), c.getString(c.getColumnIndexOrThrow(COLUMN_RDV_STATUS)), c.getInt(c.getColumnIndexOrThrow(COLUMN_RDV_MEDECIN_ID_FK)));
            }
        }
        return rdv;
    }

    public RendezVous getLatestProcessedAppointment(int patientId) {
        RendezVous rdv = null;
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = {String.valueOf(patientId), "en_attente"};
        try (Cursor c = db.query(TABLE_RDV, null, COLUMN_PATIENT_ID + " = ? AND " + COLUMN_RDV_STATUS + " != ?", selectionArgs, null, null, COLUMN_RDV_ID + " DESC", "1")) {
            if (c.moveToFirst()) {
                rdv = new RendezVous(c.getInt(c.getColumnIndexOrThrow(COLUMN_RDV_ID)), c.getInt(c.getColumnIndexOrThrow(COLUMN_PATIENT_ID)), c.getString(c.getColumnIndexOrThrow(COLUMN_DATE_RDV)), c.getString(c.getColumnIndexOrThrow(COLUMN_HEURE_RDV)), c.getString(c.getColumnIndexOrThrow(COLUMN_MOTIF)), c.getString(c.getColumnIndexOrThrow(COLUMN_RDV_STATUS)), c.getInt(c.getColumnIndexOrThrow(COLUMN_RDV_MEDECIN_ID_FK)));
            }
        }
        return rdv;
    }

    public Cursor getRendezVousForMedecin(int medecinId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_RDV, null, COLUMN_RDV_MEDECIN_ID_FK + " = ?", new String[]{String.valueOf(medecinId)}, null, null, COLUMN_DATE_RDV + " ASC");
    }

    public boolean updateSecretaireMedecin(int secretaireId, int medecinId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDECIN_ID_FK, medecinId);
        int rows = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(secretaireId)});
        return rows > 0;
    }

    public int getAssociatedMedecinId(int secretaireId) {
        int medecinId = -1;
        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_MEDECIN_ID_FK}, COLUMN_ID + " = ?", new String[]{String.valueOf(secretaireId)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                medecinId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDECIN_ID_FK));
            }
        }
        return medecinId;
    }

    public User getSecretaryForMedecin(int medecinId) {
        User secretary = null;
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_FULL_NAME, COLUMN_EMAIL, COLUMN_STATUS, COLUMN_ROLE};
        String selection = COLUMN_ROLE + " = ? AND " + COLUMN_MEDECIN_ID_FK + " = ?";
        String[] selectionArgs = {"secretaire", String.valueOf(medecinId)};
        try (Cursor c = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null, "1")) {
            if (c.moveToFirst()) {
                secretary = new User(c.getInt(c.getColumnIndexOrThrow(COLUMN_ID)), c.getString(c.getColumnIndexOrThrow(COLUMN_FULL_NAME)), c.getString(c.getColumnIndexOrThrow(COLUMN_EMAIL)), c.getString(c.getColumnIndexOrThrow(COLUMN_STATUS)), c.getString(c.getColumnIndexOrThrow(COLUMN_ROLE)));
            }
        }
        return secretary;
    }

    public void createAdminIfNotExists() {
        SQLiteDatabase db = getWritableDatabase();
        createAdminInDatabase(db);
    }

    private void createAdminInDatabase(SQLiteDatabase db) {
        try (Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_ROLE + " = ?", new String[]{"admin"}, null, null, null)) {
            if (!cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_FULL_NAME, "Admin");
                values.put(COLUMN_EMAIL, "admin@mhealth.com");
                values.put(COLUMN_PASSWORD, hashPassword("admin123"));
                values.put(COLUMN_ROLE, "admin");
                values.put(COLUMN_STATUS, "actif");
                db.insert(TABLE_USERS, null, values);
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }
}