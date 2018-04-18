package com.dash.dashapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.dash.dashapp.R;
import com.dash.dashapp.activities.ContentRSSActivity;
import com.dash.dashapp.models.BlogNews;
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

    @View(R.id.cached)
    public ImageView cachedView;

    private BlogNews blogNews;
    private Context context;

    public BlogNewsView(Context context, BlogNews blogNews) {
        this.context = context;
        this.blogNews = blogNews;
    }

    @Resolve
    public void onResolved() {
        String titleHtml = blogNews.title;
        titleView.setText(Html.fromHtml(titleHtml));

        SimpleDateFormat newsDateFormat = new SimpleDateFormat("MMM dd, YYYY", Locale.US);
        dateView.setText(newsDateFormat.format(blogNews.date));

        if (blogNews.image != null) {
            String thumbnailUrl = (URLs.DASH_CONTROL_BASE_API + blogNews.image).replace("//", "/");
            GlideApp.with(context)
                    .load(thumbnailUrl)
                    .error(R.drawable.ic_broken_image_24dp)
                    .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                    .into(thumbnailView);
        }

        cachedView.setVisibility(blogNews.cached ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    @Click(R.id.news_row)
    public void onClick() {
        Intent intent = new Intent(context, ContentRSSActivity.class);
        intent.putExtra(TITLE_NEWS, blogNews.title);
//        intent.putExtra(CONTENT_NEWS, mNews.getContent());
        context.startActivity(intent);
    }
}
