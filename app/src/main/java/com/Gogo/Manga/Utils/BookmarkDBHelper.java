package com.Gogo.Manga.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import  com.Gogo.Manga.model.Bookmark;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by ASUS on 02/05/2018.
 */
public class BookmarkDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mangako_2.db";
    private static final int DATABASE_VERSION = 3 ;
    public static final String TABLE_NAME = "data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_URL = "url";

    public static final String TABLE_READ= "nonton";
    public static final String FIELD_EPISODE= "episode";
    public static final String FIELD_LINK ="link";


    public static final String TABLE_HISTORY= "history";
    public static final String HISTORY_URL = "url";
    public static final String HISTORY_TITLE = "title";
    public static final String HISTORY_CHAPTER = "chapter";
    public static final String HISTORY_URL_CHAPTER = "url_chapter";
    public static final String HISTORY_IMAGE = "image";
    public static final String HISTORY_TIME = "time";

    public BookmarkDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_IMAGE + " BLOB NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL);"
        );

        db.execSQL(" CREATE TABLE " + TABLE_READ + " (" +
                FIELD_EPISODE + " TEXT NOT NULL, " +
                FIELD_LINK + " TEXT NOT NULL);"
        );

        db.execSQL(" CREATE TABLE " + TABLE_HISTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HISTORY_URL + " TEXT NOT NULL, " +
                HISTORY_TITLE + " TEXT NOT NULL, " +
                HISTORY_CHAPTER + " TEXT NOT NULL, " +
                HISTORY_URL_CHAPTER + " TEXT NOT NULL, " +
                HISTORY_IMAGE + " TEXT NOT NULL, " +
                HISTORY_TIME + " TEXT NOT NULL );"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void deleteHistory(){
        SQLiteDatabase db = this.getWritableDatabase();
        /*String query = "DELETE FROM " + TABLE_HISTORY + "  " ;
          db.rawQuery(query, null);*/
        db.delete(TABLE_HISTORY, null, null);

    }

    public Cursor getHistory(  ){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_HISTORY + "  " ;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getLastHistory(  ){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_HISTORY + " ORDER BY "+COLUMN_ID+" DESC LIMIT 1 " ;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void saveHistory(String url, String title, String chapter,
                            String url_chapter, String image, String time){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HISTORY_URL, url);
        values.put(HISTORY_TITLE, title);
        values.put(HISTORY_CHAPTER,chapter);
        values.put(HISTORY_URL_CHAPTER, url_chapter);
        values.put(HISTORY_IMAGE, image);
        values.put(HISTORY_TIME, time);

        // insert
        db.insert(TABLE_HISTORY,null, values);
        db.close();
    }

    /**create record**/
    public void saveNewArtikel(Context context, Bookmark artikel) {
        if (getReadBook(artikel.getUrl())>0){
            Toast.makeText(context, "This Manga Already Bookmark", Toast.LENGTH_SHORT).show();
        }else {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, artikel.getTitle());
            values.put(COLUMN_IMAGE, artikel.getImage());
            values.put(COLUMN_URL, artikel.getUrl());

            // insert
            db.insert(TABLE_NAME,null, values);
            db.close();

            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Good job!")
                    .setContentText("Manga Has Been Added to Bookmark")
                    .show();
        }
    }


    public int getReadBook(String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_NAME + " WHERE  "+COLUMN_URL+" = "+ "'" +link +"'" ;
        //   String query = "SELECT  * FROM " + TABLE_READ  ;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }




    /**delete record**/
    public void deleteArtikelRecord(long id, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE id='"+id+"'");
        Toast.makeText(context, "Delete Succesfully", Toast.LENGTH_SHORT).show();

    }

    public int getReadCount(String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_READ + " WHERE  "+FIELD_LINK+" = "+ "'" +link +"'" ;
        //   String query = "SELECT  * FROM " + TABLE_READ  ;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void saveRead(String episode, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FIELD_EPISODE, episode);
        cv.put(FIELD_LINK, link);
        long result = db.insert(TABLE_READ, null, cv);

        if (result == -1) {
            Log.i("dbhelpher", "Failed to Save");
            //Toast.makeText(context, "Failed to Save", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("dbhelpher", "Successfully to Save");
            //      Toast.makeText(context, "Successfully to Save", Toast.LENGTH_SHORT).show();
        }

    }

}
