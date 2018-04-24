package com.dash.dashapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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
    }

    protected void showBackAction() {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setTitle(int titleId) {
        actionBar.setTitle(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
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
}
