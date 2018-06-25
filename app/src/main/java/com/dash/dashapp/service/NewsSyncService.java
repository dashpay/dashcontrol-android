package com.dash.dashapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.DashBlogNews;
import com.dash.dashapp.events.NewsSyncCompleteEvent;
import com.dash.dashapp.events.NewsSyncFailedEvent;
import com.dash.dashapp.models.BlogNews;
import com.dash.dashapp.utils.MainPreferences;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsSyncService extends Service {

    private static final String TAG = NewsSyncService.class.getCanonicalName();

    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int HTTP_STATUS_CODE_NOT_FOUND = 404;

    private MainPreferences preferences;

    private Call<List<DashBlogNews>> blogNewsCall;

    private int currentPage;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new MainPreferences(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        syncFirstPage();
        return Service.START_NOT_STICKY;
    }

    private void syncFirstPage() {
        sync(1);
    }

    private void syncNextPage() {
        sync(currentPage + 1);
    }

    private void sync(int page) {
        currentPage = page;
        blogNewsCall = DashControlClient.getInstance().getBlogNews(page);
        blogNewsCall.enqueue(blogNewsCallCallback);
        Log.d(TAG, "Synchronising page " + page);
    }

    private Callback<List<DashBlogNews>> blogNewsCallCallback = new Callback<List<DashBlogNews>>() {
        @Override
        public void onResponse(@NonNull Call<List<DashBlogNews>> call,
                               @NonNull Response<List<DashBlogNews>> response) {
            if (response.isSuccessful()) {
                List<DashBlogNews> responseNewsList = Objects.requireNonNull(response.body());
                Log.d(TAG, "Received news: " + responseNewsList.size());
                List<BlogNews> newsList = new ArrayList<>();
                for (DashBlogNews responseNews : responseNewsList) {
                    newsList.add(responseNews.convert());
                }
                syncData(newsList);
            } else {
                if (response.code() == HTTP_STATUS_CODE_NOT_FOUND) {
                    Log.d(TAG, "Downloading historical news complete");
                    preferences.setNewsHistoryComplete(true);
                    stopSelf();
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<DashBlogNews>> call, @NonNull Throwable t) {
            EventBus.getDefault().post(new NewsSyncFailedEvent(t));
            stopSelf();
        }
    };

    private void syncData(List<BlogNews> newsList) {
        boolean savedAllNews = saveUnsaved(newsList);
        boolean isLastPage = newsList.size() < DEFAULT_PAGE_SIZE;
        if (!isLastPage && (savedAllNews || !preferences.isNewsHistoryComplete())) {
            syncNextPage();
        } else {
            if (isLastPage) {
                Log.d(TAG, "Downloading historical news complete");
                preferences.setNewsHistoryComplete(true);
            }
            Log.d(TAG, "News sync complete");
            stopSelf();
        }
    }

    private boolean saveUnsaved(List<BlogNews> newsList) {
        List<BlogNews> unsavedNewsList;
        if (isDatabaseEmpty()) {
            unsavedNewsList = newsList;
        } else {
            unsavedNewsList = new ArrayList<>();
            for (BlogNews news : newsList) {
                if (!isSaved(news.date, news.title)) {
                    unsavedNewsList.add(news);
                }
            }
        }
        if (unsavedNewsList.size() > 0) {
            save(unsavedNewsList);
        }
        Log.d(TAG, "Saved news: " + unsavedNewsList.size());
        return (unsavedNewsList.size() == newsList.size());
    }

    private void save(final List<BlogNews> blogNewsList) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.insert(blogNewsList);
                }
            });
        }
    }

    public boolean isDatabaseEmpty() {
        try (Realm realm = Realm.getDefaultInstance()) {
            return (realm.where(BlogNews.class).count() == 0);
        }
    }

    public boolean isSaved(Date date, String title) {
        try (Realm realm = Realm.getDefaultInstance()) {
            return realm.where(BlogNews.class)
                    .equalTo(BlogNews.Field.DATE, date)
                    .equalTo(BlogNews.Field.TITLE, title)
                    .count() > 0;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        cancelRequest();
        EventBus.getDefault().post(new NewsSyncCompleteEvent());
        super.onDestroy();
    }

    private void cancelRequest() {
        if (blogNewsCall != null) {
            blogNewsCall.cancel();
        }
    }
}
