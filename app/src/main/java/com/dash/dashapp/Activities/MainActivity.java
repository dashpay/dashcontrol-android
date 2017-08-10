package com.dash.dashapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dash.dashapp.Fragments.NewsFragment;
import com.dash.dashapp.Fragments.PortfolioFragment;
import com.dash.dashapp.Fragments.PriceFragment;
import com.dash.dashapp.Fragments.ProposalsFragment;
import com.dash.dashapp.Fragments.dummy.DummyContent;
import com.dash.dashapp.R;

public class MainActivity extends AppCompatActivity implements
        PortfolioFragment.OnFragmentInteractionListener,
        PriceFragment.OnFragmentInteractionListener,
        ProposalsFragment.OnListFragmentInteractionListener{

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
