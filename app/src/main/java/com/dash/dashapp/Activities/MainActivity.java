package com.dash.dashapp.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.dash.dashapp.R;
import com.dash.dashapp.Fragments.NewsFragment;
import com.dash.dashapp.Fragments.PortfolioFragment;
import com.dash.dashapp.Fragments.PriceFragment;
import com.dash.dashapp.Fragments.ProposalsFragment;
import com.dash.dashapp.Fragments.dummy.DummyContent;
import com.dash.dashapp.Utils.HandleXML;

public class MainActivity extends FragmentActivity implements
        PortfolioFragment.OnFragmentInteractionListener,
        PriceFragment.OnFragmentInteractionListener,
        ProposalsFragment.OnListFragmentInteractionListener{

    NewsFragment newsFragment;



    private HandleXML obj;
    private final String RSS_LINK = "https://www.dash.org/rss/dash_blog_rss.xml";

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
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        newsFragment = NewsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
