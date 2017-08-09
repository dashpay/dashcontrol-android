package com.dash.dashapp.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dash.dashapp.Adapters.MyNewsRecyclerViewAdapter;
import com.dash.dashapp.Adapters.NewsView;
import com.dash.dashapp.Interface.DatabaseUpdateListener;
import com.dash.dashapp.Model.News;
import com.dash.dashapp.R;
import com.dash.dashapp.Utils.HandleXML;
import com.dash.dashapp.Utils.LoadMoreView;
import com.dash.dashapp.Utils.MyDBHandler;
import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsFragment extends Fragment implements DatabaseUpdateListener {
    private static final String TAG = "NewsFragment";
    private InfinitePlaceHolderView mInfinitePlaceHolderView;
    private MyNewsRecyclerViewAdapter myNewsRecyclerViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressWheel;
    private HandleXML obj;
    private final String RSS_LINK = "https://www.dash.org/rss/dash_blog_rss.xml";
    private DatabaseUpdateListener dbListener;
    private WrapContentLinearLayoutManager mLayoutManager;
    private ArrayList<News> newsList;
    private int numberRows = 10;


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
        Log.d(TAG, "OnCreate");
        dbListener = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView");

        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        Context context = view.getContext();

        mProgressWheel = (ProgressBar) view.findViewById(R.id.progress_wheel);
        mInfinitePlaceHolderView = (InfinitePlaceHolderView) view.findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.container);

        mLayoutManager = new WrapContentLinearLayoutManager(context);

        mInfinitePlaceHolderView.setLayoutManager(mLayoutManager);
        mInfinitePlaceHolderView.setItemAnimator(new DefaultItemAnimator());
        mInfinitePlaceHolderView.setHasFixedSize(true);


        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.canChildScrollUp();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "OnActivityCreate");

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                obj = new HandleXML(RSS_LINK);
                obj.fetchXML(dbListener);
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

            turnWheelOn();
            loadRSS();
            turnWheelOff();
        }
    }


    public void loadRSS() {
        Log.d(TAG, "Load RSS");

        MyDBHandler dbHandler = new MyDBHandler(getContext(), null);
        newsList = dbHandler.findAllNews();

        /*myNewsRecyclerViewAdapter = new MyNewsRecyclerViewAdapter(newsList);
        mInfinitePlaceHolderView.setAdapter(myNewsRecyclerViewAdapter);
        mInfinitePlaceHolderView.addOnScrollListener(mBottomListener);*/

        for(int i = 0; i < LoadMoreView.LOAD_VIEW_SET_COUNT; i++){
            mInfinitePlaceHolderView.addView(new NewsView(getContext(), newsList.get(i)));
        }
        mInfinitePlaceHolderView.setLoadMoreResolver(new LoadMoreView(mInfinitePlaceHolderView, newsList));
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

    @Override
    public void onUpdateStarted() {
        Log.d(TAG, "Update completed");
        turnWheelOn();
    }

    @Override
    public void onUpdateCompleted() {
        Log.d(TAG, "Update completed");
        mInfinitePlaceHolderView.refresh();
        loadRSS();
        turnWheelOff();
    }

    private void turnWheelOn() {
        numberRows = 10;
        Log.d(TAG, "Turn wheel on");
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mProgressWheel.setVisibility(View.VISIBLE);
        }
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void turnWheelOff() {
        Log.d(TAG, "Turn wheel off");
        if (mSwipeRefreshLayout.isRefreshing()) {
            mProgressWheel.setVisibility(View.GONE);
        }
        mSwipeRefreshLayout.setRefreshing(false);

        Log.d(TAG, "Turn wheel off : mSwipeRefreshLayout.isRefreshing() " + mSwipeRefreshLayout.isRefreshing());
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

}
