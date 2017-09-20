package com.dash.dashapp.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.dash.dashapp.R;

public class ContentRSSActivity extends AppCompatActivity {

    private static final String CONTENT_RSS = "content_rss";
    private TextView rss_content_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_rss);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String contentString = intent.getStringExtra(CONTENT_RSS);

        rss_content_view = (TextView) findViewById(R.id.content_view);
        rss_content_view.setMovementMethod(LinkMovementMethod.getInstance());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            rss_content_view.setText(Html.fromHtml(contentString,Html.FROM_HTML_MODE_LEGACY));
        } else {
            rss_content_view.setText(Html.fromHtml(contentString));
        }
    }


    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
