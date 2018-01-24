package com.dash.dashapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dash.dashapp.Activities.SettingsActivity;
import com.dash.dashapp.R;

public class SharedPreferencesManager {

    private static final String APP_SETTINGS = "APP_SETTINGS";


    // properties
    private static final String SOME_STRING_VALUE = "SOME_STRING_VALUE";
    // other properties...


    private SharedPreferencesManager() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getLanguageRSS(Context context) {
        return getSharedPreferences(context).getString(context.getString(R.string.language_preference), URLs.RSS_LINK_DEF);
    }

    public static void setLanguageRSS(Context context, String languageValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(context.getString(R.string.language_preference), languageValue);
        editor.apply();
    }

    // other getters/setters
}