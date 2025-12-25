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
    private static final int DATABASE_VERSION = 4;

    // =======================
    // TABLE & COLUMNS - USERS
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
    // TABLE & COLUMNS - APPOINTMENTS
    // =======================
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COLUMN_APPOINTMENT_ID = "_id";
    public static final String COLUMN_APPOINTMENT_DATE = "appointment_date";
    public static final String COLUMN_APPOINTMENT_TIME = "appointment_time";
    public static final String COLUMN_APPOINTMENT_REASON = "appointment_reason";
    public static final String COLUMN_APPOINTMENT_USER_ID = "user_id";


    // =======================
    // CREATE TABLE QUERY - USERS
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
    // CREATE TABLE QUERY - APPOINTMENTS
    // =======================
    private static final String CREATE_TABLE_APPOINTMENTS =
            "CREATE TABLE " + TABLE_APPOINTMENTS + " ("
                    + COLUMN_APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_APPOINTMENT_USER_ID + " INTEGER, "
                    + COLUMN_APPOINTMENT_DATE + " TEXT, "
                    + COLUMN_APPOINTMENT_TIME + " TEXT, "
                    + COLUMN_APPOINTMENT_REASON + " TEXT, "
                    + "FOREIGN KEY(" + COLUMN_APPOINTMENT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
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
        db.execSQL(CREATE_TABLE_APPOINTMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // =======================
    // GET USER NAME
    // =======================
    public String getUserName(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_FULL_NAME}, COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME));
            cursor.close();
            return name;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // =======================
    // CHECK IF USER EXISTS
    // =======================
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_EMAIL + " = ?",
                new String[]{email}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }


    // =======================
    // ADD APPOINTMENT
    // =======================
    public boolean addAppointment(int userId, String date, String time, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_APPOINTMENT_USER_ID, userId);
        contentValues.put(COLUMN_APPOINTMENT_DATE, date);
        contentValues.put(COLUMN_APPOINTMENT_TIME, time);
        contentValues.put(COLUMN_APPOINTMENT_REASON, reason);
        long result = db.insert(TABLE_APPOINTMENTS, null, contentValues);
        return result != -1;
    }

    // =======================
    // GET UPCOMING APPOINTMENT FOR A SPECIFIC USER
    // =======================
    public Cursor getUpcomingAppointment(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_APPOINTMENTS,
                new String[]{COLUMN_APPOINTMENT_ID, COLUMN_APPOINTMENT_DATE, COLUMN_APPOINTMENT_TIME, COLUMN_APPOINTMENT_REASON},
                COLUMN_APPOINTMENT_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, COLUMN_APPOINTMENT_DATE + " ASC", "1");
    }


    // =======================
    // GET ALL APPOINTMENTS (FOR SECRETARY/ADMIN)
    // =======================
    public Cursor getAllAppointments() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a." + COLUMN_APPOINTMENT_ID + ", u." + COLUMN_FULL_NAME + ", a." + COLUMN_APPOINTMENT_DATE + ", a." + COLUMN_APPOINTMENT_REASON +
                " FROM " + TABLE_APPOINTMENTS + " a, " + TABLE_USERS + " u " +
                " WHERE a." + COLUMN_APPOINTMENT_USER_ID + " = u." + COLUMN_ID +
                " ORDER BY a." + COLUMN_APPOINTMENT_DATE + " DESC";
        return db.rawQuery(query, null);
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
