package com.dash.dashapp.api.data;

import android.text.Html;

import com.dash.dashapp.models.BlogNews;

import java.util.Date;

import io.realm.RealmObject;

public class DashBlogNews extends RealmObject implements BlogNews.Convertible {

    public String title;
    public String url;
    public String image;
    public Date date;
    public String shortDate;

    public String getTitle() {
        return Html.fromHtml(title).toString();
    }

    @Override
    public BlogNews convert() {
        BlogNews blogNews = new BlogNews();
        blogNews.title = getTitle();
        blogNews.url = url;
        blogNews.image = image;
        blogNews.date = date;
        blogNews.cached = false;
        return blogNews;
    }
}
