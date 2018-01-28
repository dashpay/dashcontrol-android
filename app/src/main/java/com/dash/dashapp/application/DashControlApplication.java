package com.dash.dashapp.application;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.utils.MySingleton;
import com.dash.dashapp.utils.SharedPreferencesManager;
import com.dash.dashapp.utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

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
            newConfig.setLocale(locale);
            getApplicationContext().createConfigurationContext(newConfig);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //pickDefaultLanguage();

        importChartData();
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

    private void importChartData() {

        Date currentTime = Calendar.getInstance().getTime();

        long startDate = currentTime.getTime() - SIX_HOURS_INTERVAL;
        long endDate = currentTime.getTime();

        String startDateString = "start=" + startDate;
        String endDateString = "&end=" + endDate;
        String marketString = "&market=DASH_USDT";
        String exchangeString = "&exchange=poloniex";

        // DEBUG NO limit
        String noLimit = "&noLimit";

        String URLGraph = URLs.URL_GRAPH + startDateString + endDateString + marketString + exchangeString + noLimit;


        Log.d(TAG, "Retrieving price data : " + URLGraph);

        // For debug purpose when server does not send message back
        String jsonString = "[{\"time\":\"2018-01-27T07:50:00.000Z\",\"close\":761.69261776,\"high\":761.69261776,\"low\":761.69261776,\"open\":761.69261776,\"pairVolume\":143.36117623,\"trades\":9,\"volume\":0.18821395000000002},{\"time\":\"2018-01-27T07:55:00.000Z\",\"close\":761.69262112,\"high\":768.14289846,\"low\":761.69262112,\"open\":768.14289845,\"pairVolume\":17.98223758,\"trades\":3,\"volume\":0.02357443},{\"time\":\"2018-01-27T08:00:00.000Z\",\"close\":761.99929985,\"high\":768.1392305,\"low\":761.69262175,\"open\":768.1392305,\"pairVolume\":103.09073902000002,\"trades\":4,\"volume\":0.13441663},{\"time\":\"2018-01-27T08:05:00.000Z\",\"close\":767.99910088,\"high\":767.99910089,\"low\":767.99910079,\"open\":767.99910079,\"pairVolume\":673.2444162199998,\"trades\":12,\"volume\":0.87662136},{\"time\":\"2018-01-27T08:10:00.000Z\",\"close\":768.20831676,\"high\":768.20831676,\"low\":767.99910079,\"open\":767.99910079,\"pairVolume\":1488.0962323700003,\"trades\":22,\"volume\":1.93728224},{\"time\":\"2018-01-27T08:15:00.000Z\",\"close\":771.55690987,\"high\":771.81320907,\"low\":768.14037699,\"open\":771.64135596,\"pairVolume\":2614.4459242499997,\"trades\":14,\"volume\":3.39329044},{\"time\":\"2018-01-27T08:20:00.000Z\",\"close\":773.55060698,\"high\":773.55060698,\"low\":767.99910079,\"open\":768.140377,\"pairVolume\":1579.34279617,\"trades\":5,\"volume\":2.0563814800000006},{\"time\":\"2018-01-27T08:25:00.000Z\",\"close\":766.67190014,\"high\":773.54171467,\"low\":766.67190014,\"open\":767.99910079,\"pairVolume\":217.57885602,\"trades\":4,\"volume\":0.28137722000000004},{\"time\":\"2018-01-27T08:30:00.000Z\",\"close\":764.29967512,\"high\":770,\"low\":764.29967512,\"open\":770,\"pairVolume\":219.30360239,\"trades\":5,\"volume\":0.28500758000000004},{\"time\":\"2018-01-27T08:35:00.000Z\",\"close\":764.1591053,\"high\":764.1591053,\"low\":764.1591053,\"open\":764.1591053,\"pairVolume\":44.33664882,\"trades\":2,\"volume\":0.05802018},{\"time\":\"2018-01-27T08:40:00.000Z\",\"close\":763.59906687,\"high\":763.59906687,\"low\":763.59906687,\"open\":763.59906687,\"pairVolume\":9.33099733,\"trades\":1,\"volume\":0.01221976},{\"time\":\"2018-01-27T08:45:00.000Z\",\"close\":761.99929983,\"high\":763.59906685,\"low\":761.99929983,\"open\":763.59906685,\"pairVolume\":1798.6542735599996,\"trades\":8,\"volume\":2.35877941}]";

        try {
            JSONArray jsonarray = new JSONArray(jsonString);

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobj = null;
                jsonobj = jsonarray.getJSONObject(i);

                Log.d(TAG, "time : " + jsonobj.getString("time"));
                Log.d(TAG, "close : " + jsonobj.getInt("close"));
                Log.d(TAG, "high : " + jsonobj.getInt("high"));
                Log.d(TAG, "low : " + jsonobj.getInt("low"));
                Log.d(TAG, "open : " + jsonobj.getInt("open"));
                Log.d(TAG, "pairVolume : " + jsonobj.getInt("pairVolume"));
                Log.d(TAG, "trades : " + jsonobj.getInt("trades"));
                Log.d(TAG, "volume : " + jsonobj.getInt("volume"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    // Getting exchanges (default exchange to display)
    JsonObjectRequest jsObjRequestExchanges = new JsonObjectRequest
            (Request.Method.GET, URLGraph, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {

                        JSONArray jsonarray = new JSONArray(response);

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject jsonobj = jsonarray.getJSONObject(i);

                            Log.d(TAG, "time : " + jsonobj.getString("time"));
                            Log.d(TAG, "close : " + jsonobj.getInt("close"));
                            Log.d(TAG, "high : " + jsonobj.getInt("high"));
                            Log.d(TAG, "low : " + jsonobj.getInt("low"));
                            Log.d(TAG, "open : " + jsonobj.getInt("open"));
                            Log.d(TAG, "pairVolume : " + jsonobj.getInt("pairVolume"));
                            Log.d(TAG, "trades : " + jsonobj.getInt("trades"));
                            Log.d(TAG, "volume : " + jsonobj.getInt("volume"));
                        }

                            /*JSONArray names = response.names();
                            JSONArray values = response.toJSONArray(names);
                            for(int i=0; i<values.length(); i++){
                                if (names.getString(i).equals("time")){
                                    Log.d(TAG, "time : " + values.getString(i));
                                }
                                else if (names.getString(i).equals("close")){
                                    Log.d(TAG, "close : " + values.getInt(i));
                                }
                                else if (names.getString(i).equals("high")){
                                    Log.d(TAG, "high : " + values.getInt(i));
                                }
                                else if (names.getString(i).equals("low")){
                                    Log.d(TAG, "low : " + values.getInt(i));
                                }
                                else if (names.getString(i).equals("open")){
                                    Log.d(TAG, "open : " + values.getInt(i));
                                }
                                else if (names.getString(i).equals("pairVolume")){
                                    Log.d(TAG, "pairVolume : " + values.getInt(i));
                                }
                                else if (names.getString(i).equals("trades")){
                                    Log.d(TAG, "trades : " + values.getInt(i));
                                }
                                else if (names.getString(i).equals("volume")){
                                    Log.d(TAG, "volume : " + values.getInt(i));
                                }
                            }*/

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
        MySingleton.getInstance(

    getApplicationContext()).

    addToRequestQueue(jsObjRequestExchanges);
}

}