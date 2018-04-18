package com.dash.dashapp.api.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashBlogNews {

    private String title;
    private String url;
    private String image;
    private String date;
    private String shortDate;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    public String getShortDate() {
        return shortDate;
    }

    public Date getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
