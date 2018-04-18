package com.dash.dashapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.dash.dashapp.R;
import com.dash.dashapp.activities.ContentRSSActivity;
import com.dash.dashapp.api.data.DashBlogNews;
import com.dash.dashapp.utils.GlideApp;
import com.dash.dashapp.utils.URLs;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Layout(R.layout.news_view)
public class BlogNewsView {

    private static final String TITLE_NEWS = "title_rss";
    private static final String CONTENT_NEWS = "content_rss";

    @View(R.id.title)
    public TextView titleView;

    @View(R.id.date)
    public TextView dateView;

    @View(R.id.thumbnail)
    public ImageView thumbnailView;

    private DashBlogNews blogNews;
    private Context context;

    public BlogNewsView(Context context, DashBlogNews blogNews) {
        this.context = context;
        this.blogNews = blogNews;
    }

    @Resolve
    public void onResolved() {
        String titleHtml = blogNews.getTitle();
        titleView.setText(Html.fromHtml(titleHtml));

        SimpleDateFormat newsDateFormat = new SimpleDateFormat("MMM dd, YYYY", Locale.US);
        dateView.setText(newsDateFormat.format(blogNews.getDate()));

        if (blogNews.getImage() != null) {
            String thumbnailUrl = (URLs.DASH_CONTROL_BASE_API + blogNews.getImage()).replace("//", "/");
            GlideApp.with(context)
                    .load(thumbnailUrl)
                    .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                    .into(thumbnailView);
        }
    }

    @Click(R.id.news_row)
    public void onClick() {
        Intent intent = new Intent(context, ContentRSSActivity.class);
        intent.putExtra(TITLE_NEWS, blogNews.getTitle());
//        intent.putExtra(CONTENT_NEWS, mNews.getContent());
        context.startActivity(intent);
    }
}
