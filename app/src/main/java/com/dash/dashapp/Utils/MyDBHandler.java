package com.dash.dashapp.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dash.dashapp.Model.News;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sebas on 8/5/2017.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 15;
    private static final String DATABASE_NAME = "dashDB.db";
    public static final String TABLE_NEWS = "news";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RSS_GUID = "rss_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_THUMBNAIL = "image";
    public static final String COLUMN_DATE = "date_pub";
    public static final String COLUMN_CONTENT = "content";
    private static final String TAG = "MyDBHandler";

    public MyDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEWS_TABLE =
                "CREATE TABLE " + TABLE_NEWS + "(" +
                        COLUMN_ID + " INTEGER PRIMARY KEY," +
                        COLUMN_RSS_GUID + " TEXT," +
                        COLUMN_TITLE + " TEXT," +
                        COLUMN_THUMBNAIL + " TEXT," +
                        COLUMN_DATE + " DATE," +
                        COLUMN_CONTENT + " TEXT" +
                        ")";
        db.execSQL(CREATE_NEWS_TABLE);

    }

    public void addNews(News news) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RSS_GUID, news.getGuid());
        values.put(COLUMN_TITLE, news.getTitle());
        values.put(COLUMN_THUMBNAIL, news.getThumbnail());

        SimpleDateFormat rssFormat = new SimpleDateFormat("EEE, ww MMM yyyy hh:mm:ss");
        Date dateRSS = new Date();
        try {
            dateRSS = rssFormat.parse(news.getPubDate());
        } catch (ParseException ex) {
            ex.getMessage();
        }

        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateSQL = sqlFormat.format(dateRSS);

        values.put(COLUMN_DATE, dateSQL);
        values.put(COLUMN_CONTENT, news.getContent());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NEWS, null, values);
        db.close();
    }

    public News findNews(String newsId) {
        String query = "SELECT * FROM " + TABLE_NEWS + " WHERE " + COLUMN_RSS_GUID + "='" + newsId + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        News news = new News();
        if (cursor.moveToFirst()) {
            news.setGuid(cursor.getString(1));
            news.setTitle(cursor.getString(2));
            news.setThumbnail(cursor.getString(3));
            news.setPubDate(cursor.getString(4));
            news.setContent(cursor.getString(5));
            cursor.close();
        } else {
            news = null;
        }
        db.close();
        return news;
    }

    public ArrayList<News> findAllNews(String filter) {
        Log.d(TAG, "Find list news");

        ArrayList<News> newsList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NEWS;
        if (filter != null){
            query += " WHERE " + COLUMN_TITLE + " LIKE '%" + filter + "%'";
        }
        query += " ORDER BY " + COLUMN_DATE + " DESC;";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            News news = new News();
            news.setGuid(cursor.getString(1));
            news.setTitle(cursor.getString(2));
            news.setThumbnail(cursor.getString(3));
            news.setPubDate(cursor.getString(4));
            news.setContent(cursor.getString(5));
            newsList.add(news);
        }
        cursor.close();

        db.close();
        return newsList;
    }


    public boolean deleteNews(String newsId) {
        boolean result = false;
        String query = "SELECT * FROM " + TABLE_NEWS + " WHERE " + COLUMN_RSS_GUID + " =  '" + newsId + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        News news = new News();
        if (cursor.moveToFirst()) {
            news.setGuid(cursor.getString(1));
            db.delete(TABLE_NEWS, COLUMN_RSS_GUID + " = ?",
                    new String[]{String.valueOf(news.getGuid())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public void deleteAllNews() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NEWS);
        db.close();
    }

}


