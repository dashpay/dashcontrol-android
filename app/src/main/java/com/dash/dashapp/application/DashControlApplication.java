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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
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

        //pickDefaultLanguage();

        i = 0;

        MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null);
        dbHandler.deleteAllPriceChart(0, 0);

        currentDate = DateUtil.timestampMilliToSec();

        long startDate = currentDate - DateUtil.SIX_HOURS_INTERVAL;
        long endDate = currentDate;

        Log.d("DateDebug", "Querying server with start date : " + DateUtil.getDate(startDate*1000));
        Log.d("DateDebug", "Querying server with end date : " + DateUtil.getDate(endDate*1000));

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

                                    for (int i = 0; i < response.length(); i++) {

                                        JSONObject jsonobj = response.getJSONObject(i);

                                        PriceChartData priceChartData = new PriceChartData();

                                        priceChartData.setTime(DateUtil.dateStringToSecond(jsonobj.getString("time")));
                                        priceChartData.setClose(jsonobj.getInt("close"));
                                        priceChartData.setHigh(jsonobj.getInt("high"));
                                        priceChartData.setLow(jsonobj.getInt("low"));
                                        priceChartData.setOpen(jsonobj.getInt("open"));
                                        priceChartData.setPairVolume(jsonobj.getInt("pairVolume"));
                                        priceChartData.setTrades(jsonobj.getInt("trades"));
                                        priceChartData.setVolume(jsonobj.getInt("volume"));

                                        MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null);
                                        dbHandler.addPriceChart(priceChartData);
                                    }



                                    i++;

                                    Log.e(TAG, "i  : " + i);

                                    if (i < DateUtil.intervalArray.length) {

                                        long startDate = currentDate - DateUtil.intervalArray[i];
                                        long endDate = currentDate - DateUtil.intervalArray[i - 1];

                                        importChartData(startDate, endDate);
                                    } else {
                                        i = 0;
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
                            body = new String(error.networkResponse.data,"UTF-8");
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

    public static Context getAppContext(){
        return  mContext;
    }

}