package com.dash.dashapp.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.service.PriceDataService;
import com.dash.dashapp.utils.SharedPreferencesManager;
import com.dash.dashapp.utils.URLs;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.Locale;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by sebas on 9/18/2017.
 */
public class DashControlApplication extends Application {
    private static final String TAG = "DashControlApplication";
    private Locale locale = null;

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

        mContext = getApplicationContext();

        pickDefaultLanguage();

        initRealm();

        startPriceChartService();
    }

    public void startPriceChartService() {
        Intent intent = new Intent(this, PriceDataService.class);
        startService(intent);
    }

    private void initRealm() {
        Realm.init(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build()
        );
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