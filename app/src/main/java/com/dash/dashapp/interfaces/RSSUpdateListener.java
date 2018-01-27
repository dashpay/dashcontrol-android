package com.dash.dashapp.interfaces;

import com.dash.dashapp.models.News;

import java.util.List;

/**
 * Created by sebas on 8/7/2017.
 */

public interface RSSUpdateListener {

    void onUpdateStarted();
    void onFirstBatchNewsCompleted(List<News> newsList);
    void onDatabaseUpdateCompleted();


}
