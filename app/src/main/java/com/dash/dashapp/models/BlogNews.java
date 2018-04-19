package com.dash.dashapp.models;

import com.dash.dashapp.utils.URLs;

import java.util.Date;

public class BlogNews {

    public String title;
    public String url;
    public String image;
    public Date date;
    public boolean cached;

    public String getImageUrl() {
        return (URLs.DASH_BLOG_API + image).replace("//", "/");
    }

    public String getBlogPostUrl() {
        return (URLs.DASH_BLOG_API + url).replace("//", "/");
    }

    public interface Convertible {
        BlogNews convert();
    }
}