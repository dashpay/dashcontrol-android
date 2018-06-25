package com.dash.dashapp.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dash.dashapp.R;
import com.dash.dashapp.models.BlogNews;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class BlogNewsAdapter extends RealmRecyclerViewAdapter<BlogNews, BlogNewsHolder> {

    private static final int VIEW_TYPE_LATEST = 0;
    private static final int VIEW_TYPE_REGULAR = 1;

    private boolean highlightFirstEntry;

    public BlogNewsAdapter(@Nullable OrderedRealmCollection<BlogNews> data, boolean highlightFirstEntry) {
        super(data, true);
        setHasStableIds(true);
        this.highlightFirstEntry = highlightFirstEntry;
    }

    @NonNull
    @Override
    public BlogNewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_LATEST: {
                view = inflater.inflate(R.layout.news_view_latest, parent, false);
                break;
            }
            case VIEW_TYPE_REGULAR:
            default: {
                view = inflater.inflate(R.layout.news_view, parent, false);
                break;
            }
        }
        return new BlogNewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogNewsHolder holder, int position) {
        BlogNews blogNews = getItem(position);
        holder.bind(blogNews);
    }

    @Override
    public int getItemViewType(int position) {
        if (highlightFirstEntry && position == 0) {
            return VIEW_TYPE_LATEST;
        } else {
            return VIEW_TYPE_REGULAR;
        }
    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }
}
