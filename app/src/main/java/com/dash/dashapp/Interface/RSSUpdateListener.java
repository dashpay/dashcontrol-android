package com.dash.dashapp.Interface;

import com.dash.dashapp.Model.News;

import java.util.ArrayList;

/**
 * Created by sebas on 8/7/2017.
 */

public interface RSSUpdateListener {

    void onUpdateStarted();
    void onFirstBatchNewsCompleted(ArrayList<News> newsList);
    void onDatabaseUpdateCompleted();


}
