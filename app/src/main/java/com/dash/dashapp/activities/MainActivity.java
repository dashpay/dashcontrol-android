package com.dash.dashapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dash.dashapp.R;
import com.dash.dashapp.fragments.NewsFragment;
import com.dash.dashapp.fragments.PortfolioFragment;
import com.dash.dashapp.fragments.PriceFragment;
import com.dash.dashapp.fragments.ProposalsFragment;

import java.util.Objects;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements
        PortfolioFragment.OnFragmentInteractionListener,
        PriceFragment.OnFragmentInteractionListener {

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
                    switchFragment(newsFragment);
                    return true;
                case R.id.navigation_proposals:
                    switchFragment(proposalFragment);
                    return true;
                case R.id.navigation_prices:
                    switchFragment(PriceFragment.newInstance());
                    return true;
                case R.id.navigation_portfolio:
                    switchFragment(PortfolioFragment.newInstance());
                    return true;
            }
            return false;
        }

    };

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.content, fragment)
                .commit();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        newsFragment = NewsFragment.newInstance();
        proposalFragment = ProposalsFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, newsFragment)
                .commit();

        ActionBar supportActionBar = Objects.requireNonNull(getSupportActionBar());
        supportActionBar.setDisplayShowHomeEnabled(true);
        supportActionBar.setIcon(R.drawable.ic_dash_d_white_24dp);

        supportActionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
