package com.dash.dashapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.dash.dashapp.R;
import com.dash.dashapp.interfaces.ItemClickListener;
import com.dash.dashapp.models.BlogNews;
import com.dash.dashapp.utils.GlideApp;
import com.dash.dashapp.utils.URLs;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlogNewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.title)
    public TextView titleView;

    @BindView(R.id.date)
    public TextView dateView;

    @BindView(R.id.thumbnail)
    public ImageView thumbnailView;

    @BindView(R.id.cached)
    public ImageView cachedView;

    private Context context;

    private ItemClickListener itemClickListener;

    public BlogNewsHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void bind(BlogNews blogNews) {
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

    @Override
    public void onClick(View v) {
//        this.itemClickListener.onItemClick(v, getLayoutPosition());
//        Intent intent = new Intent(context, ContentRSSActivity.class);
//        intent.putExtra(TITLE_NEWS, blogNews.title);
//        intent.putExtra(CONTENT_NEWS, mNews.getContent());
//        context.startActivity(intent);
    }

    public void setItemClickListener(ItemClickListener ic) {
        this.itemClickListener = ic;
    }
}