package com.dash.dashapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dash.dashapp.models.News;
import com.dash.dashapp.models.PriceChartData;
import com.dash.dashapp.models.Proposal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sebas on 8/5/2017.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 23;
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
    public static final String COLUMN_ORDER = "order_display"; //the order value should be used to sort proposals in case the JSON order is not preserved correctly. The order is defined by a reddit like algo covering the time and the upvotes and downvotes on DashCentral [integer]
    public static final String COLUMN_COMMENT_AMOUNT = "comment_amount"; //amount of proposal comments posted on DashCentral [integer]
    public static final String COLUMN_OWNER_USERNAME = "owner_username"; //username of the proposal owner on DashCentral [string]


    public static final String TABLE_PRICE_CHART = "price_chart";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_START_GAP = "start_gap";
    public static final String COLUMN_END_GAP = "end_gap";
    public static final String COLUMN_GAP = "gap";
    public static final String COLUMN_END_GAP_INDEX = "end_gap_index";
    public static final String COLUMN_CLOSE = "close";
    public static final String COLUMN_HIGH = "high";
    public static final String COLUMN_LOW = "low";
    public static final String COLUMN_OPEN = "open";
    public static final String COLUMN_PAIR_VOLUME = "pairVolume";
    public static final String COLUMN_TRADES = "trades";
    public static final String COLUMN_VOLUME = "volume";


    public MyDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPOSALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICE_CHART);
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
                        COLUMN_TITLE_PROP + " TEXT," +
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

        String CREATE_PRICE_CHART_TABLE =
                "CREATE TABLE " + TABLE_PRICE_CHART + "(" +
                        COLUMN_TIME + " REAL," +
                        COLUMN_START_GAP + " REAL," +
                        COLUMN_END_GAP + " REAL," +
                        COLUMN_GAP + " REAL," +
                        COLUMN_CLOSE + " REAL," +
                        COLUMN_HIGH + " REAL," +
                        COLUMN_LOW + " REAL," +
                        COLUMN_OPEN + " REAL," +
                        COLUMN_PAIR_VOLUME + " REAL," +
                        COLUMN_TRADES + " REAL," +
                        COLUMN_VOLUME + " REAL" +
                        ")";
        db.execSQL(CREATE_PRICE_CHART_TABLE);

        String CREATE_INDEX_END_GAP =
                "CREATE INDEX " + COLUMN_END_GAP_INDEX + " ON " + TABLE_PRICE_CHART + "(" + COLUMN_END_GAP + " )";
        db.execSQL(CREATE_INDEX_END_GAP);
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

    // NEWS
    public void addPriceChart(PriceChartData priceChartData) {
        //Log.d(TAG, "Add price chart");

        ContentValues values = new ContentValues();
        Log.d("DateDebug", "Inserting in database : " + DateUtil.getDate(priceChartData.getTime()));
        values.put(COLUMN_TIME, priceChartData.getTime());
        values.put(COLUMN_START_GAP, priceChartData.getStartGap());
        values.put(COLUMN_END_GAP, priceChartData.getEndGap());
        values.put(COLUMN_GAP, priceChartData.getGap());
        values.put(COLUMN_CLOSE, priceChartData.getClose());
        values.put(COLUMN_HIGH, priceChartData.getHigh());
        values.put(COLUMN_LOW, priceChartData.getLow());
        values.put(COLUMN_OPEN, priceChartData.getOpen());
        values.put(COLUMN_PAIR_VOLUME, priceChartData.getPairVolume());
        values.put(COLUMN_TRADES, priceChartData.getTrades());
        values.put(COLUMN_VOLUME, priceChartData.getVolume());

        /*Log.d(TAG, "Current priceChartData : " +
                " Time : " + priceChartData.getTime() + " " +
                " Start gap : " + priceChartData.getStartGap() + " " +
                " End gap : " + priceChartData.getEndGap() + " " +
                " Gap : " + priceChartData.getGap() + " " +
                " Volume : " + priceChartData.getVolume() + " " +
                " Close : " + priceChartData.getClose() + " " +
                " Open : " + priceChartData.getOpen() + " " +
                " Low : " + priceChartData.getLow() + " " +
                " High : " + priceChartData.getHigh() + " " +
                " Pair volume : " + priceChartData.getPairVolume() + " " +
                " Trades : " + priceChartData.getTrades());*/

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PRICE_CHART, null, values);
        db.close();
    }


    public List<PriceChartData> findPriceChart(long dateStart, long dateEnd, long gap) {

        // TODO AGGREGATE GAP WITH SQL
        Log.d(TAG, "Find list Price chart");
        Log.d(TAG, "Start date : " + dateStart);
        Log.d(TAG, "End date : " + dateEnd);

        List<PriceChartData> priceChartList = new ArrayList<>();

        String query = "SELECT *" +
                " FROM " + TABLE_PRICE_CHART +
                " WHERE " + COLUMN_TIME + " > " + dateStart +
                " AND " + COLUMN_TIME + " < " + dateEnd +
                " AND " + COLUMN_GAP + " = " + gap +
                " ORDER BY " + COLUMN_END_GAP + " DESC;";


        /*String query = "SELECT " +
                "gap," +
                "MAX(" + COLUMN_CLOSE + ")," +
                "MAX(" + COLUMN_HIGH + ")," +
                "MIN(" + COLUMN_LOW + ")," +
                "MAX(" + COLUMN_OPEN + ")," +
                "SUM(" + COLUMN_PAIR_VOLUME + ")," +
                "SUM(" + COLUMN_TRADES + ")," +
                "SUM(" + COLUMN_VOLUME + ") " +
                "FROM " +
                "(" +
                "SELECT CASE WHEN (ROW_NUMBER () OVER (PARTITION BY(" + COLUMN_TIME + " - (" + COLUMN_TIME + "%" + gap + ")) ORDER BY time DESC)) = 1 THEN " + COLUMN_CLOSE + " ELSE 0 END AS " + COLUMN_CLOSE + "," +
                COLUMN_HIGH + "," +
                COLUMN_LOW + "," +
                "CASE WHEN (ROW_NUMBER () OVER (PARTITION BY(" + COLUMN_TIME + " - (" + COLUMN_TIME + "%" + gap + ")) ORDER BY time ASC)) = 1 THEN " + COLUMN_OPEN + " ELSE 0 END AS " + COLUMN_OPEN + "," +
                COLUMN_PAIR_VOLUME + "," +
                COLUMN_TRADES + "," +
                COLUMN_VOLUME + "," +
                COLUMN_TIME + " - (" + COLUMN_TIME + "%" + gap + ") AS gap," +
                COLUMN_TIME +
                " FROM " + TABLE_PRICE_CHART +
                " WHERE " + COLUMN_TIME + " > " + dateStart +
                " AND " + COLUMN_TIME + " <= " + dateEnd + " ORDER BY " + COLUMN_TIME + " DESC) t" +
                " GROUP BY t.gap";*/


        Log.d(TAG, "Query : " + query);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            PriceChartData priceChartData = new PriceChartData();
            priceChartData.setTime(cursor.getLong(0));
            priceChartData.setStartGap(cursor.getLong(1));
            priceChartData.setEndGap(cursor.getLong(2));
            priceChartData.setGap(cursor.getLong(3));
            priceChartData.setClose(cursor.getLong(4));
            priceChartData.setHigh(cursor.getLong(5));
            priceChartData.setLow(cursor.getLong(6));
            priceChartData.setOpen(cursor.getLong(7));
            priceChartData.setPairVolume(cursor.getLong(8));
            priceChartData.setTrades(cursor.getLong(9));
            priceChartData.setVolume(cursor.getLong(10));

            /*Log.d(TAG, "Current priceChartData : " +
                    " Time : " + priceChartData.getTime() + " " +
                    " Start gap : " + priceChartData.getStartGap() + " " +
                    " End gap : " + priceChartData.getEndGap() + " " +
                    " Gap : " + priceChartData.getGap() + " " +
                    " Volume : " + priceChartData.getVolume() + " " +
                    " Close : " + priceChartData.getClose() + " " +
                    " Open : " + priceChartData.getOpen() + " " +
                    " Low : " + priceChartData.getLow() + " " +
                    " High : " + priceChartData.getHigh() + " " +
                    " Pair volume : " + priceChartData.getPairVolume() + " " +
                    " Trades : " + priceChartData.getTrades());*/

            priceChartList.add(priceChartData);
        }
        cursor.close();

        db.close();
        return priceChartList;
    }

    public void deleteAllPriceChart(long dateStart, long dateEnd) {

        Log.d(TAG, "Delete all price chart");

        SQLiteDatabase db = this.getWritableDatabase();
        if (dateStart == 0 && dateEnd == 0) {
            db.execSQL("delete from " + TABLE_PRICE_CHART);
        }
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

    public List<News> findAllNews(String filter) {
        Log.d(TAG, "Find list news");

        List<News> newsList = new ArrayList<>();

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


    public void deletePriceChart() {
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

    public List<Proposal> findAllProposals(String filter) {
        Log.d(TAG, "Find list proposals");

        List<Proposal> proposalsList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_PROPOSALS;
        if (filter != null) {
            query += " WHERE " + COLUMN_TITLE_PROP + " LIKE '%" + filter + "%'";
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


