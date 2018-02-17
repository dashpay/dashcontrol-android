package com.dash.dashapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dash.dashapp.R;
import com.dash.dashapp.adapters.SettingsAdapter;
import com.dash.dashapp.models.SettingsModel;
import com.dash.dashapp.utils.URLs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity {

    public static final Map<String, String> listAvailableLanguage = new HashMap<String, String>() {{
        put("en", URLs.RSS_LINK_EN);
        put("es", URLs.RSS_LINK_ES);
        put("fr", URLs.RSS_LINK_FR);
        put("pt", URLs.RSS_LINK_PT);
        put("cn", URLs.RSS_LINK_CN);
        put("ru", URLs.RSS_LINK_RU);
        put("jp", URLs.RSS_LINK_JP);
        put("kr", URLs.RSS_LINK_KR);
    }};
    @BindView(R.id.list_languages)
    RecyclerView listLanguages;
    private SettingsAdapter mSettingsAdapter;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        ab.setTitle("Settings");


        // Get ListView object from xml
        listLanguages = (RecyclerView) findViewById(R.id.list_languages);

        // Defined Array values to show in ListView
        String[] languages = new String[]{
                getString(R.string.eng),
                getString(R.string.esp),
                getString(R.string.fr),
                getString(R.string.pt),
                getString(R.string.cn),
                getString(R.string.ru),
                getString(R.string.jp),
                getString(R.string.kr)
        };

        final String[] values = new String[]{
                URLs.RSS_LINK_EN,
                URLs.RSS_LINK_ES,
                URLs.RSS_LINK_FR,
                URLs.RSS_LINK_PT,
                URLs.RSS_LINK_CN,
                URLs.RSS_LINK_RU,
                URLs.RSS_LINK_JP,
                URLs.RSS_LINK_KR
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data


        List settingsModel = new ArrayList();

        for(int i = 0; i<languages.length; i++ ){
            settingsModel.add(new SettingsModel(languages[i],values[i]));
        }

        mSettingsAdapter = new SettingsAdapter(settingsModel,this);

        // Assign adapter to ListView
        listLanguages.setAdapter(mSettingsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listLanguages.setLayoutManager(linearLayoutManager);
    }

}
