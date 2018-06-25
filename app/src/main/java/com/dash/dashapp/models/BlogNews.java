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

    private String title;
    private String url;
    private String image;
    private Date date;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageUrl() {
        return URLs.DASH_BLOG_API + getImage().substring(1);
    }

    public String getBlogPostUrl() {
        return URLs.DASH_BLOG_API + getUrl().substring(1);
    }

    public interface Convertible {
        BlogNews convert();
    }
}