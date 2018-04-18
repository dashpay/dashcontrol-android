package com.dash.dashapp.utils;

import com.dash.dashapp.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.infinite.LoadMore;

@Layout(R.layout.load_more_view)
public class LoadMoreView {

    private Callback callback;

    public LoadMoreView(Callback callback) {
        this.callback = callback;
    }

    @LoadMore
    public void onLoadMore() {
        callback.onShowMore();
    }

    public interface Callback {
        void onShowMore();
    }
}