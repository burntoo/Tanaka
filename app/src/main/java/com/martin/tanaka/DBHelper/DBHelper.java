package com.martin.tanaka.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper  {
    public static final String DBName = "tanaka.db";

    public DBHelper(Context context) {
        super(context, "tanaka.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE students(name TEXT, phonenumber TEXT PRIMARY KEY, profileImage TEXT, age TEXT, height TEXT, marital TEXT, location TEXT, coordinates TEXT, score TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
    }

    public Boolean insertData (String name, String phoneNumber, String img, String age,String height, String marital, String location, String coordinates, String password){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phonenumber", phoneNumber);
        values.put("profileImage", img);
        values.put("age", age);
        values.put("height", height);
        values.put("marital", marital);
        values.put("location", location);
        values.put("coordinates", coordinates);
        values.put("score", "0%");
        values.put("password", password);

        long results = db.insert("students", null, values);
        if(results == -1) return  false;
        else
            return true;
    }

    public boolean checkPhone(String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT phonenumber FROM students WHERE phonenumber = ?", new String[] {phone});
        if(cursor.getCount() > 0) return true;
        else
            return false;
    }

    public boolean login(String phone, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT phonenumber, password FROM students WHERE phonenumber = ? AND password = ?", new String[] {phone, password});
        if(cursor.getCount() > 0){ return true; }
        else{ return false; }
    }

    public Cursor getData (String phone)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT name, phonenumber, profileImage, age, height, marital, location, score FROM students WHERE phonenumber = ?", new String[] {phone});
        return cursor;
    }

}
