package com.dash.dashapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.dash.dashapp.R;
import com.dash.dashapp.models.BlogNews;

import java.util.ArrayList;
import java.util.List;

public class BlogNewsAdapter extends RecyclerView.Adapter<BlogNewsHolder> implements Filterable {

    private static final int VIEW_TYPE_LATEST = 0;
    private static final int VIEW_TYPE_REGULAR = 1;

    private List<BlogNews> blogNewsList;
    private List<BlogNews> referenceBlogNewsList;

    public BlogNewsAdapter() {
        this.blogNewsList = new ArrayList<>();
        this.referenceBlogNewsList = new ArrayList<>();
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
        BlogNews blogNews = blogNewsList.get(position);
        holder.bind(blogNews);
//        holder.setItemClickListener(new ItemClickListener() {
//            @Override
//            public void onItemClick(View v, int pos) {
//                Snackbar.make(v, players.get(pos).getName(), Snackbar.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return blogNewsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_LATEST;
        } else {
            return VIEW_TYPE_REGULAR;
        }
    }

    public void clear() {
        blogNewsList.clear();
        referenceBlogNewsList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<BlogNews> newBlogNewsList) {
        blogNewsList.addAll(newBlogNewsList);
        referenceBlogNewsList.addAll(newBlogNewsList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new BlogNewsFilter(referenceBlogNewsList) {
            @Override
            protected void publishResults(List<BlogNews> blogNewsFilteredList) {
                blogNewsList = blogNewsFilteredList;
                notifyDataSetChanged();
            }
        };
    }
}