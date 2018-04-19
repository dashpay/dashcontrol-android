package com.dash.dashapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dash.dashapp.R;

import java.util.Objects;

import butterknife.BindView;

public class SimpleWebViewActivity extends BaseActivity {

    private static final String TITLE_EXTRA = "title_extra";

    @BindView(R.id.web_view)
    WebView webView;

    public static Intent createIntent(Context context, String title, String url) {
        Intent intent = new Intent(context, SimpleWebViewActivity.class);
        intent.setData(Uri.parse(url));
        intent.putExtra(TITLE_EXTRA, title);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_simplewebview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String title = getIntent().getStringExtra(TITLE_EXTRA);
        setTitle(title);
        showBackAction();

        setupWebView();

        Uri uri = Objects.requireNonNull(getIntent().getData());
        webView.loadUrl(uri.toString());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        webView.setInitialScale(1);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        webView.setScrollbarFadingEnabled(false);
    }
}