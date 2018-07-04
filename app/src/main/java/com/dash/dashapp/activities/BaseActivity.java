package com.dash.dashapp.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.dash.dashapp.R;

import java.util.Objects;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private ActionBar actionBar;

    protected abstract int getLayoutResourceId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setElevation(0);
        actionBar.setTitle(null);
        actionBar.setCustomView(R.layout.actionbar_logo);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    protected void showBackAction() {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setTitle(int titleId) {
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setTitle(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setTitle(title);
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    protected void startActivityForResult(Uri requestUri, int requestCode, int actionChooserTitleResId, int errorMessageResId) {
        Intent walletUriIntent = new Intent(Intent.ACTION_VIEW, requestUri);
        ComponentName componentName = walletUriIntent.resolveActivity(getPackageManager());
        if (componentName != null) {
            Intent chooserIntent = Intent.createChooser(walletUriIntent, getString(actionChooserTitleResId));
            startActivityForResult(chooserIntent, requestCode);
        } else {
            Toast.makeText(this, errorMessageResId, Toast.LENGTH_LONG).show();
        }
    }
}
