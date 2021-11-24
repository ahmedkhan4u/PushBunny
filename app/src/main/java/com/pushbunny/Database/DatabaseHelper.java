package com.pushbunny.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PushBunny.db";
    public static final String TABLE_NAME = "notification";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TITLE";
    public static final String COL_3 = "MESSAGE";
    public static final String COL_4 = "TIME";
    public static final String COL_5 = "IMAGE";
    public static final String COL_6 = "OTHER_DATA";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String myDB = "create table " + TABLE_NAME + " ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_2+" TEXT, "+COL_3+" TEXT, "+COL_4+" TEXT, "+COL_5+" TEXT, "+COL_6+" TEXT);";
        db.execSQL(myDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }



    public boolean insertData(String title, String msg, String timeStamp, String image, String otherData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, msg);
        contentValues.put(COL_4, timeStamp);
        contentValues.put(COL_5, image);
        contentValues.put(COL_6, otherData);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME, null);
        return res;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }
}
