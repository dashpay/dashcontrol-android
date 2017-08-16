package com.dash.dashapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.dash.dashapp.Activities.ContentRSSActivity;
import com.dash.dashapp.Activities.SettingsActivity;
import com.dash.dashapp.Model.News;
import com.dash.dashapp.R;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

/**
 * Created by sebas on 8/8/2017.
 */

@Layout(R.layout.news_view)
public class NewsView {

    private static final String CONTENT_NEWS = "content_rss";
    @View(R.id.title)
    private TextView titleTxt;

    @View(R.id.date)
    private TextView dateTxt;

    private News mNews;
    private Context mContext;

    public NewsView(Context context, News news) {
        mContext = context;
        mNews = news;
    }

    @Resolve
    private void onResolved() {
        titleTxt.setText(mNews.getTitle());
        dateTxt.setText(mNews.getPubDate());
        //Glide.with(mContext).load(mInfo.getImageUrl()).into(imageView);
    }



    @Click(R.id.news_row)
    private void onClick(){
        Intent intent = new Intent(mContext, ContentRSSActivity.class);
        intent.putExtra(CONTENT_NEWS, mNews.getContent());
        mContext.startActivity(intent);
    }
}
