package com.dash.dashapp.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dash.dashapp.Fragments.NewsFragment;
import com.dash.dashapp.Fragments.PortfolioFragment;
import com.dash.dashapp.Fragments.PriceFragment;
import com.dash.dashapp.Fragments.ProposalsFragment;
import com.dash.dashapp.Fragments.dummy.DummyContent;
import com.dash.dashapp.R;
import com.dash.dashapp.Utils.SharedPreferencesManager;

import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        PortfolioFragment.OnFragmentInteractionListener,
        PriceFragment.OnFragmentInteractionListener,
        ProposalsFragment.OnListFragmentInteractionListener{

    private static final String TAG = "MainActivity";
    NewsFragment newsFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commit();
                    return true;
                case R.id.navigation_proposals:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, ProposalsFragment.newInstance()).commit();
                    return true;
                case R.id.navigation_prices:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, PriceFragment.newInstance()).commit();
                    return true;
                case R.id.navigation_portfolio:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, PortfolioFragment.newInstance()).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickDefaultLanguage();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        newsFragment = NewsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commit();


    }

    private void pickDefaultLanguage() {

        // if default language is null
        if (SharedPreferencesManager.getLanguageRSS(this).equals(SettingsActivity.RSS_LINK_DEF)){
            // if Device's exist in available dash RSS languages

            for (Map.Entry<String, String> entry : SettingsActivity.listAvailableLanguage.entrySet())
            {
                if (Locale.getDefault().getLanguage().equals(entry.getKey())){
                    // Make default language device's language
                    SharedPreferencesManager.setLanguageRSS(this, entry.getValue());
                    return;
                }
            }
            // else english
            SharedPreferencesManager.setLanguageRSS(this, SettingsActivity.RSS_LINK_EN);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
