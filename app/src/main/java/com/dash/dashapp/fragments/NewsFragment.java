package com.dash.dashapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.adapters.BlogNewsView;
import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.DashBlogNews;
import com.dash.dashapp.utils.LoadMoreView;
import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    private static final int BLOG_NEWS_PAGE_SIZE = 50;

    @BindView(R.id.news_list)
    InfinitePlaceHolderView infinitePlaceHolderView;

    @BindView(R.id.container)
    SwipeRefreshLayout swipeRefreshLayout;

    private Unbinder unbinder;

    private Call<List<DashBlogNews>> blogNewsCall;

    private int currentPage = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsFragment() {
    }

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        infinitePlaceHolderView.setLayoutManager(new LinearLayoutManager(getActivity()));
        infinitePlaceHolderView.setItemAnimator(new DefaultItemAnimator());
        infinitePlaceHolderView.setHasFixedSize(true);
        infinitePlaceHolderView.setLoadMoreResolver(new LoadMoreView(loadMoreCallback));

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.canChildScrollUp();
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFirstPage();
    }

    private void loadFirstPage() {
        currentPage = 0;
        swipeRefreshLayout.setRefreshing(true);
        loadNextPage();
    }

    private void loadNextPage() {
        blogNewsCall = DashControlClient.getInstance().getBlogNews(++currentPage);
        blogNewsCall.enqueue(callback);
    }

    Callback<List<DashBlogNews>> callback = new Callback<List<DashBlogNews>>() {
        @Override
        public void onResponse(@NonNull Call<List<DashBlogNews>> call, @NonNull Response<List<DashBlogNews>> response) {
            if (response.isSuccessful()) {
                if (currentPage == 1) {
                    infinitePlaceHolderView.removeAllViews();
                }
                List<DashBlogNews> blogNewsList = Objects.requireNonNull(response.body());
                for (DashBlogNews item : blogNewsList) {
                    BlogNewsView blogNewsView = new BlogNewsView(getContext(), item);
                    infinitePlaceHolderView.addView(blogNewsView);
                }
                infinitePlaceHolderView.loadingDone();
                if (blogNewsList.size() < BLOG_NEWS_PAGE_SIZE) {
                    infinitePlaceHolderView.noMoreToLoad();
                }
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(@NonNull Call<List<DashBlogNews>> call, @NonNull Throwable t) {
            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    LoadMoreView.Callback loadMoreCallback = new LoadMoreView.Callback() {
        @Override
        public void onShowMore() {
            loadNextPage();
        }
    };

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadFirstPage();
        }
    };


    public void handleRSS() {
/*
        MyDBHandler dbHandler = new MyDBHandler(mContext, null);
        mNewsList = dbHandler.findAllNews(null);

        if (!isNetworkAvailable()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.alert_message)
                    .setTitle(R.string.alert_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Log.i(TAG, "onClick: " + mNewsList.size());
                                    if (mNewsList.size() > 0) {
                                        if (mInfinitePlaceHolderView.getVisibility() != View.VISIBLE)
                                            mInfinitePlaceHolderView.setVisibility(View.VISIBLE);
                                        mSwipeRefreshLayout.setBackground(getResources().getDrawable(R.drawable.splash_bg));
                                        mSwipeRefreshLayout.setBackgroundResource(0);
                                        loadRSS();
                                    } else {
                                        if (mInfinitePlaceHolderView.getVisibility() != View.GONE)
                                            mInfinitePlaceHolderView.setVisibility(View.GONE);
                                        mSwipeRefreshLayout.setBackground(getResources().getDrawable(R.drawable.splash_bg));
                                    }
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

        }
*/
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        setupSearchView(item);
    }

    private void setupSearchView(MenuItem item) {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        ActionBar supportActionBar = Objects.requireNonNull(activity).getSupportActionBar();
        Context themedContext = Objects.requireNonNull(supportActionBar).getThemedContext();
        SearchView sv = new SearchView(themedContext);
        item.setShowAsAction(item.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | item.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Text submit");
                infinitePlaceHolderView.removeAllViews();

//                MyDBHandler dbHandler = new MyDBHandler(mContext, null);
//                mNewsList = dbHandler.findAllNews(query);
//                loadRSS();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "Text change");
                return false;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onStop() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.destroyDrawingCache();
            swipeRefreshLayout.clearAnimation();
        }
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (blogNewsCall != null) {
            blogNewsCall.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        blogNewsCall = null;
        unbinder.unbind();
    }
}
