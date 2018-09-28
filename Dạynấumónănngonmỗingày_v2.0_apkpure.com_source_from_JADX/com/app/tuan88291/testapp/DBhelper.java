package com.app.tuan88291.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.facebook.share.internal.ShareConstants;
import java.util.ArrayList;
import java.util.List;

public class DBhelper extends SQLiteOpenHelper {
    private static final String CREATE_IMG = "CREATE TABLE img(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, link TEXT, type TEXT DEFAULT 'img', idbv TEXT)";
    private static final String CREATE_M = "CREATE TABLE cook(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT, noidung TEXT, theloai TEXT, idbv TEXT)";
    private static final String CREATE_US = "CREATE TABLE user(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, idfb TEXT, name TEXT, admin INTEGER DEFAULT 0)";
    private static final String DB_NAME = "fb";
    private static int dbVersion;
    private final String TABLE_IMG;
    private final String TABLE_M;
    private final String TABLE_USER;
    private SQLiteDatabase db;
    private String p2;

    static {
        dbVersion = 1;
    }

    public DBhelper(Context context) {
        super(context, DB_NAME, null, dbVersion);
        this.TABLE_USER = "user";
        this.TABLE_M = "cook";
        this.TABLE_IMG = "img";
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_US);
        db.execSQL(CREATE_M);
        db.execSQL(CREATE_IMG);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS user");
            db.execSQL("DROP TABLE IF EXISTS cook");
            db.execSQL("DROP TABLE IF EXISTS img");
            onCreate(db);
        }
    }

    public void open() {
        this.db = getWritableDatabase();
    }

    public void close() {
        if (this.db != null && this.db.isOpen()) {
            this.db.close();
        }
    }

    public List<Data_save> getall() {
        SQLiteDatabase db = getWritableDatabase();
        List<Data_save> data = new ArrayList();
        Cursor cursor = getReadableDatabase().rawQuery("select * from cook ORDER BY id DESC", null);
        if (cursor == null || !cursor.moveToFirst()) {
            cursor.close();
            close();
            return data;
        }
        do {
            data.add(new Data_save(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        } while (cursor.moveToNext());
        cursor.close();
        close();
        return data;
    }

    public ArrayList<Data_detail> getimg(String idbv) {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Data_detail> data = new ArrayList();
        Cursor cursor = getReadableDatabase().rawQuery("select * from img where idbv = " + idbv, null);
        if (cursor == null || !cursor.moveToFirst()) {
            cursor.close();
            close();
            return data;
        }
        do {
            data.add(new Data_detail(cursor.getString(1), cursor.getString(3), cursor.getString(2)));
        } while (cursor.moveToNext());
        cursor.close();
        close();
        return data;
    }

    public String getlink(String idbv) {
        Cursor cursor1 = getReadableDatabase().rawQuery("select * from img where idbv = " + idbv, null);
        cursor1.moveToFirst();
        while (!cursor1.isAfterLast()) {
            this.p2 = cursor1.getString(1);
            cursor1.moveToNext();
        }
        cursor1.close();
        return this.p2;
    }

    public int checksave(String idbv) {
        Cursor cursor = getReadableDatabase().rawQuery("select * from cook where idbv = " + idbv, null);
        if (cursor != null) {
            return cursor.getCount();
        }
        cursor.close();
        return 0;
    }

    public int checkimg(String idbv) {
        Cursor cursor = getReadableDatabase().rawQuery("select * from img where idbv = " + idbv, null);
        if (cursor != null) {
            return cursor.getCount();
        }
        cursor.close();
        return 0;
    }

    public int countuser() {
        Cursor cursor = getReadableDatabase().query("user", null, null, null, null, null, null);
        if (cursor != null) {
            return cursor.getCount();
        }
        cursor.close();
        return 0;
    }

    public int updatemon(String maTV, String tenTV_New) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("mon", tenTV_New);
        return db.update("tbb", values, "id=?", new String[]{maTV});
    }

    public void insertuser(String table, String idfb, String name) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues value = new ContentValues();
        value.put("idfb", idfb);
        value.put(ShareConstants.WEB_DIALOG_PARAM_NAME, name);
        db.insert(table, null, value);
        close();
    }

    public void savecook(String table, String title, String noidung, String theloai, String idbv) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues value = new ContentValues();
        value.put(ShareConstants.WEB_DIALOG_PARAM_TITLE, title);
        value.put("noidung", noidung);
        value.put("theloai", theloai);
        value.put("idbv", idbv);
        db.insert(table, null, value);
        close();
    }

    public void saveimg(String table, String link, String type, String idbv) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues value = new ContentValues();
        value.put(ShareConstants.WEB_DIALOG_PARAM_LINK, link);
        value.put(ShareConstants.MEDIA_TYPE, type);
        value.put("idbv", idbv);
        db.insert(table, null, value);
        close();
    }

    public void delete(String table) {
        getReadableDatabase().delete(table, null, null);
    }

    public void deletecook(String idbv) {
        getReadableDatabase().delete("cook", "idbv=?", new String[]{idbv});
    }

    public void deleteimg(String idbv) {
        getReadableDatabase().delete("img", "idbv=?", new String[]{idbv});
    }

    public String ten() {
        Cursor cursor1 = getReadableDatabase().query("user", null, null, null, null, null, null);
        cursor1.moveToFirst();
        while (!cursor1.isAfterLast()) {
            this.p2 = cursor1.getString(2);
            cursor1.moveToNext();
        }
        cursor1.close();
        return this.p2;
    }

    public String idfb() {
        Cursor cursor1 = getReadableDatabase().query("user", null, null, null, null, null, null);
        cursor1.moveToFirst();
        while (!cursor1.isAfterLast()) {
            this.p2 = cursor1.getString(1);
            cursor1.moveToNext();
        }
        cursor1.close();
        return this.p2;
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
