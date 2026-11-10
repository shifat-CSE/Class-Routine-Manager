package com.example.class_routine_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "RoutineDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "RoutineTable";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, SUBJECT TEXT, CODE TEXT, TEACHER TEXT, ROOM TEXT, DAY TEXT, START_TIME TEXT, END_TIME TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addClass(ClassRoutine routine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SUBJECT", routine.getSubject());
        values.put("CODE", routine.getCode());
        values.put("TEACHER", routine.getTeacher());
        values.put("ROOM", routine.getRoom());
        values.put("DAY", routine.getDay());
        values.put("START_TIME", routine.getStartTime());
        values.put("END_TIME", routine.getEndTime());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<ClassRoutine> getAllClasses() {
        List<ClassRoutine> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                ClassRoutine routine = new ClassRoutine(
                        cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7)
                );
                classList.add(routine);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return classList;
    }
}