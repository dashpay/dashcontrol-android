package com.dash.dashapp.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.models.Exchange;
import com.dash.dashapp.models.Market;
import com.dash.dashapp.models.PriceChartData;
import com.dash.dashapp.utils.DateUtil;
import com.dash.dashapp.utils.MyDBHandler;
import com.dash.dashapp.utils.MySingleton;
import com.dash.dashapp.utils.SharedPreferencesManager;
import com.dash.dashapp.utils.URLs;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by sebas on 9/18/2017.
 */
public class DashControlApplication extends Application {
    private static final String TAG = "DashControlApplication";
    private Locale locale = null;

    private int indexIntervals;
    private long currentDate;
    private static Context mContext;
    private boolean downloadStepByStep = true;

    private Exchange currentExchange = new Exchange();
    private Market currentMarket = new Market();

    private List<Exchange> listExchanges;
    private MyDBHandler dbHandler;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.setLocale(locale);
            getApplicationContext().createConfigurationContext(newConfig);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        pickDefaultLanguage();

        setMarketAndExchanges();

        initRealm();
    }

    private void initRealm() {
        Realm.init(this);
//        Stetho.initializeWithDefaults(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                .build()
        );
        /*
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        */
    }


    private void setMarketAndExchanges() {

        dbHandler = new MyDBHandler(getApplicationContext(), null);

        dbHandler.deleteAllMarket();

        // Getting prices
        JsonObjectRequest jsObjRequestPrice = new JsonObjectRequest
                (Request.Method.GET, URLs.URL_PRICE, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "Loading Exchanges");

                        listExchanges = new ArrayList<>();

                        try {

                            Iterator<String> listExchangesKeys = response.keys();


                            // Getting exchanges
                            while (listExchangesKeys.hasNext()) {

                                //Foreach exchange getting the market
                                List<Market> listMarket = new ArrayList<>();


                                String exchangeName = listExchangesKeys.next();

                                Exchange exchange = new Exchange();
                                exchange.setName(exchangeName);

                                // get the value i care about
                                JSONObject exchangeJson = (JSONObject) response.get(exchangeName);

                                try {
                                    double dash_btc = exchangeJson.getDouble("DASH_BTC");
                                    Market market = new Market("DASH_BTC", dash_btc);
                                    listMarket.add(market);

                                    dbHandler.addMarket(exchange, market, 0);

                                } catch (Exception e) {
                                    e.getMessage();
                                }
                                try {
                                    double dash_usd = exchangeJson.getDouble("DASH_USD");
                                    Market market = new Market("DASH_USD", dash_usd);
                                    listMarket.add(market);

                                    dbHandler.addMarket(exchange, market, 0);

                                } catch (Exception e) {
                                    e.getMessage();
                                }
                                try {
                                    double dash_usdt = exchangeJson.getDouble("DASH_USDT");
                                    Market market = new Market("DASH_USDT", dash_usdt);
                                    listMarket.add(market);

                                    dbHandler.addMarket(exchange, market, 0);

                                } catch (Exception e) {
                                    e.getMessage();
                                }

                                exchange.setListMarket(listMarket);
                                listExchanges.add(exchange);
                            }

                            setDefaultExchanges();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.getMessage();
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(mContext).addToRequestQueue(jsObjRequestPrice);
    }

    private void setDefaultExchanges() {

        // Getting exchanges (default exchange to display)
        JsonObjectRequest jsObjRequestExchanges = new JsonObjectRequest
                (Request.Method.GET, URLs.URL_EXCHANGES, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d(TAG, "Loading Default values");

                            JSONObject price = response.getJSONObject("default");
                            currentExchange.setName(price.getString("exchange"));
                            currentMarket.setName(price.getString("market"));

                            dbHandler.updateDefault(currentExchange, currentMarket);

                        } catch (Exception e) {
                            e.getMessage();
                        }


                        //////////////// IMPORTING GRAPH PRICES /////////////////

                        // Deleting data older than 3 months
                        dbHandler.deletePriceChart(0, currentDate - DateUtil.THREE_MONTHS_INTERVAL);

                        // Importing graph prices based on available Exchanges and market
                        currentDate = System.currentTimeMillis();
                        indexIntervals = 0;

                        //Getting latest database data
                        long latestDate = dbHandler.getLatestRecordedDateInGraph(currentExchange, currentMarket);

                        long startDate = currentDate - DateUtil.intervalArray[indexIntervals];
                        long endDate = currentDate;

                        if (latestDate != 0) {
                            startDate = latestDate;
                            downloadStepByStep = false;
                        } else {
                            downloadStepByStep = true;
                        }

                        importChartData(startDate, endDate, currentExchange, currentMarket);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.getMessage();
                        Log.d(TAG, "Error : " + error.getMessage());

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(mContext).addToRequestQueue(jsObjRequestExchanges);
    }

    private void importChartData(long startDate, long endDate, final Exchange exchange, final Market market) {

        startDate = DateUtil.convertToUTC(startDate);
        endDate = DateUtil.convertToUTC(endDate);

        Log.d(TAG, "Intervale : " + DateUtil.intervalArray[indexIntervals]);

        Log.d("DateDebug", "Postman test startDate : " + startDate
                + " endDate : " + endDate);

        String startDateString = "start=" + startDate;
        String endDateString = "&end=" + endDate;
        //String marketString = "&market=" + market.getName();
        //String exchangeString = "&exchange=" + exchange.getName();
        String marketString = "&market=DASH_USDT";
        String exchangeString = "&exchange=poloniex";

        // DEBUG NO limit
        String noLimit = "&noLimit";

        String URLGraph = URLs.URL_GRAPH + startDateString + endDateString + marketString + exchangeString + noLimit;

        Log.d(TAG, "URLGraph : " + URLGraph);

        // Getting exchanges (default exchange to display)
        JsonArrayRequest jsObjRequestExchanges = new JsonArrayRequest
                (Request.Method.GET, URLGraph, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(final JSONArray response) {

                        new Thread(new Runnable() {
                            public void run() {
                                try {

                                    List<PriceChartData> listPriceChartDataFifteenMinutes = new ArrayList<>();
                                    List<PriceChartData> listPriceChartDataThirtyMinutes = new ArrayList<>();
                                    List<PriceChartData> listPriceChartDataTwoHours = new ArrayList<>();
                                    List<PriceChartData> listPriceChartDataFourHours = new ArrayList<>();
                                    List<PriceChartData> listPriceChartDataTwentyFourHours = new ArrayList<>();

                                    long firstGapFifteenMinutes = 0;
                                    long firstGapThirtyMinutes = 0;
                                    long firstGapTwoHours = 0;
                                    long firstGapFourHours = 0;
                                    long firstGapTwentyFourHours = 0;

                                    for (int i = 0; i < response.length(); i++) {

                                        JSONObject jsonobj = response.getJSONObject(i);

                                        PriceChartData priceChartData = new PriceChartData();

                                        long timestampMilli = DateUtil.UTCToLocale(DateUtil.dateStringToMilliseconds(jsonobj.getString("time")));
                                        double close = (jsonobj.getDouble("close"));
                                        double high = (jsonobj.getInt("high"));
                                        double low = (jsonobj.getInt("low"));
                                        double open = (jsonobj.getInt("open"));
                                        double pairVolume = (jsonobj.getInt("pairVolume"));
                                        double trades = (jsonobj.getInt("trades"));
                                        double volume = (jsonobj.getInt("volume"));

                                        priceChartData.setExchange(exchange.getName());
                                        priceChartData.setMarket(market.getName());
                                        priceChartData.setTime(timestampMilli);
                                        priceChartData.setStartGap(timestampMilli);
                                        priceChartData.setEndGap(timestampMilli + DateUtil.FIVE_MINUTES_GAP);
                                        priceChartData.setGap(DateUtil.FIVE_MINUTES_GAP);
                                        priceChartData.setClose(close);
                                        priceChartData.setHigh(high);
                                        priceChartData.setLow(low);
                                        priceChartData.setOpen(open);
                                        priceChartData.setPairVolume(pairVolume);
                                        priceChartData.setTrades(trades);
                                        priceChartData.setVolume(volume);

                                        MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null);

                                        // Adding the 5 minutes gap to database
                                        dbHandler.addPriceChart(priceChartData);

                                        if (i == 0) {
                                            firstGapFifteenMinutes = timestampMilli;
                                            firstGapThirtyMinutes = timestampMilli;
                                            firstGapTwoHours = timestampMilli;
                                            firstGapFourHours = timestampMilli;
                                            firstGapTwentyFourHours = timestampMilli;
                                        }

                                        // If we reach the maximum gap value then start over to the next
                                        if (timestampMilli >= (firstGapFifteenMinutes + DateUtil.FIFTEEN_MINUTES_GAP)) {
                                            // first aggregate and insert data
                                            PriceChartData priceChartDataFifteenMinutes = new PriceChartData();

                                            priceChartDataFifteenMinutes.setGap(DateUtil.FIFTEEN_MINUTES_GAP);

                                            priceChartDataFifteenMinutes.setExchange(exchange.getName());
                                            priceChartDataFifteenMinutes.setMarket(market.getName());

                                            PriceChartData firstObject = listPriceChartDataFifteenMinutes.get(0);
                                            PriceChartData lastObject = listPriceChartDataFifteenMinutes.get(listPriceChartDataFifteenMinutes.size() - 1);

                                            priceChartDataFifteenMinutes.setOpen(firstObject.getOpen());
                                            priceChartDataFifteenMinutes.setClose(lastObject.getClose());

                                            long time = firstObject.getTime();
                                            priceChartDataFifteenMinutes.setStartGap(time - (time % DateUtil.FIFTEEN_MINUTES_GAP));
                                            priceChartDataFifteenMinutes.setEndGap(time + (DateUtil.FIFTEEN_MINUTES_GAP - (time % DateUtil.FIFTEEN_MINUTES_GAP)));
                                            priceChartDataFifteenMinutes.setTime(time + (DateUtil.FIFTEEN_MINUTES_GAP - (time % DateUtil.FIFTEEN_MINUTES_GAP)));


                                            double sumPairVolume = 0;
                                            double sumTrades = 0;
                                            double sumVolume = 0;
                                            double minimumLow = firstObject.getLow();
                                            double maximumHigh = firstObject.getHigh();
                                            for (PriceChartData currentPriceChart : listPriceChartDataFifteenMinutes) {
                                                sumPairVolume += currentPriceChart.getPairVolume();
                                                sumTrades += currentPriceChart.getTrades();
                                                sumVolume += currentPriceChart.getVolume();
                                                if (currentPriceChart.getLow() < minimumLow) {
                                                    minimumLow = currentPriceChart.getLow();
                                                }
                                                if (currentPriceChart.getHigh() > maximumHigh) {
                                                    maximumHigh = currentPriceChart.getHigh();
                                                }
                                            }

                                            priceChartDataFifteenMinutes.setPairVolume(sumPairVolume);
                                            priceChartDataFifteenMinutes.setTrades(sumTrades);
                                            priceChartDataFifteenMinutes.setVolume(sumVolume);
                                            priceChartDataFifteenMinutes.setLow(minimumLow);
                                            priceChartDataFifteenMinutes.setHigh(maximumHigh);

                                            dbHandler.addPriceChart(priceChartDataFifteenMinutes);

                                            // Clear data
                                            listPriceChartDataFifteenMinutes.clear();
                                            firstGapFifteenMinutes = timestampMilli;

                                        }

                                        if (timestampMilli >= (firstGapThirtyMinutes + DateUtil.THIRTY_MINUTES_GAP)) {
                                            // first aggregate and insert data
                                            PriceChartData priceChartDataThirtyMinutes = new PriceChartData();

                                            priceChartDataThirtyMinutes.setGap(DateUtil.THIRTY_MINUTES_GAP);

                                            priceChartDataThirtyMinutes.setExchange(exchange.getName());
                                            priceChartDataThirtyMinutes.setMarket(market.getName());

                                            PriceChartData firstObject = listPriceChartDataThirtyMinutes.get(0);
                                            PriceChartData lastObject = listPriceChartDataThirtyMinutes.get(listPriceChartDataThirtyMinutes.size() - 1);

                                            priceChartDataThirtyMinutes.setOpen(firstObject.getOpen());
                                            priceChartDataThirtyMinutes.setClose(lastObject.getClose());

                                            long time = firstObject.getTime();
                                            priceChartDataThirtyMinutes.setStartGap(time - (time % DateUtil.THIRTY_MINUTES_GAP));
                                            priceChartDataThirtyMinutes.setEndGap(time + (DateUtil.THIRTY_MINUTES_GAP - (time % DateUtil.THIRTY_MINUTES_GAP)));
                                            priceChartDataThirtyMinutes.setTime(time + (DateUtil.THIRTY_MINUTES_GAP - (time % DateUtil.THIRTY_MINUTES_GAP)));


                                            double sumPairVolume = 0;
                                            double sumTrades = 0;
                                            double sumVolume = 0;
                                            double minimumLow = firstObject.getLow();
                                            double maximumHigh = firstObject.getHigh();
                                            for (PriceChartData currentPriceChart : listPriceChartDataThirtyMinutes) {
                                                sumPairVolume += currentPriceChart.getPairVolume();
                                                sumTrades += currentPriceChart.getTrades();
                                                sumVolume += currentPriceChart.getVolume();
                                                if (currentPriceChart.getLow() < minimumLow) {
                                                    minimumLow = currentPriceChart.getLow();
                                                }
                                                if (currentPriceChart.getHigh() > maximumHigh) {
                                                    maximumHigh = currentPriceChart.getHigh();
                                                }
                                            }

                                            priceChartDataThirtyMinutes.setPairVolume(sumPairVolume);
                                            priceChartDataThirtyMinutes.setTrades(sumTrades);
                                            priceChartDataThirtyMinutes.setVolume(sumVolume);
                                            priceChartDataThirtyMinutes.setLow(minimumLow);
                                            priceChartDataThirtyMinutes.setHigh(maximumHigh);


                                            dbHandler.addPriceChart(priceChartDataThirtyMinutes);

                                            // Clear data
                                            listPriceChartDataThirtyMinutes.clear();
                                            firstGapThirtyMinutes = timestampMilli;

                                        }

                                        if (timestampMilli >= (firstGapTwoHours + DateUtil.TWO_HOURS_GAP)) {
                                            // first aggregate and insert data
                                            PriceChartData priceChartDataTwoHours = new PriceChartData();

                                            priceChartDataTwoHours.setGap(DateUtil.TWO_HOURS_GAP);

                                            priceChartDataTwoHours.setExchange(exchange.getName());
                                            priceChartDataTwoHours.setMarket(market.getName());

                                            PriceChartData firstObject = listPriceChartDataTwoHours.get(0);
                                            PriceChartData lastObject = listPriceChartDataTwoHours.get(listPriceChartDataTwoHours.size() - 1);

                                            priceChartDataTwoHours.setOpen(firstObject.getOpen());
                                            priceChartDataTwoHours.setClose(lastObject.getClose());

                                            long time = firstObject.getTime();
                                            priceChartDataTwoHours.setStartGap(time - (time % DateUtil.TWO_HOURS_GAP));
                                            priceChartDataTwoHours.setEndGap(time + (DateUtil.TWO_HOURS_GAP - (time % DateUtil.TWO_HOURS_GAP)));
                                            priceChartDataTwoHours.setTime(time + (DateUtil.TWO_HOURS_GAP - (time % DateUtil.TWO_HOURS_GAP)));


                                            double sumPairVolume = 0;
                                            double sumTrades = 0;
                                            double sumVolume = 0;
                                            double minimumLow = firstObject.getLow();
                                            double maximumHigh = firstObject.getHigh();
                                            for (PriceChartData currentPriceChart : listPriceChartDataTwoHours) {
                                                sumPairVolume += currentPriceChart.getPairVolume();
                                                sumTrades += currentPriceChart.getTrades();
                                                sumVolume += currentPriceChart.getVolume();
                                                if (currentPriceChart.getLow() < minimumLow) {
                                                    minimumLow = currentPriceChart.getLow();
                                                }
                                                if (currentPriceChart.getHigh() > maximumHigh) {
                                                    maximumHigh = currentPriceChart.getHigh();
                                                }
                                            }

                                            priceChartDataTwoHours.setPairVolume(sumPairVolume);
                                            priceChartDataTwoHours.setTrades(sumTrades);
                                            priceChartDataTwoHours.setVolume(sumVolume);
                                            priceChartDataTwoHours.setLow(minimumLow);
                                            priceChartDataTwoHours.setHigh(maximumHigh);


                                            dbHandler.addPriceChart(priceChartDataTwoHours);


                                            listPriceChartDataTwoHours.clear();
                                            firstGapTwoHours = timestampMilli;
                                        }

                                        if (timestampMilli >= (firstGapFourHours + DateUtil.FOUR_HOURS_GAP)) {
                                            // first aggregate and insert data
                                            PriceChartData priceChartDataFourHours = new PriceChartData();

                                            priceChartDataFourHours.setGap(DateUtil.FOUR_HOURS_GAP);

                                            priceChartDataFourHours.setExchange(exchange.getName());
                                            priceChartDataFourHours.setMarket(market.getName());

                                            PriceChartData firstObject = listPriceChartDataFourHours.get(0);
                                            PriceChartData lastObject = listPriceChartDataFourHours.get(listPriceChartDataFourHours.size() - 1);

                                            priceChartDataFourHours.setOpen(firstObject.getOpen());
                                            priceChartDataFourHours.setClose(lastObject.getClose());

                                            long time = firstObject.getTime();
                                            priceChartDataFourHours.setStartGap(time - (time % DateUtil.FOUR_HOURS_GAP));
                                            priceChartDataFourHours.setEndGap(time + (DateUtil.FOUR_HOURS_GAP - (time % DateUtil.FOUR_HOURS_GAP)));
                                            priceChartDataFourHours.setTime(time + (DateUtil.FOUR_HOURS_GAP - (time % DateUtil.FOUR_HOURS_GAP)));


                                            double sumPairVolume = 0;
                                            double sumTrades = 0;
                                            double sumVolume = 0;
                                            double minimumLow = firstObject.getLow();
                                            double maximumHigh = firstObject.getHigh();
                                            for (PriceChartData currentPriceChart : listPriceChartDataFourHours) {
                                                sumPairVolume += currentPriceChart.getPairVolume();
                                                sumTrades += currentPriceChart.getTrades();
                                                sumVolume += currentPriceChart.getVolume();
                                                if (currentPriceChart.getLow() < minimumLow) {
                                                    minimumLow = currentPriceChart.getLow();
                                                }
                                                if (currentPriceChart.getHigh() > maximumHigh) {
                                                    maximumHigh = currentPriceChart.getHigh();
                                                }
                                            }

                                            priceChartDataFourHours.setPairVolume(sumPairVolume);
                                            priceChartDataFourHours.setTrades(sumTrades);
                                            priceChartDataFourHours.setVolume(sumVolume);
                                            priceChartDataFourHours.setLow(minimumLow);
                                            priceChartDataFourHours.setHigh(maximumHigh);


                                            dbHandler.addPriceChart(priceChartDataFourHours);


                                            // Clear data
                                            listPriceChartDataFourHours.clear();
                                            firstGapFourHours = timestampMilli;
                                        }

                                        if (timestampMilli >= (firstGapTwentyFourHours + DateUtil.TWENTY_FOUR_HOURS_GAP)) {
                                            // first aggregate and insert data
                                            PriceChartData priceChartDataTwentyFourHours = new PriceChartData();

                                            priceChartDataTwentyFourHours.setGap(DateUtil.TWENTY_FOUR_HOURS_GAP);

                                            priceChartDataTwentyFourHours.setExchange(exchange.getName());
                                            priceChartDataTwentyFourHours.setMarket(market.getName());

                                            PriceChartData firstObject = listPriceChartDataTwentyFourHours.get(0);
                                            PriceChartData lastObject = listPriceChartDataTwentyFourHours.get(listPriceChartDataTwentyFourHours.size() - 1);

                                            priceChartDataTwentyFourHours.setOpen(firstObject.getOpen());
                                            priceChartDataTwentyFourHours.setClose(lastObject.getClose());

                                            long time = firstObject.getTime();
                                            priceChartDataTwentyFourHours.setStartGap(time - (time % DateUtil.TWENTY_FOUR_HOURS_GAP));
                                            priceChartDataTwentyFourHours.setEndGap(time + (DateUtil.TWENTY_FOUR_HOURS_GAP - (time % DateUtil.TWENTY_FOUR_HOURS_GAP)));
                                            priceChartDataTwentyFourHours.setTime(time + (DateUtil.TWENTY_FOUR_HOURS_GAP - (time % DateUtil.TWENTY_FOUR_HOURS_GAP)));


                                            double sumPairVolume = 0;
                                            double sumTrades = 0;
                                            double sumVolume = 0;
                                            double minimumLow = firstObject.getLow();
                                            double maximumHigh = firstObject.getHigh();
                                            for (PriceChartData currentPriceChart : listPriceChartDataTwentyFourHours) {
                                                sumPairVolume += currentPriceChart.getPairVolume();
                                                sumTrades += currentPriceChart.getTrades();
                                                sumVolume += currentPriceChart.getVolume();
                                                if (currentPriceChart.getLow() < minimumLow) {
                                                    minimumLow = currentPriceChart.getLow();
                                                }
                                                if (currentPriceChart.getHigh() > maximumHigh) {
                                                    maximumHigh = currentPriceChart.getHigh();
                                                }
                                            }

                                            priceChartDataTwentyFourHours.setPairVolume(sumPairVolume);
                                            priceChartDataTwentyFourHours.setTrades(sumTrades);
                                            priceChartDataTwentyFourHours.setVolume(sumVolume);
                                            priceChartDataTwentyFourHours.setLow(minimumLow);
                                            priceChartDataTwentyFourHours.setHigh(maximumHigh);


                                            dbHandler.addPriceChart(priceChartDataTwentyFourHours);


                                            // Clear data
                                            listPriceChartDataTwentyFourHours.clear();
                                            firstGapTwentyFourHours = timestampMilli;
                                        }

                                        // Insert new value
                                        listPriceChartDataFifteenMinutes.add(priceChartData);
                                        listPriceChartDataThirtyMinutes.add(priceChartData);
                                        listPriceChartDataTwoHours.add(priceChartData);
                                        listPriceChartDataFourHours.add(priceChartData);
                                        listPriceChartDataTwentyFourHours.add(priceChartData);

                                    }


                                    if (downloadStepByStep) {

                                        //This part downloads the next interval (we first download the firsts 6 hours, then fill to the first 24h, etc ...
                                        indexIntervals++;

                                        Log.e(TAG, "i  : " + indexIntervals);

                                        if (indexIntervals < DateUtil.intervalArray.length) {

                                            long startDate = currentDate - DateUtil.intervalArray[indexIntervals];
                                            long endDate = currentDate - DateUtil.intervalArray[indexIntervals - 1]; // - 1; // -1 is to avoid getting the start value from the previous query

                                            importChartData(startDate, endDate, exchange, market);
                                        } else {
                                            // We continue downloading with another exchange/market
                                            indexIntervals = 0;

                                            //downloadOtherExchangeMarket();
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e(TAG, "Error : " + error.getMessage());
                        Log.e(TAG, "Error StackTrace: \t" + Arrays.toString(error.getStackTrace()));

                        if (error == null || error.networkResponse == null) {
                            return;
                        }

                        String body;
                        //get status code here
                        final String statusCode = String.valueOf(error.networkResponse.statusCode);
                        Log.e(TAG, "statusCode : " + statusCode);
                        //get response body and parse with appropriate encoding
                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                            Log.e(TAG, "body : " + body);
                        } catch (UnsupportedEncodingException e) {
                            // exception
                            Log.e(TAG, "e : " + e.getMessage());

                        }

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequestExchanges);
    }

    /**
     * Perform the download of the non default market and exchange prices in bulk
     */
    private void downloadOtherExchangeMarket() {
        for (Exchange exchange : listExchanges) {
            for (Market market : exchange.getListMarket()) {

                if (!exchange.getName().equals(currentExchange.getName()) || !market.getName().equals(currentMarket.getName())) {


                    // Importing graph prices based on available Exchanges and market
                    currentDate = System.currentTimeMillis();
                    indexIntervals = 0;

                    //Getting latest database data
                    long latestDate = dbHandler.getLatestRecordedDateInGraph(exchange, market);

                    long startDate = currentDate - DateUtil.THREE_MONTHS_INTERVAL;
                    long endDate = currentDate;

                    if (latestDate != 0) {
                        startDate = latestDate;
                    }
                    downloadStepByStep = false;

                    Log.d("DateDebug", "Querying server with start date : " + DateUtil.getDate(startDate));
                    Log.d("DateDebug", "Querying server with end date : " + DateUtil.getDate(endDate));

                    importChartData(startDate, endDate, exchange, market);
                }
            }
        }
    }

    private void pickDefaultLanguage() {

        // if default language is null
        if (SharedPreferencesManager.getLanguageRSS(this).equals(URLs.RSS_LINK_DEF)) {
            // if Device's exist in available dash RSS languages

            for (Map.Entry<String, String> entry : SettingsActivity.listAvailableLanguage.entrySet()) {
                if (Locale.getDefault().getLanguage().equals(entry.getKey())) {
                    // Make default language device's language
                    SharedPreferencesManager.setLanguageRSS(this, entry.getValue());
                    return;
                }
            }
            // else english
            SharedPreferencesManager.setLanguageRSS(this, URLs.RSS_LINK_EN);
        }

        String lang = SharedPreferencesManager.getLanguageRSS(this);
        locale = new Locale(lang);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        //configuration.setLocale(locale);
        getApplicationContext().createConfigurationContext(configuration);
    }

    public static Context getAppContext() {
        return mContext;
    }

}