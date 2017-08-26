package com.dash.dashapp.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dash.dashapp.Activities.MainActivity;
import com.dash.dashapp.Activities.SettingsActivity;
import com.dash.dashapp.Adapters.NewsView;
import com.dash.dashapp.Interface.RSSUpdateListener;
import com.dash.dashapp.Model.News;
import com.dash.dashapp.R;
import com.dash.dashapp.Utils.XmlUtil;
import com.dash.dashapp.Utils.LoadMoreNews;
import com.dash.dashapp.Utils.MyDBHandler;
import com.dash.dashapp.Utils.SharedPreferencesManager;
import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.util.ArrayList;

public class NewsFragment extends BaseFragment implements RSSUpdateListener {
    private static final String TAG = "NewsFragment";
    private static final int NUMBER_FIRST_BATCH = 10;
    private InfinitePlaceHolderView mInfinitePlaceHolderView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressWheel;
    private XmlUtil obj;
    private RSSUpdateListener dbListener;
    private WrapContentLinearLayoutManager mLayoutManager;
    private ArrayList<News> newsList;
    private boolean updatePerforming = false;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        dbListener = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        Context context = view.getContext();

        mProgressWheel = (ProgressBar) view.findViewById(R.id.progress_wheel);
        mInfinitePlaceHolderView = (InfinitePlaceHolderView) view.findViewById(R.id.news_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.container);

        mLayoutManager = new WrapContentLinearLayoutManager(context);

        mInfinitePlaceHolderView.setLayoutManager(mLayoutManager);
        mInfinitePlaceHolderView.setItemAnimator(new DefaultItemAnimator());
        mInfinitePlaceHolderView.setHasFixedSize(true);


        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.canChildScrollUp();

        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!updatePerforming){
                    obj = new XmlUtil(mContext);
                    obj.fetchRSSXML(dbListener);
                }
            }
        });


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
                                    //getActivity().finish();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

        } else if (isNetworkAvailable()) {
            handleRSS();
        }
    }


    public void handleRSS() {

        MyDBHandler dbHandler = new MyDBHandler(mContext, null);
        newsList = dbHandler.findAllNews(null);

        if (newsList.size() == 0) {
            updateRSS();
        } else {
            loadRSS();
        }
    }

    public void loadRSS() {
        turnWheelOn();
        for (int i = 0; i < LoadMoreNews.LOAD_VIEW_SET_COUNT; i++) {
            try{
                mInfinitePlaceHolderView.addView(new NewsView(getContext(), newsList.get(i)));
                Log.d(TAG, "Add view index + " + i);
            }catch (Exception e){
                e.getMessage();
            }
        }

        if (newsList.size() > NUMBER_FIRST_BATCH){
            mInfinitePlaceHolderView.setLoadMoreResolver(new LoadMoreNews(mInfinitePlaceHolderView, newsList));
        }
        turnWheelOff();
    }

    public void updateRSS() {
        obj = new XmlUtil(SharedPreferencesManager.getLanguageRSS(getContext());
        obj.fetchRSSXML(dbListener);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        //... constructor
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Text submit");
                mInfinitePlaceHolderView.removeAllViews();

                MyDBHandler dbHandler = new MyDBHandler(mContext, null);
                newsList = dbHandler.findAllNews(query);
                loadRSS();
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
    public void onUpdateStarted() {
        updatePerforming = true;
        turnWheelOn();
    }

    @Override
    public void onFirstBatchNewsCompleted(ArrayList<News> newsList) {
        Log.d(TAG, "First batch completed");
        this.newsList = newsList;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInfinitePlaceHolderView.removeAllViews();
                loadRSS();
                turnWheelOff();
            }
        });
    }

    @Override
    public void onDatabaseUpdateCompleted() {
        Log.d(TAG, "Database completed");
        updatePerforming = false;
        MyDBHandler dbHandler = new MyDBHandler(mContext, null);
        newsList = dbHandler.findAllNews(null);

        if (mInfinitePlaceHolderView.getChildCount() == 0){
            loadRSS();
            turnWheelOff();
        }
    }

    private void turnWheelOn() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mProgressWheel.setVisibility(View.VISIBLE);
        }
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void turnWheelOff() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mProgressWheel.setVisibility(View.GONE);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStop() {
        turnWheelOff();
        if (mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
        super.onStop();
    }
}
