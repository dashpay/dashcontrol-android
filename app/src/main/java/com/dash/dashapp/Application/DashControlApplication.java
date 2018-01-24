package com.dash.dashapp.Application;

import android.app.Application;
import android.content.res.Configuration;

import com.dash.dashapp.Utils.SharedPreferencesManager;

import java.util.Locale;

/**
 * Created by sebas on 9/18/2017.
 */
public class DashControlApplication extends Application {
    private Locale locale = null;

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
    }
}