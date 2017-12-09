package com.dash.dashapp.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dash.dashapp.Fragments.NewsFragment;
import com.dash.dashapp.Fragments.PortfolioFragment;
import com.dash.dashapp.Fragments.PriceFragment;
import com.dash.dashapp.Fragments.ProposalsFragment;
import com.dash.dashapp.R;
import com.dash.dashapp.Utils.SharedPreferencesManager;

import java.util.Locale;
import java.util.Map;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements
        PortfolioFragment.OnFragmentInteractionListener,
        PriceFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    private NewsFragment newsFragment;
    private ProposalsFragment proposalFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commit();
                    return true;
                case R.id.navigation_proposals:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, proposalFragment).commit();
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
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickDefaultLanguage();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        newsFragment = NewsFragment.newInstance();
        proposalFragment = ProposalsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commit();

        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }

    private void pickDefaultLanguage() {

        // if default language is null
        if (SharedPreferencesManager.getLanguageRSS(this).equals(SettingsActivity.RSS_LINK_DEF)) {
            // if Device's exist in available dash RSS languages

            for (Map.Entry<String, String> entry : SettingsActivity.listAvailableLanguage.entrySet()) {
                if (Locale.getDefault().getLanguage().equals(entry.getKey())) {
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
}
