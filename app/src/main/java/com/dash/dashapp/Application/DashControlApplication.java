package com.dash.dashapp.Application;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dash.dashapp.Utils.MySingleton;
import com.dash.dashapp.Utils.SharedPreferencesManager;
import com.dash.dashapp.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by sebas on 9/18/2017.
 */
public class DashControlApplication extends Application {
    private static final String TAG = "DashControlApplication";
    private Locale locale = null;

    private static final long SIX_HOURS_INTERVAL = 1000 * 60 * 60;
    private static final long TWENTY_FOUR_HOURS_INTERVAL = 1000 * 60 * 60 * 24;
    private static final long TWO_DAYS_INTERVAL = 1000 * 60 * 60 * 24 * 2;
    private static final long FOUR_DAYS_INTERVAL = 1000 * 60 * 60 * 24 * 4;
    private static final long ONE_WEEK_INTERVAL = 1000 * 60 * 60 * 24 * 7;
    private static final long TWO_WEEKS_INTERVAL = 1000 * 60 * 60 * 24 * 14;
    private static final long ONE_MONTH_INTERVAL = 1000 * 60 * 60 * 24 * 30;
    private static final long THREE_MONTHS_INTERVAL = 1000 * 60 * 60 * 24 * 90;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration config = getBaseContext().getResources().getConfiguration();

        config = new Configuration(config);

        String lang = SharedPreferencesManager.getLanguageRSS(this);
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
        importChartData();
    }

    private void importChartData() {

        Date currentTime = Calendar.getInstance().getTime();

        long startDate = currentTime.getTime() - SIX_HOURS_INTERVAL;
        long endDate = currentTime.getTime();

        String startDateString = "start=" + startDate;
        String endDateString = "&end=" + endDate;
        String marketString = "&market=DASH_USDT";
        String exchangeString = "&exchange=poloniex";

        String URLGraph = URLs.URL_GRAPH + startDateString + endDateString + marketString + exchangeString;


        Log.d(TAG, "Retrieving price data : " + URLGraph);

        // Getting exchanges (default exchange to display)
        JsonObjectRequest jsObjRequestExchanges = new JsonObjectRequest
                (Request.Method.GET, URLGraph, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            // ...
                            JSONArray json = new JSONArray(response);
                            // ...
                            Log.d(TAG, "Json response : " + json.toString());

                            for(int i=0;i<json.length();i++){
                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject e = json.getJSONObject(i);



                                Log.d(TAG, "time : " + e.getString("time"));
                                Log.d(TAG, "close : " + e.getString("close"));
                                Log.d(TAG, "high : " + e.getString("high"));
                                Log.d(TAG, "low : " + e.getString("low"));
                                Log.d(TAG, "open : " + e.getString("open"));
                                Log.d(TAG, "pairVolume : " + e.getString("pairVolume"));
                                Log.d(TAG, "trades : " + e.getString("trades"));
                                Log.d(TAG, "volume : " + e.getString("volume"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequestExchanges);
    }

}