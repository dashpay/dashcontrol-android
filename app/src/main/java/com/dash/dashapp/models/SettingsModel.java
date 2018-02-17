package com.dash.dashapp.models;

/**
 * Created by Dexter Barretto on 15/2/18.
 * Github : @dbarretto
 */

public class SettingsModel {

    private String title;
    private String URL;

    public SettingsModel(String title, String URL) {
        this.title = title;
        this.URL = URL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
