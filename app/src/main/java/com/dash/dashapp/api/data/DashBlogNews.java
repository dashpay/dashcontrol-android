package com.dash.dashapp.api.data;

import android.text.Html;

import com.dash.dashapp.models.BlogNews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashBlogNews implements BlogNews.Convertible {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);

    public String title;
    public String url;
    public String image;
    public String date;
    public String shortDate;

    public Date getDate() {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public String getTitle() {
        return Html.fromHtml(title).toString();
    }

    @Override
    public BlogNews convert() {
        BlogNews blogNews = new BlogNews();
        blogNews.title = getTitle();
        blogNews.url = url;
        blogNews.image = image;
        blogNews.date = getDate();
        blogNews.cached = false;
        return blogNews;
    }
}
