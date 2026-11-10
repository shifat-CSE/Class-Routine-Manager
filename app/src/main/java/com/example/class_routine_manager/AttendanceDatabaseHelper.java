package com.example.class_routine_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "AttendanceDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "AttendanceTable";

    public AttendanceDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, COURSE_NAME TEXT, TOTAL_CLASSES INTEGER DEFAULT 0, PRESENT_CLASSES INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addCourse(String courseName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("COURSE_NAME", courseName);
        values.put("TOTAL_CLASSES", 0);
        values.put("PRESENT_CLASSES", 0);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateAttendance(int id, int total, int present) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TOTAL_CLASSES", total);
        values.put("PRESENT_CLASSES", present);
        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<AttendanceCourse> getAllCourses() {
        List<AttendanceCourse> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new AttendanceCourse(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}