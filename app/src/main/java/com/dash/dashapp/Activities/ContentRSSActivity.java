package com.dash.dashapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dash.dashapp.R;

import butterknife.BindView;

public class ContentRSSActivity extends BaseActivity {

    private static final String TITLE_NEWS = "title_rss";
    private static final String CONTENT_RSS = "content_rss";
    @BindView(R.id.content_view)
    TextView contentView;
    @BindView(R.id.SCROLLER_ID)
    ScrollView SCROLLERID;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content_rss;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String titleString = intent.getStringExtra(TITLE_NEWS);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        ab.setTitle(titleString);
        String contentString = intent.getStringExtra(CONTENT_RSS);

        contentView.setMovementMethod(LinkMovementMethod.getInstance());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentView.setText(Html.fromHtml(contentString, Html.FROM_HTML_MODE_LEGACY));
        } else {
            contentView.setText(Html.fromHtml(contentString));
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
