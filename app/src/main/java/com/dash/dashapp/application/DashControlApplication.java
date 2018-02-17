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
import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.models.PriceChartData;
import com.dash.dashapp.utils.DateUtil;
import com.dash.dashapp.utils.MyDBHandler;
import com.dash.dashapp.utils.MySingleton;
import com.dash.dashapp.utils.SharedPreferencesManager;
import com.dash.dashapp.utils.URLs;
import com.facebook.stetho.Stetho;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sebas on 9/18/2017.
 */
public class DashControlApplication extends Application {
    private static final String TAG = "DashControlApplication";
    private Locale locale = null;

    private int i;
    private long currentDate;
    private static Context mContext;
    private boolean isDatabaseEmpty = true;

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.setLocale(locale);
            getApplicationContext().createConfigurationContext(newConfig);
        }
    }*/

    @Override
    public void onCreate() {
        super.onCreate();

        //pickDefaultLanguage();

        Stetho.initializeWithDefaults(this);

        i = 0;

        MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null);


        // TODO only download new data
        //Getting latest database data
        long latestDate = dbHandler.getLatestRecordedDateInGraph();

        currentDate = System.currentTimeMillis();

        // Deleting data older than 3 months
        dbHandler.deletePriceChart(0, currentDate - DateUtil.THREE_MONTHS_INTERVAL);

        long startDate = currentDate - DateUtil.SIX_HOURS_INTERVAL;
        long endDate = currentDate;

        if (latestDate != 0) {
            startDate = latestDate;
            isDatabaseEmpty = false;
        } else {
            isDatabaseEmpty = true;
        }

        Log.d("DateDebug", "Querying server with start date : " + DateUtil.getDate(startDate));
        Log.d("DateDebug", "Querying server with end date : " + DateUtil.getDate(endDate));

        importChartData(startDate, endDate);

        mContext = getApplicationContext();

    }

    private void importChartData(long startDate, long endDate) {

        Log.d(TAG, "Intervale : " + DateUtil.intervalArray[i]);


        Log.d("DateDebug", "Postman test startDate : " + startDate
                + " endDate : " + endDate);

        String startDateString = "start=" + startDate;
        String endDateString = "&end=" + endDate;
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

                                        long timestampMilli = DateUtil.dateStringToMilliseconds(jsonobj.getString("time"));
                                        double close = (jsonobj.getDouble("close"));
                                        double high = (jsonobj.getInt("high"));
                                        double low = (jsonobj.getInt("low"));
                                        double open = (jsonobj.getInt("open"));
                                        double pairVolume = (jsonobj.getInt("pairVolume"));
                                        double trades = (jsonobj.getInt("trades"));
                                        double volume = (jsonobj.getInt("volume"));

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


                                    if (isDatabaseEmpty) {

                                        //This part downloads the next interval (we first download the firsts 6 hours, then fill to the first 24h, etc ...
                                        i++;

                                        Log.e(TAG, "i  : " + i);

                                        if (i < DateUtil.intervalArray.length) {

                                            long startDate = currentDate - DateUtil.intervalArray[i];
                                            long endDate = currentDate - DateUtil.intervalArray[i - 1] - 1; // -1 is to avoid getting the start value from the previous query

                                            importChartData(startDate, endDate);
                                        } else {
                                            i = 0;
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