package com.zarar.bankers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "bankers.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "runs";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, input TEXT NOT NULL, status TEXT NOT NULL, sequence TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insertRun(String input, String status, String sequence) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("input", input);
        values.put("status", status);
        values.put("sequence", sequence);
        return db.insert(TABLE, null, values);
    }

    public String getHistory() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, new String[]{"id", "input", "status", "sequence", "created_at"}, null, null, null, null, "id DESC");
        StringBuilder history = new StringBuilder();
        try {
            while (cursor.moveToNext()) {
                history.append("#").append(cursor.getInt(0))
                        .append(" | ").append(cursor.getString(2))
                        .append("\nInput: ").append(cursor.getString(1))
                        .append("\nSequence: ").append(cursor.getString(3))
                        .append("\nDate: ").append(cursor.getString(4))
                        .append("\n\n");
            }
        } finally {
            cursor.close();
        }
        return history.length() == 0 ? "No saved runs yet." : history.toString();
    }

    public void clearHistory() {
        getWritableDatabase().delete(TABLE, null, null);
    }
}
