package com.example.mhealth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}
