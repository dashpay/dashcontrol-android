package com.dash.dashapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dash.dashapp.Interface.ItemClickListener;
import com.dash.dashapp.Model.News;
import com.dash.dashapp.R;

import java.util.ArrayList;

public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = "MyNewsRecyclerView";
    private ArrayList<News> mNewsList;

    public MyNewsRecyclerViewAdapter(ArrayList<News> mNewsList) {
        this.mNewsList = mNewsList;
        for (News news : mNewsList){
            Log.d(TAG, news.getTitle());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTitleView.setText(mNewsList.get(position).getTitle());
        holder.mDateView.setText(mNewsList.get(position).getPubDate());

        /*SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm");
        String date = format.format(rssObject.getNewses().get(position).getPubDate());
        holder.mDateView.setText(date);*/
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        private ItemClickListener itemClickListener;
        public TextView mTitleView, mDateView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.title);
            mDateView = (TextView) itemView.findViewById(R.id.date);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }
}
