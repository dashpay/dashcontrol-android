package com.dash.dashapp.Utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dash.dashapp.Adapters.NewsView;
import com.dash.dashapp.Adapters.ProposalView;
import com.dash.dashapp.Model.News;
import com.dash.dashapp.Model.Proposal;
import com.dash.dashapp.R;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.infinite.LoadMore;

import java.util.List;

/**
 * Created by sebas on 8/8/2017.
 */

@Layout(R.layout.load_more_view)
public class LoadMoreProposals {

    public static final int LOAD_VIEW_SET_COUNT = 10;
    private static final String TAG = "LoadMoreProposals";

    private InfinitePlaceHolderView mLoadMoreView;
    private List<Proposal> mProposalList;

    public LoadMoreProposals(InfinitePlaceHolderView loadMoreView, List<Proposal> proposalList) {
        this.mLoadMoreView = loadMoreView;
        this.mProposalList = proposalList;
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
                         i < (count - 1 + LoadMoreProposals.LOAD_VIEW_SET_COUNT) && mProposalList.size() > i;
                         i++) {

                        /*if (mNewsList.get(i).getGuid().equals(mNewsList.get(i-1).getGuid())){
                            i++;
                        }*/
                        mLoadMoreView.addView(new ProposalView(mLoadMoreView.getContext(), mProposalList.get(i)));

                        Log.d(TAG, "Starting with index : " + i);

                        if (i == mProposalList.size() - 1) {
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