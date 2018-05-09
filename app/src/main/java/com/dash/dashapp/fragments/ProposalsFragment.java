package com.dash.dashapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.adapters.ProposalAdapter;
import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.BudgetApiBudgetAnswer;
import com.dash.dashapp.api.data.DashBudget;
import com.dash.dashapp.api.data.DashProposal;
import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.models.BudgetSummary;
import com.dash.dashapp.view.ExpandableFiltersView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProposalsFragment extends BaseFragment {

    @BindView(R.id.proposals_list)
    RecyclerView proposalRecyclerView;

    @BindView(R.id.container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.filters)
    ExpandableFiltersView filtersView;

    @BindView(R.id.total_budget)
    TextView totalBudgetView;

    @BindView(R.id.allocated_budget)
    TextView allocatedBudgetView;

    @BindView(R.id.superblocks_summary)
    TextView superblocksSummaryView;

    private MenuItem searchMenuItem;

    private Unbinder unbinder;

    private Call<BudgetApiBudgetAnswer> budgetProposalsCall;

    private ProposalAdapter proposalAdapter;

    private EndlessRecyclerViewScrollListener endlessScrollListener;

    private int currentPage = 0;

    private boolean inSearchMode = false;

    public ProposalsFragment() {
    }

    @SuppressWarnings("unused")
    public static ProposalsFragment newInstance() {
        ProposalsFragment fragment = new ProposalsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_proposals_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        proposalRecyclerView.setLayoutManager(layoutManager);
        proposalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        proposalRecyclerView.setHasFixedSize(true);
        endlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                if (!inSearchMode) {
//                    loadNextPage();
//                }
            }
        };
        proposalRecyclerView.addOnScrollListener(endlessScrollListener);

        proposalAdapter = new ProposalAdapter();
        proposalRecyclerView.setAdapter(proposalAdapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.canChildScrollUp();
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        filtersView.setOnFilterChangeListener(new ExpandableFiltersView.OnFilterChangeListener() {
            @Override
            public void onFilterChange(ExpandableFiltersView filtersView, ExpandableFiltersView.Filter filter) {
                ProposalsFragment.this.onFilterChange(filter);
            }
        });

        return view;
    }

    private void onFilterChange(ExpandableFiltersView.Filter filter) {
        ProposalAdapter proposalAdapter = (ProposalAdapter) proposalRecyclerView.getAdapter();
        switch (filter) {
            case CURRENT: {
                proposalAdapter.setPropertyFilters(false, false);
                break;
            }
            case ONGOING: {
                proposalAdapter.setPropertyFilters(true, false);
                break;
            }
            case PAST: {
                proposalAdapter.setPropertyFilters(false, true);
                break;
            }
        }
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
        budgetProposalsCall = DashControlClient.getInstance().getDashProposals(++currentPage);
        budgetProposalsCall.enqueue(callback);
    }

    Callback<BudgetApiBudgetAnswer> callback = new Callback<BudgetApiBudgetAnswer>() {
        @Override
        public void onResponse(@NonNull Call<BudgetApiBudgetAnswer> call, Response<BudgetApiBudgetAnswer> response) {
            if (response.isSuccessful()) {
                BudgetApiBudgetAnswer budgetApiAnswer = Objects.requireNonNull(response.body());
                display(budgetApiAnswer.dashBudget);
                List<DashProposal> proposalList = Objects.requireNonNull(budgetApiAnswer.proposals);
                display(new ArrayList<BudgetProposal.Convertible>(proposalList));
                persist(proposalList, budgetApiAnswer.dashBudget);
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(@NonNull Call<BudgetApiBudgetAnswer> call, @NonNull Throwable t) {
            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            displayFromCache();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private void displayFromCache() {
        try (Realm realm = Realm.getDefaultInstance()) {

            RealmQuery<DashBudget> budgetWhereQuery = realm.where(DashBudget.class);
            DashBudget budgetQueryResult = budgetWhereQuery.findFirst();
            if (budgetQueryResult != null) {
                display(budgetQueryResult);
            }

            RealmQuery<DashProposal> proposalsWhereQuery = realm.where(DashProposal.class);
            RealmResults<DashProposal> proposalsQueryResult = proposalsWhereQuery.findAll();
            List<BudgetProposal.Convertible> proposalList = new ArrayList<BudgetProposal.Convertible>(proposalsQueryResult);
            display(proposalList);
        }
    }

    private void display(BudgetSummary.Convertible budgetSummary) {
        BudgetSummary summary = budgetSummary.convert();
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(1);
        formatter.setMaximumFractionDigits(1);
        totalBudgetView.setText(formatter.format(summary.totalAmount));
        allocatedBudgetView.setText(formatter.format(summary.allotedAmount));

        SimpleDateFormat dateFormatter = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        String superblockSummary = getString(R.string.superblocks_summary, summary.superblock, summary.paymentDateHuman, dateFormatter.format(summary.paymentDate));
        superblocksSummaryView.setText(superblockSummary);
    }

    private void display(List<BudgetProposal.Convertible> proposalList) {
        if (currentPage == 1) {
            proposalAdapter.clear();
            endlessScrollListener.resetState();
        }
        List<BudgetProposal> list = new ArrayList<>();
        for (BudgetProposal.Convertible item : proposalList) {
            list.add(item.convert());
        }
        proposalAdapter.addAll(list);
    }

    private void persist(final List<DashProposal> proposalList, final DashBudget dashBudget) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    if (currentPage == 1) {
                        realm.delete(DashProposal.class);
                    }
                    realm.insert(proposalList);

                    realm.delete(DashBudget.class);
                    realm.insert(dashBudget);
                }
            });
        }
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (searchMenuItem != null) {
                searchMenuItem.collapseActionView();
            }
            loadFirstPage();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        setupSearchView();
    }

    private void setupSearchView() {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        ActionBar supportActionBar = Objects.requireNonNull(activity).getSupportActionBar();
        Context themedContext = Objects.requireNonNull(supportActionBar).getThemedContext();
        SearchView searchView = new SearchView(themedContext);
        searchMenuItem.setShowAsAction(searchMenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | searchMenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchMenuItem.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((Filterable) proposalRecyclerView.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewDetachedFromWindow(View arg0) {
                inSearchMode = false;
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                inSearchMode = true;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
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
        if (budgetProposalsCall != null) {
            budgetProposalsCall.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        budgetProposalsCall = null;
        unbinder.unbind();
    }
}
