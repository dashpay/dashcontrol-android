package com.dash.dashapp.models;

import com.dash.dashapp.utils.PrimaryKeyFactory;
import com.dash.dashapp.utils.URLs;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BlogNews extends RealmObject {

    public interface Field {
        String TITLE = "title";
        String DATE = "date";
    }

    @PrimaryKey
    private long id = PrimaryKeyFactory.nextKey(BlogNews.class);

    public String title;
    public String url;
    public String image;
    public Date date;

    public long getId() {
        return id;
    }

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