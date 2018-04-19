package com.dash.dashapp.realm;

import com.dash.dashapp.api.data.DashBlogNews;
import com.dash.dashapp.models.BlogNews;

import java.util.Date;

import io.realm.RealmObject;

public class DashBlogNewsRealm extends RealmObject implements BlogNews.Convertible {

    public String title;
    public String url;
    public String image;
    public Date date;
    public String shortDate;

    public static DashBlogNewsRealm convert(DashBlogNews src) {
        DashBlogNewsRealm blogNews = new DashBlogNewsRealm();
        blogNews.title = src.getTitle();
        blogNews.url = src.url;
        blogNews.image = src.image;
        blogNews.date = src.getDate();
        blogNews.shortDate = src.shortDate;
        return blogNews;
    }

    @Override
    public BlogNews convert() {
        BlogNews blogNews = new BlogNews();
        blogNews.title = title;
        blogNews.url = url;
        blogNews.image = image;
        blogNews.date = date;
        blogNews.cached = true;
        return blogNews;
    }
}