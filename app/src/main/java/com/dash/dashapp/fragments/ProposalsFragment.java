package com.dash.dashapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.activities.ProposalDetailActivity;
import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.adapters.ProposalAdapter;
import com.dash.dashapp.api.data.BudgetApiBudgetAnswer;
import com.dash.dashapp.events.BudgetSyncCompleteEvent;
import com.dash.dashapp.events.BudgetSyncFailedEvent;
import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.models.BudgetSummary;
import com.dash.dashapp.service.BudgetSyncService;
import com.dash.dashapp.view.ExpandableFiltersView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;

public class ProposalsFragment extends BaseFragment {

    @BindView(R.id.proposals_list)
    RecyclerView proposalRecyclerView;

    @BindView(R.id.container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.filters)
    ExpandableFiltersView filtersView;

    @BindView(R.id.summary)
    View summaryView;

    @BindView(R.id.total_budget)
    TextView totalBudgetView;

    @BindView(R.id.allocated_budget)
    TextView allocatedBudgetView;

    @BindView(R.id.superblocks_summary)
    TextView superblocksSummaryView;

    private MenuItem searchMenuItem;

    private Unbinder unbinder;

    private Realm realm;

    private Call<BudgetApiBudgetAnswer> budgetProposalsCall;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_proposals_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

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
        displayProposals(filter, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupNewsRecycler();
    }

    private void setupNewsRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        proposalRecyclerView.setLayoutManager(layoutManager);
        proposalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        proposalRecyclerView.setHasFixedSize(true);

        displayProposals(filtersView.getSelectedFilter(), null);
    }

    private void displayProposals(ExpandableFiltersView.Filter filter, String searchTerm) {
        RealmQuery<BudgetProposal> proposalsQuery = realm.where(BudgetProposal.class);

        Date today = new Date();
        switch (filter) {
            case CURRENT: {
                proposalsQuery = proposalsQuery.equalTo(BudgetProposal.Field.HISTORICAL, false);
                break;
            }
            case ONGOING: {
                proposalsQuery = proposalsQuery
                        .equalTo(BudgetProposal.Field.HISTORICAL, false)
                        .greaterThan(BudgetProposal.Field.DATE_END, today)
                        .greaterThan(BudgetProposal.Field.REMAINING_PAYMENT_COUNT, 0)
                        .equalTo(BudgetProposal.Field.WILL_BE_FUNDED, true)
                        .equalTo(BudgetProposal.Field.IN_NEXT_BUDGET, true);
                break;
            }
            case PAST: {
                proposalsQuery = proposalsQuery.equalTo(BudgetProposal.Field.HISTORICAL, true);
                break;
            }
        }
        proposalsQuery = proposalsQuery.sort(BudgetProposal.Field.DATE_ADDED, Sort.DESCENDING);

        boolean searchMode = (searchTerm != null);
        if (searchMode) {
            proposalsQuery = proposalsQuery.contains(BudgetProposal.Field.TITLE, searchTerm, Case.INSENSITIVE);
        }

        RealmResults<BudgetProposal> proposalsResult = proposalsQuery.findAll();

        ProposalAdapter proposalsRealmAdapter = new ProposalAdapter(proposalsResult, onItemClickListener);
        proposalRecyclerView.setAdapter(proposalsRealmAdapter);

        displayBudgetSummary();
    }

    private void displayBudgetSummary() {
        BudgetSummary summary = realm.where(BudgetSummary.class).findFirst();
        if (summary != null) {
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
            totalBudgetView.setText(formatter.format(summary.getTotalAmount()));
            allocatedBudgetView.setText(formatter.format(summary.getAllotedAmount()));

            SimpleDateFormat dateFormatter = new SimpleDateFormat("d MMMM yyyy", Locale.US);
            String superblockSummary = getString(R.string.superblocks_summary, summary.getSuperblock(), summary.getPaymentDateHuman(), dateFormatter.format(summary.getPaymentDate()));
            superblocksSummaryView.setText(superblockSummary);
        }
    }

    ProposalAdapter.OnItemClickListener onItemClickListener = new ProposalAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BudgetProposal proposal) {
            BudgetProposal unmanagedProposal = realm.copyFromRealm(proposal);
            Context context = Objects.requireNonNull(getContext());
            Intent intent = ProposalDetailActivity.createIntent(context, unmanagedProposal);
            context.startActivity(intent);
        }
    };

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (searchMenuItem != null) {
                searchMenuItem.collapseActionView();
            }
            FragmentActivity activity = getActivity();
            if (activity != null) {
                Intent intent = new Intent(activity, BudgetSyncService.class);
                activity.startService(intent);
                swipeRefreshLayout.setRefreshing(true);
            }
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

            private boolean searching;

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                if (searching || newQuery.length() > 2) {
                    searching = true;
                    displayProposals(filtersView.getSelectedFilter(), newQuery);
                }
                if (newQuery.length() == 0) {
                    searching = false;
                }
                return false;
            }
        });
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewDetachedFromWindow(View arg0) {
                filtersView.setVisibility(View.VISIBLE);
                summaryView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                summaryView.setVisibility(View.GONE);
                filtersView.setVisibility(View.GONE);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBudgetSyncCompleteEvent(BudgetSyncCompleteEvent event) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        displayBudgetSummary();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBudgetSyncFailedEvent(BudgetSyncFailedEvent event) {
        Throwable cause = event.getCause();
        if (cause != null) {
            Toast.makeText(getContext(), cause.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.destroyDrawingCache();
            swipeRefreshLayout.clearAnimation();
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelRequest();
        realm.close();
        unbinder.unbind();
    }

    private void cancelRequest() {
        if (budgetProposalsCall != null) {
            budgetProposalsCall.cancel();
            budgetProposalsCall = null;
        }
    }
}
