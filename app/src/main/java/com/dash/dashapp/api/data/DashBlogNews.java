package com.dash.dashapp.api.data;

import android.text.Html;

import com.dash.dashapp.models.BlogNews;

import java.util.Date;

public class DashBlogNews implements BlogNews.Convertible {

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
        blogNews.setTitle(getTitle());
        blogNews.setUrl(url);
        blogNews.setImage(image);
        blogNews.setDate(date);
        return blogNews;
    }
}
