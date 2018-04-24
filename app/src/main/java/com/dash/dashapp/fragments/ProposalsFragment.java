package com.dash.dashapp.fragments;

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

import com.dash.dashapp.activities.MainActivity;
import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.adapters.ProposalView;
import com.dash.dashapp.interfaces.ProposalUpdateListener;
import com.dash.dashapp.models.Proposal;
import com.dash.dashapp.R;
import com.dash.dashapp.utils.JsonUtil;
import com.dash.dashapp.utils.LoadMoreProposals;
import com.dash.dashapp.utils.MyDBHandler;
import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.util.List;

public class ProposalsFragment extends BaseFragment implements ProposalUpdateListener {
    private static final String TAG = "ProposalsFragment";
    private static final int NUMBER_FIRST_BATCH = 10;
    private InfinitePlaceHolderView mInfinitePlaceHolderView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressWheel;
    private JsonUtil obj;
    private ProposalUpdateListener dbListener;
    private ProposalsFragment.WrapContentLinearLayoutManager mLayoutManager;
    private List<Proposal> proposalsList;
    private boolean updatePerforming = false;
    public final static String URL_PROPOSAL = "https://www.dashcentral.org/api/v1/budget";



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProposalsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProposalsFragment newInstance() {
        ProposalsFragment fragment = new ProposalsFragment();
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

        View view = inflater.inflate(R.layout.fragment_proposals_list, container, false);
        Context context = view.getContext();

//        mProgressWheel = (ProgressBar) view.findViewById(R.id.progress_wheel);
        mInfinitePlaceHolderView = (InfinitePlaceHolderView) view.findViewById(R.id.proposals_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.container);

        mLayoutManager = new ProposalsFragment.WrapContentLinearLayoutManager(context);

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
                    updateProposals();
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
            handleJson();
        }
    }


    public void handleJson() {

        MyDBHandler dbHandler = new MyDBHandler(mContext, null);
        proposalsList = dbHandler.findAllProposals(null);

        if (proposalsList.size() == 0) {
            updateProposals();
        } else {
            loadJson();
        }
    }

    public void loadJson() {
        turnWheelOn();
        for (int i = 0; i < LoadMoreProposals.LOAD_VIEW_SET_COUNT; i++) {
            try{
                mInfinitePlaceHolderView.addView(new ProposalView(getContext(), proposalsList.get(i)));
                Log.d(TAG, "Add view index + " + i);
            }catch (Exception e){
                e.getMessage();
            }
        }

        if (proposalsList.size() > NUMBER_FIRST_BATCH){
            mInfinitePlaceHolderView.setLoadMoreResolver(new LoadMoreProposals(mInfinitePlaceHolderView, proposalsList));
        }
        turnWheelOff();
    }

    public void updateProposals() {
        MyDBHandler dbHandler = new MyDBHandler(mContext, null);
        dbHandler.deleteAllProposals();
        dbHandler.deleteAllComments();
        obj = new JsonUtil(getContext());
        obj.fetchProposalJson(dbListener);
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
                e.getMessage();
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
                proposalsList = dbHandler.findAllProposals(query);
                loadJson();
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
    public void onUpdateStarted() {
        updatePerforming = true;
        turnWheelOn();
    }

    @Override
    public void onFirstBatchProposalsCompleted(List<Proposal> ProposalsList) {
        Log.d(TAG, "First batch completed");
        this.proposalsList = ProposalsList;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInfinitePlaceHolderView.removeAllViews();
                loadJson();
                turnWheelOff();
            }
        });

    }

    @Override
    public void onDatabaseUpdateCompleted() {
        Log.d(TAG, "Database completed");
        updatePerforming = false;
        MyDBHandler dbHandler = new MyDBHandler(mContext, null);
        proposalsList = dbHandler.findAllProposals(null);

        if (mInfinitePlaceHolderView.getChildCount() == 0){
            loadJson();
            turnWheelOff();
        }
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
