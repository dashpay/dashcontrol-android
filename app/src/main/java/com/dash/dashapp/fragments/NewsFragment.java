package com.dash.dashapp.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.adapters.BlogNewsAdapter;
import com.dash.dashapp.events.NewsSyncCompleteEvent;
import com.dash.dashapp.events.NewsSyncFailedEvent;
import com.dash.dashapp.models.BlogNews;
import com.dash.dashapp.service.NewsSyncService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.content.Context.ACTIVITY_SERVICE;

public class NewsFragment extends Fragment {

    @BindView(R.id.news_list)
    RecyclerView newsRecyclerView;

    @BindView(R.id.container)
    SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayoutManager layoutManager;

    private MenuItem searchMenuItem;

    private Unbinder unbinder;

    private Realm realm;

    private boolean inSearchMode = false;

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

        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.canChildScrollUp();
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        if (isNewsSyncServiceRunning()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupNewsRecycler();
    }

    private void setupNewsRecycler() {
        layoutManager = new LinearLayoutManager(getActivity());
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        newsRecyclerView.setHasFixedSize(true);

        displayNews(null);
    }

    private void displayNews(String searchTerm) {

        RealmQuery<BlogNews> newsQuery = realm.where(BlogNews.class)
                .sort(BlogNews.Field.DATE, Sort.DESCENDING);

        boolean searchMode = (searchTerm != null);
        if (searchMode) {
            newsQuery = newsQuery.like(BlogNews.Field.TITLE, "*" + searchTerm + "*");
        }

        RealmResults<BlogNews> blogNewsResult = newsQuery.findAll();

        BlogNewsAdapter newsRealmAdapter = new BlogNewsAdapter(blogNewsResult, !searchMode);
        newsRecyclerView.setAdapter(newsRealmAdapter);
        blogNewsResult.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<BlogNews>>() {
            @Override
            public void onChange(@NonNull RealmResults<BlogNews> blogNews, @NonNull OrderedCollectionChangeSet changeSet) {
                int lastCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                // we don't want to scroll top automatically if user manually scrolled down
                if (lastCompletelyVisibleItemPosition < 3) {
                    newsRecyclerView.scrollToPosition(0);
                }
            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (searchMenuItem != null) {
                searchMenuItem.collapseActionView();
            }
            FragmentActivity activity = getActivity();
            if (activity != null) {
                Intent intent = new Intent(activity, NewsSyncService.class);
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
                    displayNews(newQuery);
                }
                if (newQuery.length() == 0) {
                    searching = false;
                }
                return false;
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

    private boolean isNewsSyncServiceRunning() {
        Context context = Objects.requireNonNull(getContext());
        ActivityManager manager = Objects.requireNonNull((ActivityManager) context.getSystemService(ACTIVITY_SERVICE));
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NewsSyncService.class.getCanonicalName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewsSyncCompleteEvent(NewsSyncCompleteEvent event) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewsSyncFailedEvent(NewsSyncFailedEvent event) {
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
        if (searchMenuItem != null) {
            searchMenuItem.collapseActionView();
        }
        newsRecyclerView.setAdapter(null);
        realm.close();
        unbinder.unbind();
    }
}
