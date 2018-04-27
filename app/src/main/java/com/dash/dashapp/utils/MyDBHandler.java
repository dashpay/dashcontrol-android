package com.dash.dashapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.models.Comment;
import com.dash.dashapp.models.Exchange;
import com.dash.dashapp.models.Market;
import com.dash.dashapp.models.PriceChartData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebas on 8/5/2017.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 29;
    private static final String DATABASE_NAME = "dashDB.db";
    private static final String TAG = "MyDBHandler";


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


    public static final String TABLE_COMMENTS = "comments"; // Comment table
    public static final String COLUMN_HASH_PROPOSAL = "hash_proposal"; // Foreign key dash
    public static final String COLUMN_ID_COMMENTS = "id";  // unique comment identifier [string]
    public static final String COLUMN_USERNAME = "username"; // DashCentral username of the comment poster [string]
    public static final String COLUMN_DATE_COMMENTS = "date"; // comment date [datetime, UTC]
    public static final String COLUMN_DATE_HUMAN = "date_human"; // time since comment has been posted in words e.g. "3 days ago" [string]
    public static final String COLUMN_ORDER_COMMENTS = "order_comments"; // sort comments using this order value [integer]
    public static final String COLUMN_LEVEL = "level"; // use the level value to add a css padding (e.g. $level*13px) to the comments in order to create the impression of a tree [integer]
    public static final String COLUMN_RECENTLY_POSTED = "recently_posted"; // use this value to highlight comments that have been posted recently [boolean]
    public static final String COLUMN_POSTED_BY_OWNER = "posted_by_owner"; // highlight comments posted by the owner of the proposal [boolean]
    public static final String COLUMN_REPLY_URL = "reply_url"; // add a reply link to each comment and use this URL [string]
    public static final String COLUMN_CONTENT_COMMENTS = "content"; //  comment content [string]


    public static final String TABLE_PRICE_CHART = "price_chart";
    public static final String COLUMN_EXCHANGE = "exchange";
    public static final String COLUMN_MARKET = "market";
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


    public static final String TABLE_MARKET_PRICE = "market_price";
    public static final String COLUMN_EXCHANGE_PRICE = "exchange";
    public static final String COLUMN_MARKET_PRICE = "market";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DEFAULT = "default_market";


    public MyDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPOSALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKET_PRICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICE_CHART);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

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


        String CREATE_COMMENTS =
                "CREATE TABLE " + TABLE_COMMENTS + "(" +
                        COLUMN_HASH_PROPOSAL + " TEXT," +
                        COLUMN_ID_COMMENTS + " TEXT," +
                        COLUMN_USERNAME + " TEXT," +
                        COLUMN_DATE_COMMENTS + " TEXT," +
                        COLUMN_DATE_HUMAN + " TEXT," +
                        COLUMN_ORDER_COMMENTS + " INTEGER," +
                        COLUMN_LEVEL + " INTEGER," +
                        COLUMN_RECENTLY_POSTED + " INTEGER," +
                        COLUMN_POSTED_BY_OWNER + " INTEGER," +
                        COLUMN_REPLY_URL + " TEXT," +
                        COLUMN_CONTENT_COMMENTS + " TEXT" +
                        ")";
        db.execSQL(CREATE_COMMENTS);


        String CREATE_MARKET_PRICE =
                "CREATE TABLE " + TABLE_MARKET_PRICE + "(" +
                        COLUMN_EXCHANGE_PRICE + " TEXT," +
                        COLUMN_MARKET_PRICE + " TEXT," +
                        COLUMN_PRICE + " REAL," +
                        COLUMN_DEFAULT + " INTEGER" +
                        ")";
        db.execSQL(CREATE_MARKET_PRICE);


        String CREATE_PRICE_CHART_TABLE =
                "CREATE TABLE " + TABLE_PRICE_CHART + "(" +
                        COLUMN_EXCHANGE + " TEXT," +
                        COLUMN_MARKET + " TEXT," +
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

    // PROPOSALS
    public void addProposal(BudgetProposal budgetProposal) {
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_HASH, budgetProposal.getHash());
//        values.put(COLUMN_NAME, budgetProposal.getName());
//        values.put(COLUMN_URL, budgetProposal.getUrl());
//        values.put(COLUMN_DW_URL, budgetProposal.getDw_url());
//        values.put(COLUMN_DW_URL_COMMENTS, budgetProposal.getDw_url_comments());
//        values.put(COLUMN_TITLE_PROP, budgetProposal.getTitle());
//        values.put(COLUMN_DATE_ADDED, budgetProposal.getDate_added());
//        values.put(COLUMN_DATE_ADDED_HUMAN, budgetProposal.getDate_added_human());
//        values.put(COLUMN_DATE_END, budgetProposal.getDate_end());
//        values.put(COLUMN_VOTING_DEADLINE_HUMAN, budgetProposal.getVoting_deadline_human());
//        values.put(COLUMN_WILL_BE_FUNDED, budgetProposal.isWill_be_funded());
//        values.put(COLUMN_REMAINING_YES_VOTES_UNTIL_FUNDING, budgetProposal.getRemaining_yes_votes_until_funding());
//        values.put(COLUMN_IN_NEXT_BUDGET, budgetProposal.isIn_next_budget());
//        values.put(COLUMN_MONTHLY_AMOUNT, budgetProposal.getMonthly_amount());
//        values.put(COLUMN_TOTAL_PAYMENT_COUNT, budgetProposal.getTotal_payment_count());
//        values.put(COLUMN_REMAINING_PAYMENT_COUNT, budgetProposal.getRemaining_payment_count());
//        values.put(COLUMN_YES, budgetProposal.getYes());
//        values.put(COLUMN_NO, budgetProposal.getNo());
//        values.put(COLUMN_ORDER, budgetProposal.getOrder());
//        values.put(COLUMN_COMMENT_AMOUNT, budgetProposal.getComment_amount());
//        values.put(COLUMN_OWNER_USERNAME, budgetProposal.getOwner_username());
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.insert(TABLE_PROPOSALS, null, values);
//        db.close();
    }

    public List<BudgetProposal> findAllProposals(String filter) {
        Log.d(TAG, "Find list proposals");

        List<BudgetProposal> proposalsList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_PROPOSALS;
        if (filter != null) {
            query += " WHERE " + COLUMN_TITLE_PROP + " LIKE '%" + filter + "%'";
        }
        query += " ORDER BY " + COLUMN_DATE_END + " ASC;";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            BudgetProposal budgetProposal = new BudgetProposal();
//            budgetProposal.setHash(cursor.getString(cursor.getColumnIndex(COLUMN_HASH)));
//            budgetProposal.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
//            budgetProposal.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
//            budgetProposal.setDw_url(cursor.getString(cursor.getColumnIndex(COLUMN_DW_URL)));
//            budgetProposal.setDw_url_comments(cursor.getString(cursor.getColumnIndex(COLUMN_DW_URL_COMMENTS)));
//            budgetProposal.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_PROP)));
//            budgetProposal.setDate_added(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_ADDED)));
//            budgetProposal.setDate_added_human(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_ADDED_HUMAN)));
//            budgetProposal.setDate_end(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_END)));
//            budgetProposal.setVoting_deadline_human(cursor.getString(cursor.getColumnIndex(COLUMN_VOTING_DEADLINE_HUMAN)));
//            budgetProposal.setWill_be_funded(cursor.getInt(cursor.getColumnIndex(COLUMN_WILL_BE_FUNDED)) != 0);
//            budgetProposal.setRemaining_yes_votes_until_funding(cursor.getInt(cursor.getColumnIndex(COLUMN_REMAINING_YES_VOTES_UNTIL_FUNDING)));
//            budgetProposal.setIn_next_budget(cursor.getInt(cursor.getColumnIndex(COLUMN_IN_NEXT_BUDGET)) != 0);
//            budgetProposal.setMonthly_amount(cursor.getInt(cursor.getColumnIndex(COLUMN_MONTHLY_AMOUNT)));
//            budgetProposal.setTotal_payment_count(cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_PAYMENT_COUNT)));
//            budgetProposal.setRemaining_payment_count(cursor.getInt(cursor.getColumnIndex(COLUMN_REMAINING_PAYMENT_COUNT)));
//            budgetProposal.setYes(cursor.getInt(cursor.getColumnIndex(COLUMN_YES)));
//            budgetProposal.setNo(cursor.getInt(cursor.getColumnIndex(COLUMN_NO)));
//            budgetProposal.setOrder(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER)));
//            budgetProposal.setComment_amount(cursor.getInt(cursor.getColumnIndex(COLUMN_COMMENT_AMOUNT)));
//            budgetProposal.setOwner_username(cursor.getString(cursor.getColumnIndex(COLUMN_OWNER_USERNAME)));
            proposalsList.add(budgetProposal);
        }
        cursor.close();
        db.close();
        return proposalsList;
    }


    public boolean deleteProposal(String proposalHash) {
        boolean result = false;
//        String query = "SELECT *" +
//                " FROM " + TABLE_PROPOSALS +
//                " WHERE " + COLUMN_HASH + " =  '" + proposalHash + "';";
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//        BudgetProposal proposal = new BudgetProposal();
//        if (cursor.moveToFirst()) {
//            proposal.setHash(cursor.getString(0));
//            db.delete(TABLE_PROPOSALS, COLUMN_HASH + " = ?",
//                    new String[]{String.valueOf(proposal.getHash())});
//            cursor.close();
//            result = true;
//        }
//        db.close();
        return result;
    }

    public void deleteAllProposals() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_PROPOSALS);
        db.close();
    }


    // PROPOSALS
    public void addComments(Comment comment) {
        ContentValues values = new ContentValues();

        Log.d(TAG, "Adding comment : " + comment.getContent());
        values.put(COLUMN_HASH_PROPOSAL, comment.getHashProposal());
        values.put(COLUMN_ID_COMMENTS, comment.getId());
        values.put(COLUMN_USERNAME, comment.getUsername());
        values.put(COLUMN_DATE_COMMENTS, comment.getDate());
        values.put(COLUMN_DATE_HUMAN, comment.getDate_human());
        values.put(COLUMN_ORDER_COMMENTS, comment.getOrder());
        values.put(COLUMN_LEVEL, comment.getLevel());
        values.put(COLUMN_RECENTLY_POSTED, comment.getRecently_posted() ? 1 : 0);
        values.put(COLUMN_POSTED_BY_OWNER, comment.getPosted_by_owner() ? 1 : 0);
        values.put(COLUMN_REPLY_URL, comment.getReply_url());
        values.put(COLUMN_CONTENT_COMMENTS, comment.getContent());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_COMMENTS, null, values);
        db.close();
    }


    public List<Comment> findAllProposalComments(String hashProposal) {
        Log.d(TAG, "Find list proposals");

        List<Comment> commentsList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_COMMENTS + " WHERE " + COLUMN_HASH_PROPOSAL + " = '" + hashProposal + "';";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Comment comment = new Comment();
            comment.setHashProposal(cursor.getString(cursor.getColumnIndex(COLUMN_HASH_PROPOSAL)));
            comment.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID_COMMENTS)));
            comment.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
            comment.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_COMMENTS)));
            comment.setDate_human(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_HUMAN)));
            comment.setOrder(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_COMMENTS)));
            comment.setLevel(cursor.getInt(cursor.getColumnIndex(COLUMN_LEVEL)));
            comment.setRecently_posted(cursor.getInt(cursor.getColumnIndex(COLUMN_RECENTLY_POSTED)) != 0);
            comment.setPosted_by_owner(cursor.getInt(cursor.getColumnIndex(COLUMN_POSTED_BY_OWNER)) != 0);
            comment.setReply_url(cursor.getString(cursor.getColumnIndex(COLUMN_REPLY_URL)));
            comment.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT_COMMENTS)));
            commentsList.add(comment);
        }
        cursor.close();
        db.close();
        return commentsList;
    }


    public void deleteAllComments() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_PROPOSALS);
        db.close();
    }


    ////////////////////////////////// EXCHANGE / MARKET ///////////////////////////////////
    public void addMarket(Exchange exchange, Market market, int isDefault) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXCHANGE_PRICE, exchange.getName());
        values.put(COLUMN_MARKET_PRICE, market.getName());
        values.put(COLUMN_PRICE, market.getPrice());
        values.put(COLUMN_DEFAULT, isDefault);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_MARKET_PRICE, null, values);
        db.close();
    }

    public List<Exchange> findExchanges() {

        List<Exchange> exchangeList = new ArrayList<>();
        Exchange exchange = new Exchange();
        exchange.setListMarket(new ArrayList<Market>());

        String query = "SELECT *" +
                " FROM " + TABLE_MARKET_PRICE +
                " ORDER BY " + COLUMN_EXCHANGE_PRICE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            if (exchange.getName() != null) {
                if (!exchange.getName().equals(cursor.getString(cursor.getColumnIndex(COLUMN_EXCHANGE_PRICE)))) {
                    exchangeList.add(exchange);
                    exchange = new Exchange();
                    exchange.setName(cursor.getString(cursor.getColumnIndex(COLUMN_EXCHANGE_PRICE)));
                    exchange.setListMarket(new ArrayList<Market>());
                }
            } else {
                exchange.setName(cursor.getString(cursor.getColumnIndex(COLUMN_EXCHANGE)));
            }

            Market market = new Market();
            market.setName(cursor.getString(cursor.getColumnIndex(COLUMN_MARKET_PRICE)));
            market.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)));
            market.setIsDefault(cursor.getInt(cursor.getColumnIndex(COLUMN_DEFAULT)));

            exchange.getListMarket().add(market);
        }

        exchangeList.add(exchange);

        cursor.close();
        db.close();
        return exchangeList;
    }


    public void updateDefault(Exchange exchange, Market market) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_MARKET_PRICE +
                " SET " + COLUMN_DEFAULT + " = 1" +
                " WHERE " + COLUMN_EXCHANGE_PRICE + " = '" + exchange.getName() + "'" +
                " AND " + COLUMN_MARKET_PRICE + " = '" + market.getName() + "'");
        db.close();
    }


    public void deleteAllMarket() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_MARKET_PRICE);
        db.close();
    }

    ///////////////////////////// PRICE CHART /////////////////////////////////////////
    public void addPriceChart(PriceChartData priceChartData) {
        //Log.d(TAG, "Add price chart");

        ContentValues values = new ContentValues();
        values.put(COLUMN_EXCHANGE, priceChartData.getExchange());
        values.put(COLUMN_MARKET, priceChartData.getMarket());
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


    public List<PriceChartData> findPriceChart(long dateStart, long dateEnd, long gap, Exchange exchange, Market market) {

        // TODO AGGREGATE GAP WITH SQL
        //Log.d(TAG, "Find list Price chart");
        //Log.d(TAG, "Start date : " + dateStart);
        //Log.d(TAG, "End date : " + dateEnd);

        List<PriceChartData> priceChartList = new ArrayList<>();

        String query = "SELECT *" +
                " FROM " + TABLE_PRICE_CHART +
                //" WHERE " + COLUMN_EXCHANGE + " = '" + exchange.getName() + "'" +
                //" AND " + COLUMN_MARKET + " = '" + market.getName() + "'" +
                " WHERE " + COLUMN_TIME + " >= " + dateStart +
                " AND " + COLUMN_TIME + " <= " + dateEnd +
                " AND " + COLUMN_GAP + " = " + gap;

        //Log.d(TAG, "Query : " + query);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            PriceChartData priceChartData = new PriceChartData();
            priceChartData.setExchange(cursor.getString(cursor.getColumnIndex(COLUMN_EXCHANGE)));
            priceChartData.setMarket(cursor.getString(cursor.getColumnIndex(COLUMN_MARKET)));
            priceChartData.setTime(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)));
            priceChartData.setStartGap(cursor.getLong(cursor.getColumnIndex(COLUMN_START_GAP)));
            priceChartData.setEndGap(cursor.getLong(cursor.getColumnIndex(COLUMN_END_GAP)));
            priceChartData.setGap(cursor.getLong(cursor.getColumnIndex(COLUMN_GAP)));
            priceChartData.setClose(cursor.getDouble(cursor.getColumnIndex(COLUMN_CLOSE)));
            priceChartData.setHigh(cursor.getDouble(cursor.getColumnIndex(COLUMN_HIGH)));
            priceChartData.setLow(cursor.getDouble(cursor.getColumnIndex(COLUMN_LOW)));
            priceChartData.setOpen(cursor.getDouble(cursor.getColumnIndex(COLUMN_OPEN)));
            priceChartData.setPairVolume(cursor.getDouble(cursor.getColumnIndex(COLUMN_PAIR_VOLUME)));
            priceChartData.setTrades(cursor.getDouble(cursor.getColumnIndex(COLUMN_TRADES)));
            priceChartData.setVolume(cursor.getDouble(cursor.getColumnIndex(COLUMN_VOLUME)));

            priceChartList.add(priceChartData);
        }
        cursor.close();

        db.close();
        return priceChartList;
    }

    public void deletePriceChart(long dateStart, long dateEnd) {

        Log.d(TAG, "Delete all price chart");

        SQLiteDatabase db = this.getWritableDatabase();
        if (dateStart == 0 && dateEnd == 0) {
            db.execSQL("DELETE FROM " + TABLE_PRICE_CHART);
        } else {
            db.execSQL("DELETE FROM " + TABLE_PRICE_CHART +
                    " WHERE " + COLUMN_TIME + " > " + dateStart +
                    " AND " + COLUMN_TIME + " < " + dateEnd);
        }
        db.close();
    }

    /**
     * Getting the latest record for graph and give it's date
     */
    public long getLatestRecordedDateInGraph(Exchange exchange, Market market) {

        long latestDate = 0;

        String query = "SELECT MAX(" + COLUMN_TIME + ") as " + COLUMN_TIME +
                " FROM " + TABLE_PRICE_CHART +
                " WHERE " + COLUMN_EXCHANGE + " = '" + exchange.getName() + "'" +
                " AND " + COLUMN_MARKET + " = '" + market.getName() + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            latestDate = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME));
        }
        cursor.close();
        db.close();

        return latestDate;

    }
}


