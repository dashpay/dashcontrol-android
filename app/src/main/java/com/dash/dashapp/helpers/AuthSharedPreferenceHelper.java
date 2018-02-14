package com.dash.dashapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.dash.dashapp.application.DashControlApplication;

/**
 * Created by Dexter Barretto on 11/2/18.
 * Github : @dbarretto
 */

public class AuthSharedPreferenceHelper {

    private Context context;
    private SharedPreferences sharedPrefAuth;
    private SharedPreferences.Editor editor;

    private static AuthSharedPreferenceHelper authSharedPreferenceHelper;


    public static final String device_id = "device_id";
    public static final String password = "password";
    public static final String fcm_token = "fcm_token";

    public AuthSharedPreferenceHelper() {
        this.context = DashControlApplication.getAppContext();
        this.sharedPrefAuth = context.getSharedPreferences("auth",
                Context.MODE_PRIVATE);
        this.editor = sharedPrefAuth.edit();
    }

    public void setDeviceId(String string) {
        editor.putString(device_id, string);
        editor.commit();
    }

    public void setPassword(String string) {
        editor.putString(password, string);
        editor.commit();
    }

    public void setFcmToken(String string) {
        editor.putString(fcm_token, string);
        editor.commit();
    }

    public String getDeviceId() {
        return sharedPrefAuth.getString(device_id, null);
    }

    public String getPassword() {
        return sharedPrefAuth.getString(password, null);
    }

    public String getFcmToken() {
        return sharedPrefAuth.getString(fcm_token, null);
    }

    public static AuthSharedPreferenceHelper getAuthSharedPreferenceHelper() {
        if (authSharedPreferenceHelper == null) {
            authSharedPreferenceHelper = new AuthSharedPreferenceHelper();
        }
        return authSharedPreferenceHelper;
    }

    public void deleteAuthPreference(String string){
        editor.remove(string).commit();
    }
}
