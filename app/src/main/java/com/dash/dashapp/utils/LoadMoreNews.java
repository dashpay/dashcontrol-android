package com.dash.dashapp.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dash.dashapp.adapters.NewsView;
import com.dash.dashapp.models.News;
import com.dash.dashapp.R;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.infinite.LoadMore;

import java.util.List;

/**
 * Created by sebas on 8/8/2017.
 */

@Layout(R.layout.load_more_view)
public class LoadMoreNews {

    public static final int LOAD_VIEW_SET_COUNT = 10;
    private static final String TAG = "LoadMoreNews";

    private InfinitePlaceHolderView mLoadMoreView;
    private List<News> mNewsList;

    public LoadMoreNews(InfinitePlaceHolderView loadMoreView, List<News> feedList) {
        this.mLoadMoreView = loadMoreView;
        this.mNewsList = feedList;
    }

    @LoadMore
    private void onLoadMore() {
        Log.d("DEBUG", "onLoadMore");
        new ForcedWaitedLoading();
    }

    class ForcedWaitedLoading implements Runnable {

        public ForcedWaitedLoading() {
            new Thread(this).start();
        }

        @Override
        public void run() {

            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    int count = mLoadMoreView.getViewCount();
                    Log.d("DEBUG", "count " + count);
                    for (int i = count - 1;
                         i < (count - 1 + LoadMoreNews.LOAD_VIEW_SET_COUNT) && mNewsList.size() > i;
                         i++) {
                        mLoadMoreView.addView(new NewsView(mLoadMoreView.getContext(), mNewsList.get(i + 1)));

                        Log.d(TAG, "Starting with index : " + i);

                        if (i == mNewsList.size() - 1) {
                            mLoadMoreView.noMoreToLoad();
                            break;
                        }
                    }
                    mLoadMoreView.loadingDone();
                }
            });
        }
    }
}