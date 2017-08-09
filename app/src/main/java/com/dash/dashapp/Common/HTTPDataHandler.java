package com.dash.dashapp.Common;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sebas on 8/5/2017.
 */

public class HTTPDataHandler {

    private static final String TAG = "HTTPDataHandler";
    static String stream = null;

    public HTTPDataHandler() {
    }

    public InputStream GetHTTPData(String urlString){
        InputStream in = null;
        try{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                in = new BufferedInputStream(urlConnection.getInputStream());

                /*BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null){
                    sb.append(line);
                }
                stream = sb.toString();*/
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }
}
