package com.dash.dashapp.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dash.dashapp.Model.News;
import com.dash.dashapp.Model.Proposal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sebas on 8/5/2017.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 16;
    private static final String DATABASE_NAME = "dashDB.db";
    private static final String TAG = "MyDBHandler";


    public static final String TABLE_NEWS = "news";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RSS_GUID = "rss_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_THUMBNAIL = "image";
    public static final String COLUMN_DATE = "date_pub";
    public static final String COLUMN_CONTENT = "content";


    public static final String TABLE_PROPOSALS = "proposal";
    public static final String COLUMN_HASH = "hash"; //proposal hash [string]
    public static final String COLUMN_NAME = "name"; //proposal name [string]
    public static final String COLUMN_URL = "url"; //proposal URL set by the proposal owner during proposal submission [string]
    public static final String COLUMN_DW_URL = "dw_url"; //URL pointing to the DashCentral proposal page [string]
    public static final String COLUMN_DW_URL_COMMENTS = "dw_url_comments"; //URL pointing to the comment section on the DashCentral proposal page [string]
    public static final String COLUMN_TITLE_PROP = "title_prop"; //proposal title entered by the proposal owner on DashCentral [string]
    public static final String COLUMN_DATE_ADDED = "date_added"; //date, when the proposal was first seen on the network and has been added to the DashCentral database [datetime, UTC]
    public static final String COLUMN_DATE_ADDED_HUMAN = "date_added_human"; //time since proposal has been added to the DashCentral database in words, eg. "6 days ago" [string]
    public static final String COLUMN_DATE_END = "date_end"; //date, when proposal payouts are expected to end [datetime, UTC]
    public static final String COLUMN_VOTING_DEADLINE_HUMAN = "voting_deadline_human"; //time until voting will be closed for this proposal, e.g "in 15 days" or "passed" [string]
    public static final String COLUMN_WILL_BE_FUNDED = "will_be_funded"; //is true when proposal has enough yes votes to become funded [boolean]
    public static final String COLUMN_REMAINING_YES_VOTES_UNTIL_FUNDING = "remaining_yes_votes_until_funding"; //amount of yes votes required for funding of this proposal [integer]
    public static final String COLUMN_IN_NEXT_BUDGET = "in_next_budget"; //indicates, if proposal will be included within next budget and will be paid [boolean]
    public static final String COLUMN_MONTHLY_AMOUNT = "monthly_amount"; //amount of DASH that will be paid per month [integer]
    public static final String COLUMN_TOTAL_PAYMENT_COUNT = "total_payment_amount"; //amount of payment cycles this proposal was intended to run [integer]
    public static final String COLUMN_REMAINING_PAYMENT_COUNT = "remaining_payment_count"; //remaining payment cycles [integer]
    public static final String COLUMN_YES = "yes"; //yes votes on this proposal [integer]
    public static final String COLUMN_NO = "no"; //no votes on this proposal [integer]
    public static final String COLUMN_ORDER = "order"; //the order value should be used to sort proposals in case the JSON order is not preserved correctly. The order is defined by a reddit like algo covering the time and the upvotes and downvotes on DashCentral [integer]
    public static final String COLUMN_COMMENT_AMOUNT = "comment_amount"; //amount of proposal comments posted on DashCentral [integer]
    public static final String COLUMN_OWNER_USERNAME = "owner_username"; //username of the proposal owner on DashCentral [string]


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

        String CREATE_PROPOSAL_TABLE =
                "CREATE TABLE " + TABLE_PROPOSALS + "(" +
                        COLUMN_HASH + " TEXT PRIMARY KEY," +
                        COLUMN_NAME + " TEXT," +
                        COLUMN_URL + " TEXT," +
                        COLUMN_DW_URL + " TEXT," +
                        COLUMN_DW_URL_COMMENTS + " DATE," +
                        COLUMN_TITLE_PROP + " TEXT" +
                        COLUMN_DATE_ADDED + " DATE," +
                        COLUMN_DATE_ADDED_HUMAN + " TEXT," +
                        COLUMN_DATE_END + " DATE," +
                        COLUMN_VOTING_DEADLINE_HUMAN + " TEXT," +
                        COLUMN_WILL_BE_FUNDED + " BOOLEAN," +
                        COLUMN_REMAINING_YES_VOTES_UNTIL_FUNDING + " INTEGER," +
                        COLUMN_IN_NEXT_BUDGET + " BOOLEAN," +
                        COLUMN_MONTHLY_AMOUNT + " INTEGER," +
                        COLUMN_TOTAL_PAYMENT_COUNT + " INTEGER," +
                        COLUMN_REMAINING_PAYMENT_COUNT + " INTEGER," +
                        COLUMN_YES + " INTEGER," +
                        COLUMN_NO + " INTEGER," +
                        COLUMN_ORDER + " INTEGER," +
                        COLUMN_COMMENT_AMOUNT + " INTEGER," +
                        COLUMN_OWNER_USERNAME + " TEXT" +
                        ")";
        db.execSQL(CREATE_PROPOSAL_TABLE);


    }


    // NEWS
    public void addNews(News news) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RSS_GUID, news.getGuid());
        values.put(COLUMN_TITLE, news.getTitle());
        values.put(COLUMN_THUMBNAIL, news.getThumbnail());

        SimpleDateFormat rssFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss");
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
        if (filter != null) {
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


    // PROPOSALS
    public void addProposal(Proposal proposal) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HASH, proposal.getHash());
        values.put(COLUMN_NAME, proposal.getName());
        values.put(COLUMN_URL, proposal.getUrl());
        values.put(COLUMN_DW_URL, proposal.getDw_url());
        values.put(COLUMN_DW_URL_COMMENTS, proposal.getDw_url_comments());
        values.put(COLUMN_TITLE_PROP, proposal.getTitle());
        values.put(COLUMN_DATE_ADDED, proposal.getDate_added());
        values.put(COLUMN_DATE_ADDED_HUMAN, proposal.getDate_added_human());
        values.put(COLUMN_DATE_END, proposal.getDate_end());
        values.put(COLUMN_VOTING_DEADLINE_HUMAN, proposal.getVoting_deadline_human());
        values.put(COLUMN_WILL_BE_FUNDED, proposal.isWill_be_funded());
        values.put(COLUMN_REMAINING_YES_VOTES_UNTIL_FUNDING, proposal.getRemaining_yes_votes_until_funding());
        values.put(COLUMN_IN_NEXT_BUDGET, proposal.isIn_next_budget());
        values.put(COLUMN_MONTHLY_AMOUNT, proposal.getMonthly_amount());
        values.put(COLUMN_TOTAL_PAYMENT_COUNT, proposal.getTotal_payment_count());
        values.put(COLUMN_REMAINING_PAYMENT_COUNT, proposal.getRemaining_payment_count());
        values.put(COLUMN_YES, proposal.getYes());
        values.put(COLUMN_NO, proposal.getNo());
        values.put(COLUMN_ORDER, proposal.getOrder());
        values.put(COLUMN_COMMENT_AMOUNT, proposal.getComment_amount());
        values.put(COLUMN_OWNER_USERNAME, proposal.getOwner_username());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PROPOSALS, null, values);
        db.close();
    }

    public Proposal findProposal(String proposalHash) {
        String query = "SELECT * FROM " + TABLE_PROPOSALS + " WHERE " + COLUMN_HASH + "='" + proposalHash + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Proposal proposal = new Proposal();
        if (cursor.moveToFirst()) {
            proposal.setHash(cursor.getString(0));
            proposal.setName(cursor.getString(1));
            proposal.setUrl(cursor.getString(2));
            proposal.setDw_url(cursor.getString(3));
            proposal.setDw_url_comments(cursor.getString(4));
            proposal.setTitle(cursor.getString(5));
            proposal.setDate_added(cursor.getString(6));
            proposal.setDate_added_human(cursor.getString(7));
            proposal.setDate_end(cursor.getString(8));
            proposal.setVoting_deadline_human(cursor.getString(9));
            proposal.setWill_be_funded(cursor.getInt(10) != 0);
            proposal.setRemaining_yes_votes_until_funding(cursor.getInt(11));
            proposal.setIn_next_budget(cursor.getInt(12) != 0);
            proposal.setMonthly_amount(cursor.getInt(13));
            proposal.setTotal_payment_count(cursor.getInt(14));
            proposal.setRemaining_payment_count(cursor.getInt(15));
            proposal.setYes(cursor.getInt(16));
            proposal.setNo(cursor.getInt(17));
            proposal.setOrder(cursor.getInt(18));
            proposal.setComment_amount(cursor.getInt(19));
            proposal.setOwner_username(cursor.getString(20));
            cursor.close();
        } else {
            proposal = null;
        }
        db.close();
        return proposal;
    }

    public ArrayList<Proposal> findAllProposals(String filter) {
        Log.d(TAG, "Find list proposals");

        ArrayList<Proposal> proposalsList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_PROPOSALS;
        if (filter != null) {
            query += " WHERE " + COLUMN_TITLE + " LIKE '%" + filter + "%'";
        }
        query += " ORDER BY " + COLUMN_DATE_END + " ASC;";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Proposal proposal = new Proposal();
            proposal.setHash(cursor.getString(0));
            proposal.setName(cursor.getString(1));
            proposal.setUrl(cursor.getString(2));
            proposal.setDw_url(cursor.getString(3));
            proposal.setDw_url_comments(cursor.getString(4));
            proposal.setTitle(cursor.getString(5));
            proposal.setDate_added(cursor.getString(6));
            proposal.setDate_added_human(cursor.getString(7));
            proposal.setDate_end(cursor.getString(8));
            proposal.setVoting_deadline_human(cursor.getString(9));
            proposal.setWill_be_funded(cursor.getInt(10) != 0);
            proposal.setRemaining_yes_votes_until_funding(cursor.getInt(11));
            proposal.setIn_next_budget(cursor.getInt(12) != 0);
            proposal.setMonthly_amount(cursor.getInt(13));
            proposal.setTotal_payment_count(cursor.getInt(14));
            proposal.setRemaining_payment_count(cursor.getInt(15));
            proposal.setYes(cursor.getInt(16));
            proposal.setNo(cursor.getInt(17));
            proposal.setOrder(cursor.getInt(18));
            proposal.setComment_amount(cursor.getInt(19));
            proposal.setOwner_username(cursor.getString(20));
            proposalsList.add(proposal);
        }
        cursor.close();
        db.close();
        return proposalsList;
    }


    public boolean deleteProposal(String proposalHash) {
        boolean result = false;
        String query = "SELECT * FROM " + TABLE_PROPOSALS + " WHERE " + COLUMN_HASH + " =  '" + proposalHash + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Proposal proposal = new Proposal();
        if (cursor.moveToFirst()) {
            proposal.setHash(cursor.getString(0));
            db.delete(TABLE_PROPOSALS, COLUMN_HASH + " = ?",
                    new String[]{String.valueOf(proposal.getHash())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public void deleteAllProposals() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_PROPOSALS);
        db.close();
    }

}


