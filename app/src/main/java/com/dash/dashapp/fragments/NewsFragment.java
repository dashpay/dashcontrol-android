package com.dash.dashapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.activities.SettingsActivity;
import com.dash.dashapp.adapters.BlogNewsAdapter;
import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.DashBlogNews;
import com.dash.dashapp.models.BlogNews;
import com.dash.dashapp.realm.DashBlogNewsRealm;

import java.util.ArrayList;
import java.util.List;
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

public class NewsFragment extends Fragment {

    @BindView(R.id.news_list)
    RecyclerView infinitePlaceHolderView;

    @BindView(R.id.container)
    SwipeRefreshLayout swipeRefreshLayout;

    private MenuItem searchMenuItem;

    private Unbinder unbinder;

    private Call<List<DashBlogNews>> blogNewsCall;

    private BlogNewsAdapter blogNewsAdapter;
    private EndlessRecyclerViewScrollListener endlessScrollListener;

    private int currentPage = 0;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        infinitePlaceHolderView.setLayoutManager(layoutManager);
        infinitePlaceHolderView.setItemAnimator(new DefaultItemAnimator());
        infinitePlaceHolderView.setHasFixedSize(true);
        endlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!inSearchMode) {
                    loadNextPage();
                }
            }
        };
        infinitePlaceHolderView.addOnScrollListener(endlessScrollListener);

        blogNewsAdapter = new BlogNewsAdapter();
        infinitePlaceHolderView.setAdapter(blogNewsAdapter);

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
                List<DashBlogNews> blogNewsList = Objects.requireNonNull(response.body());
                display(new ArrayList<BlogNews.Convertible>(blogNewsList));
                persist(blogNewsList);
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(@NonNull Call<List<DashBlogNews>> call, @NonNull Throwable t) {
            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            displayFromCache();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private void displayFromCache() {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmQuery<DashBlogNewsRealm> whereQuery = realm.where(DashBlogNewsRealm.class);
            RealmResults<DashBlogNewsRealm> queryResult = whereQuery.findAll();
            List<BlogNews.Convertible> blogNewsList = new ArrayList<BlogNews.Convertible>(queryResult);
            display(blogNewsList);
        }
    }

    private void display(List<BlogNews.Convertible> blogNewsList) {
        if (currentPage == 1) {
            blogNewsAdapter.clear();
            endlessScrollListener.resetState();
        }
        List<BlogNews> list = new ArrayList<>();
        for (BlogNews.Convertible item : blogNewsList) {
            list.add(item.convert());
        }
        blogNewsAdapter.addAll(list);
    }

    private void persist(final List<DashBlogNews> blogNewsList) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    if (currentPage == 1) {
                        realm.delete(DashBlogNewsRealm.class);
                    }
                    List<DashBlogNewsRealm> list = new ArrayList<>();
                    for (DashBlogNews blogNews : blogNewsList) {
                        DashBlogNewsRealm blogNewsRealm = DashBlogNewsRealm.convert(blogNews);
                        list.add(blogNewsRealm);
                    }
                    realm.insert(list);
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
                ((Filterable) infinitePlaceHolderView.getAdapter()).getFilter().filter(newText);
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
