package com.example.mhealth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList; // Add this import
import java.util.List; // Add this import

public class DatabaseHelper extends SQLiteOpenHelper {

    // =======================
    // SINGLETON INSTANCE
    // =======================
    private static DatabaseHelper instance;

    // =======================
    // DATABASE INFO
    // =======================
    private static final String DATABASE_NAME = "mhealth.db";
    private static final int DATABASE_VERSION = 2;

    // =======================
    // TABLE & COLUMNS
    // =======================
    public static final String TABLE_USERS = "users";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_STATUS = "status";

    // =======================
    // CREATE TABLE QUERY
    // =======================
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_FULL_NAME + " TEXT, "
                    + COLUMN_EMAIL + " TEXT UNIQUE, "
                    + COLUMN_PHONE + " TEXT, "
                    + COLUMN_DOB + " TEXT, "
                    + COLUMN_GENDER + " TEXT, "
                    + COLUMN_WEIGHT + " REAL, "
                    + COLUMN_HEIGHT + " REAL, "
                    + COLUMN_PASSWORD + " TEXT, "
                    + COLUMN_ROLE + " TEXT, "
                    + COLUMN_STATUS + " TEXT"
                    + ");";

    // =======================
    // PRIVATE CONSTRUCTOR
    // =======================
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =======================
    // GET INSTANCE (SINGLETON)
    // =======================
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // =======================
    // DATABASE LIFECYCLE
    // =======================
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // =======================
    // ADD PATIENT (SIGN UP)
    // =======================
    public boolean addPatient(String fullName,
                              String email,
                              String phone,
                              String dob,
                              String gender,
                              double weight,
                              double height,
                              String password) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_PASSWORD, password); // ⚠ Hash in production
        values.put(COLUMN_ROLE, "patient");
        values.put(COLUMN_STATUS, "en_attente");

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // =======================
    // CHECK USER (LOGIN)
    // =======================
    public Cursor checkUser(String email, String password) {

        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                COLUMN_ID,
                COLUMN_EMAIL,
                COLUMN_ROLE,
                COLUMN_STATUS
        };

        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { email, password };

        return db.query(
                TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    // =======================
    // UPDATE USER STATUS (NEW METHOD)
    // =======================
    public boolean updateUserStatus(int userId, String newStatus) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, newStatus);

        int rowsAffected = db.update(
                TABLE_USERS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        return rowsAffected > 0;
    }

    // =======================
    // GET ALL PATIENTS (OPTIONAL - FOR YOUR ADAPTER)
    // =======================
    public Cursor getAllPatients() {
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_ROLE + " = ?";
        String[] selectionArgs = { "patient" };

        return db.query(
                TABLE_USERS,
                null, // all columns
                selection,
                selectionArgs,
                null,
                null,
                COLUMN_FULL_NAME + " ASC"
        );
    }

    // =======================
    // GET ALL USERS
    // =======================
    // Dans DatabaseHelper.java
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null, // toutes les colonnes
                null, // pas de condition WHERE
                null, // pas d'arguments
                null, // pas de GROUP BY
                null, // pas de HAVING
                COLUMN_FULL_NAME + " ASC" // tri par nom
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Récupérer les données depuis le curseur
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));

                // Créer l'objet User
                User user = new User(id, name, email, status, role);
                userList.add(user);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return userList;
    }
}