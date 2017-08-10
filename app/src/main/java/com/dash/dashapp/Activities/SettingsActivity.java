package com.dash.dashapp.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.Utils.HandleXML;
import com.dash.dashapp.Utils.MyDBHandler;
import com.dash.dashapp.Utils.SharedPreferencesManager;

public class SettingsActivity extends AppCompatActivity {


    public static final String RSS_LINK_EN = "https://www.dash.org/rss/dash_blog_rss.xml";
    private final String RSS_LINK_ES = "https://www.dash.org/es/rss/dash_blog_rss.xml";
    private final String RSS_LINK_FR = "https://www.dash.org/fr/rss/dash_blog_rss.xml";
    private final String RSS_LINK_PT = "https://www.dash.org/pt/rss/dash_blog_rss.xml";
    private final String RSS_LINK_CN = "https://www.dash.org/cn/rss/dash_blog_rss.xml";
    private final String RSS_LINK_RU = "https://www.dash.org/ru/rss/dash_blog_rss.xml";
    private final String RSS_LINK_JP = "https://www.dash.org/jp/rss/dash_blog_rss.xml";
    private final String RSS_LINK_KR = "https://www.dash.org/kr/rss/dash_blog_rss.xml";

    ListView listView;

    private HandleXML obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list_languages);

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
                RSS_LINK_EN,
                RSS_LINK_ES,
                RSS_LINK_FR,
                RSS_LINK_PT,
                RSS_LINK_CN,
                RSS_LINK_RU,
                RSS_LINK_JP,
                RSS_LINK_KR
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, languages);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (!SharedPreferencesManager.getLanguageRSS(getApplicationContext()).equals(values[position])){
                    SharedPreferencesManager.setLanguageRSS(getApplicationContext(), values[position]);

                    MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null);
                    dbHandler.deleteAllNews();
                }
            }

        });
    }
}
