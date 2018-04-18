package com.dash.dashapp.models;

import java.util.Date;

public class BlogNews {

    public String title;
    public String url;
    public String image;
    public Date date;
    public boolean cached;

    public interface Convertible {
        BlogNews convert();
    }
}