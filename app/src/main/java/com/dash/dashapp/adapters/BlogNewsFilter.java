package com.dash.dashapp.adapters;

import android.text.TextUtils;
import android.widget.Filter;

import com.dash.dashapp.models.BlogNews;

import java.util.ArrayList;
import java.util.List;

public abstract class BlogNewsFilter extends Filter {

    private List<BlogNews> referenceBlogNewsList;

    BlogNewsFilter(List<BlogNews> referenceBlogNewsList) {
        this.referenceBlogNewsList = referenceBlogNewsList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (!TextUtils.isEmpty(constraint)) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<BlogNews> filteredBlogNews = new ArrayList<>();
            for (BlogNews item : referenceBlogNewsList) {
                if (item.title.toUpperCase().contains(constraint)) {
                    filteredBlogNews.add(item);
                }
            }
            results.count = filteredBlogNews.size();
            results.values = filteredBlogNews;
        } else {
            results.count = referenceBlogNewsList.size();
            results.values = referenceBlogNewsList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
        //noinspection unchecked
        publishResults((List<BlogNews>) results.values);
    }

    protected abstract void publishResults(List<BlogNews> blogNewsFilteredList);
}