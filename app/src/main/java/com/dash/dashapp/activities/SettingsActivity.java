package com.dash.dashapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dash.dashapp.R;
import com.dash.dashapp.utils.MyDBHandler;
import com.dash.dashapp.utils.SharedPreferencesManager;
import com.dash.dashapp.utils.URLs;

import java.util.HashMap;
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
    ListView listLanguages;

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
        listLanguages = (ListView) findViewById(R.id.list_languages);

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, languages);


        // Assign adapter to ListView
        listLanguages.setAdapter(adapter);

        // ListView Item Click Listener
        listLanguages.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (!SharedPreferencesManager.getLanguageRSS(getApplicationContext()).equals(values[position])) {
                    SharedPreferencesManager.setLanguageRSS(getApplicationContext(), values[position]);

                    MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null);
                    dbHandler.deleteAllNews();
                }
            }

        });
    }
}
