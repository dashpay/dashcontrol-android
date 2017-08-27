package com.dash.dashapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.dash.dashapp.R;

public class ProposalDetailActivity extends AppCompatActivity {

    private static final String CONTENT_RSS = "content_rss";
    private TextView rss_content_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_rss);

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
}
