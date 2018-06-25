package com.dash.dashapp.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.service.NewsSyncService;
import com.dash.dashapp.service.PriceDataService;
import com.dash.dashapp.utils.PrimaryKeyFactory;
import com.dash.dashapp.utils.SharedPreferencesManager;
import com.dash.dashapp.utils.URLs;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DashControlApplication extends Application {

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
        startDataSyncServices();
    }

    public void startDataSyncServices() {
        Intent newsSyncServiceIntent = new Intent(this, NewsSyncService.class);
        startService(newsSyncServiceIntent);

        Intent priceDataServiceIntent = new Intent(this, PriceDataService.class);
        startService(priceDataServiceIntent);
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

        try (Realm realm = Realm.getDefaultInstance()) {
            PrimaryKeyFactory.init(realm);
        }

        RealmInspectorModulesProvider realmInspector = RealmInspectorModulesProvider.builder(this)
                .withDeleteIfMigrationNeeded(true)
                .build();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(realmInspector)
                        .build());
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