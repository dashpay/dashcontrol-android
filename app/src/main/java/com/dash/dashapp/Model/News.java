package com.dash.dashapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sebas on 8/5/2017.
 */


public class News {
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("pubDate")
    @Expose
    private String pubDate;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("guid")
    @Expose
    private String guid;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("description")
    @Expose
    private List<Object> enclosure;

    public News() {
    }

    public List<Object> categories;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Object> getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(List<Object> enclosure) {
        this.enclosure = enclosure;
    }

    public List<Object> getCategories() {
        return categories;
    }

    public void setCategories(List<Object> categories) {
        this.categories = categories;
    }
}