package com.dash.dashapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.dash.dashapp.R;
import com.dash.dashapp.activities.SimpleWebViewActivity;
import com.dash.dashapp.models.BlogNews;
import com.dash.dashapp.utils.GlideApp;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class BlogNewsHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    public TextView titleView;

    @BindView(R.id.date)
    public TextView dateView;

    @BindView(R.id.thumbnail)
    public ImageView thumbnailView;

    private Context context;

    private BlogNews blogNews;

    BlogNewsHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public void bind(BlogNews blogNews) {
        this.blogNews = blogNews;

        String titleHtml = blogNews.getTitle();
        titleView.setText(Html.fromHtml(titleHtml));

        SimpleDateFormat newsDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        dateView.setText(newsDateFormat.format(blogNews.getDate()));

        if (blogNews.getImage() != null) {
            GlideApp.with(context)
                    .load(blogNews.getImageUrl())
                    .error(R.drawable.ic_broken_image_24dp)
                    .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                    .into(thumbnailView);
        }
    }

    @OnClick
    public void onClick() {
        if (!TextUtils.isEmpty(blogNews.getUrl())) {
            Intent intent = SimpleWebViewActivity.createIntent(context, blogNews.getTitle(), blogNews.getBlogPostUrl());
            context.startActivity(intent);
        }
    }

    @OnLongClick
    public boolean onLongClick() {
        if (!TextUtils.isEmpty(blogNews.getUrl())) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(blogNews.getBlogPostUrl()));
            context.startActivity(browserIntent);
            return true;
        }
        return false;
    }
}