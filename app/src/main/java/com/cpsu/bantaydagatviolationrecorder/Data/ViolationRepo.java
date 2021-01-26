package com.cpsu.bantaydagatviolationrecorder.Data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.ValueIterator;
import android.media.session.PlaybackState;

import com.cpsu.bantaydagatviolationrecorder.Utils.DBHelper;
import com.cpsu.bantaydagatviolationrecorder.Utils.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ViolationRepo {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public ViolationRepo() {
        Violation violation = new Violation();
    }

    public static String createTblViolations() {
        String query = "CREATE TABLE IF NOT EXISTS " + Violation.TABLE_VIOLATIONS + " (" +
                Violation.COL_ID + " TEXT," +
                Violation.COL_DATETIME + " TEXT, " +
                Violation.COL_NAME + " TEXT, " +
                Violation.COL_VSL_NO + " TEXT, " +
                Violation.COL_OWN_PRMT + " INTEGER, " +
                Violation.COL_VSL_PRMT + " INTEGER, " +
                Violation.COL_OTHER_COMP + " TEXT)";
        return query;
    }

    public static String createTblPhotos() {
        String query = "CREATE TABLE IF NOT EXISTS " + Violation.TABLE_PHOTOS + " (" +
                Violation.COL_ID + " TEXT, " +
                Violation.COL_PHOTO + " BLOB)";
        return query;
    }

    public void insert(Violation v) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Violation.COL_ID, v.getvId());
        values.put(Violation.COL_DATETIME, v.getDateTime());
        values.put(Violation.COL_NAME, v.getName());
        values.put(Violation.COL_VSL_NO, v.getVesselNo());
        values.put(Violation.COL_OWN_PRMT, v.getOwnersPermit());
        values.put(Violation.COL_VSL_PRMT, v.getVesselPermit());
        values.put(Violation.COL_OTHER_COMP, v.getOtherComplaints());

        db.insert(Violation.TABLE_VIOLATIONS, null, values);
        db.close();
    }

    public void insertPic(String id, byte[] pic) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Violation.COL_ID, id);
        values.put(Violation.COL_PHOTO, pic);
        db.insert(Violation.TABLE_PHOTOS, null, values);

        db.close();
    }

    public void updatePic(String id, byte[] pic) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Violation.COL_ID, id);
        values.put(Violation.COL_PHOTO, pic);
        db.update(Violation.TABLE_PHOTOS, values, Violation.COL_ID + "=?", new String[]{String.valueOf(id)});

        db.close();
    }

    public void update(Violation v) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Violation.COL_NAME, v.getName());
        values.put(Violation.COL_VSL_NO, v.getVesselNo());
        values.put(Violation.COL_OWN_PRMT, v.getOwnersPermit());
        values.put(Violation.COL_VSL_PRMT, v.getVesselPermit());
        values.put(Violation.COL_OTHER_COMP, v.getOtherComplaints());

        db.update(Violation.TABLE_VIOLATIONS, values,Violation.COL_ID + " =?", new String[]{String.valueOf(v.getvId())});
        db.close();
    }

    public byte[] getViolationPhoto(String id) {
        byte[] photo = null;
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + Violation.TABLE_PHOTOS + " WHERE " + Violation.COL_ID + " =?";

        Cursor cursor = db.rawQuery(query, new String[]{id});

        if (cursor.moveToFirst()) {
            photo = cursor.getBlob(cursor.getColumnIndex(Violation.COL_PHOTO));
        }
        return photo;
    }

    public Violation getViolationById(String id) {
        Violation violation = new Violation();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + Violation.TABLE_VIOLATIONS  + " WHERE " + Violation.COL_ID + " =?";

        Cursor cursor = db.rawQuery(query, new String[] {id});

        if (cursor.moveToFirst()) {
            violation.setvId(cursor.getString(cursor.getColumnIndex(Violation.COL_ID)));
            violation.setDateTime(cursor.getString(cursor.getColumnIndex(Violation.COL_DATETIME)));
            violation.setName(cursor.getString(cursor.getColumnIndex(Violation.COL_NAME)));
            violation.setVesselNo(cursor.getString(cursor.getColumnIndex(Violation.COL_VSL_NO)));
            violation.setOwnersPermit(cursor.getInt(cursor.getColumnIndex(Violation.COL_OWN_PRMT)));
            violation.setVesselPermit(cursor.getInt(cursor.getColumnIndex(Violation.COL_VSL_PRMT)));
            violation.setOtherComplaints(cursor.getString(cursor.getColumnIndex(Violation.COL_OTHER_COMP)));
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        db.close();

        return violation;
    }

    public ArrayList<HashMap<String, String>> getViolations() {
        ArrayList<HashMap<String, String>> violationsList = new ArrayList<>();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + Violation.TABLE_VIOLATIONS + " ORDER BY " +
                Violation.COL_DATETIME + " DESC ";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("ID", cursor.getString(cursor.getColumnIndex(Violation.COL_ID)));
                map.put("NameVessel", cursor.getString(cursor.getColumnIndex(Violation.COL_NAME)) + " (" +  cursor.getString(cursor.getColumnIndex(Violation.COL_VSL_NO)) + ")");
                map.put("DateTime", cursor.getString(cursor.getColumnIndex(Violation.COL_DATETIME)));
                violationsList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        db.close();
        return violationsList;
    }
}
