package com.dash.dashapp.Interface;

import com.dash.dashapp.Model.News;

import java.util.List;

/**
 * Created by sebas on 8/7/2017.
 */

public interface RSSUpdateListener {

    void onUpdateStarted();

    void onFirstBatchNewsCompleted(List<News> newsList);

    void onDatabaseUpdateCompleted();


}
