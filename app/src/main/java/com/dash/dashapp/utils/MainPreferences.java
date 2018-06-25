package com.dash.dashapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainPreferences {

    public class Contract {
        static final String KEY_LATEST_NEWS_PAGE = "key_latest_news_page";
        static final String KEY_NEWS_HISTORY_COMPLETE = "key_news_history_complete";
    }

    private final SharedPreferences preferences;

    public MainPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isNewsHistoryComplete() {
        return preferences.getBoolean(Contract.KEY_NEWS_HISTORY_COMPLETE, false);
    }

    public void setNewsHistoryComplete(boolean value) {
        save(Contract.KEY_NEWS_HISTORY_COMPLETE, value);
    }

    public int getLatestNewsPage() {
        return preferences.getInt(Contract.KEY_LATEST_NEWS_PAGE, 0);
    }

    public void setLatestNewsPage(int value) {
        save(Contract.KEY_LATEST_NEWS_PAGE, value);
    }

    private void save(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void save(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}