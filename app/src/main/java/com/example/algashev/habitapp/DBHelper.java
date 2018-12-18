package com.example.algashev.habitapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "Notification";
    public static final String ID = "_id";
    public static final String DAYS = "days";
    public static final String STATUS = "status";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String create = "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY, " + DAYS + " TEXT, " + STATUS + " NUMERIC);";
            if (DATABASE_VERSION == 2)
                db.execSQL(create);
        }
        catch (Exception e) {
            int a = 0;
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
        catch (Exception e) {
            int a = 0;
        }

    }
}
