package com.dash.dashapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.DashControlMarketsAnswer;
import com.dash.dashapp.models.Exchange;
import com.dash.dashapp.models.Market;
import com.dash.dashapp.utils.ChartDataDownloader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.realm.Realm;
import io.realm.RealmResults;

public class PriceDataService extends Service {

    private static final String TAG = PriceDataService.class.getCanonicalName();

    private Queue<Pair<String, String>> chartDataSyncQueue;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startSync();
        return Service.START_NOT_STICKY;
    }

    private void startSync() {
        syncExchanges();
    }

    private void syncExchanges() {
        DashControlClient.getInstance().getPrices(new DashControlClient.Callback<List<Exchange>>() {
            @Override
            public void onResponse(List<Exchange> exchanges) {
                syncDefaultMarket(exchanges);
            }

            @Override
            public void onFailure(Throwable t) {
                logError(t);
            }
        });
    }

    private void syncDefaultMarket(final List<Exchange> exchanges) {
        DashControlClient.getInstance().getDefaultMarket(new DashControlClient.Callback<DashControlMarketsAnswer.DefaultMarket>() {
            @Override
            public void onResponse(DashControlMarketsAnswer.DefaultMarket defaultMarketStr) {
                if (defaultMarketStr != null) {
                    Exchange defaultExchange = exchanges.get(0);
                    Market defaultMarket = defaultExchange.markets.get(0);
                    for (Exchange exchange : exchanges) {
                        if (!exchange.name.equals(defaultMarketStr.exchange)) {
                            continue;
                        }
                        defaultExchange = exchange;
                        for (Market market : exchange.markets) {
                            if (market.name.equals(defaultMarketStr.market)) {
                                market.isDefault = true;
                                defaultMarket = market;
                                break;
                            }
                        }
                    }
                    // default market goes to the top of the list
                    defaultExchange.markets.remove(defaultMarket);
                    defaultExchange.markets.add(0, defaultMarket);
                    exchanges.remove(defaultExchange);
                    exchanges.add(0, defaultExchange);
                }
                persist(exchanges);
                syncChartData(exchanges);
            }

            @Override
            public void onFailure(Throwable t) {
                logError(t);
            }
        });
    }

    private void syncChartData(List<Exchange> exchanges) {
        chartDataSyncQueue = new LinkedList<>();
        for (Exchange exchange : exchanges) {
            for (Market market : exchange.markets) {
                chartDataSyncQueue.offer(new Pair<>(exchange.name, market.name));
            }
        }
        syncNextMarketChartData();
    }

    private void syncNextMarketChartData() {
        if (chartDataSyncQueue.size() > 0) {
            Pair<String, String> exchangeMarket = chartDataSyncQueue.poll();
            String exchange = exchangeMarket.first;
            String market = exchangeMarket.second;
            ChartDataDownloader chartDataDownloader = new ChartDataDownloader(chartDownloadCallback);
            chartDataDownloader.download(exchange, market);
        }
    }

    ChartDataDownloader.DownloadCallback chartDownloadCallback = new ChartDataDownloader.DownloadCallback() {
        @Override
        public void onFinished(String exchange, String market) {
            syncNextMarketChartData();
        }

        @Override
        public void onFailure(Throwable t) {
            if (t != null) {
                Log.e(TAG, t.getMessage());
            } else {
                Log.e(TAG, "Error downloading chart data");
            }
        }
    };

    private void logError(@Nullable Throwable t) {
        Log.e(TAG, "Unable to get data from dash control API");
        if (t != null) {
            t.printStackTrace();
        }
    }

    private void persist(final List<Exchange> exchanges) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.delete(Exchange.class);
                    realm.insert(exchanges);
                }
            });
        }
    }

    public static List<Exchange> findExchanges() {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Exchange> queryResult = realm
                    .where(Exchange.class)
                    .findAll();
            List<Exchange> queryResultUnmanagedCopy = realm.copyFromRealm(queryResult);
            return new ArrayList<>(queryResultUnmanagedCopy);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
