package com.dash.dashapp.utils;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LongSparseArray;

import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.ChartRecord;
import com.dash.dashapp.api.data.DashControlChartDataAnswer;
import com.dash.dashapp.models.PriceChartRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

public class ChartDataDownloader {

    private static final String TAG = ChartDataDownloader.class.getCanonicalName();

    private static final long SINGLE_RECORD_TIME = TimeUnit.MINUTES.toMillis(5);
    private long PACKAGE_TIME_WINDOW = TimeUnit.HOURS.toMillis(24);
    private long COMPLETE_TIME_WINDOW = PACKAGE_TIME_WINDOW * 14;

    private DashControlClient dashControlClient;
    private long currentTimeUtc;

    private DownloadCallback callback;

    public ChartDataDownloader(DownloadCallback callback) {
        dashControlClient = DashControlClient.getInstance();
        this.callback = callback;
    }

    public void download(String exchange, String market) {
        Log.d(TAG, String.format(Locale.getDefault(), "downloading started (%s) (%s)", exchange, market));

        currentTimeUtc = DateUtil.roundDownToNearest5minUtc(System.currentTimeMillis());
        if (ChartDataHelper.isDatabaseEmpty(exchange, market)) {
            downloadHistorical(exchange, market, currentTimeUtc);
        } else {
            downloadLatest(exchange, market);
        }
    }

    private void downloadLatest(String exchange, String market) {
        PriceChartRecord mostRecentPersistedRecord = ChartDataHelper.mostRecentPersistedRecord(exchange, market);
        if (mostRecentPersistedRecord == null) {
            throw new IllegalStateException("For empty database you should use downloadHistorical(...) instead");
        }
        long startTime = mostRecentPersistedRecord.time + SINGLE_RECORD_TIME;
        if (startTime < currentTimeUtc) {
            downloadChartData(exchange, market, startTime, currentTimeUtc, false);
        }
    }

    private void downloadHistorical(String exchange, String market, long endTime) {
        long startTime = endTime - PACKAGE_TIME_WINDOW + SINGLE_RECORD_TIME;
        downloadChartData(exchange, market, startTime, endTime, true);
    }

    private void downloadChartData(final String exchange, final String market,
                                   final long startTime, final long endTime, final boolean historical) {

        Log.d(TAG, String.format(Locale.getDefault(), "downloading records from %s to %s", new Date(startTime), new Date(endTime)));

        Call<DashControlChartDataAnswer> chartDataCall = dashControlClient.getChartData(true, exchange, market, startTime, endTime);
        chartDataCall.enqueue(new Callback<DashControlChartDataAnswer>() {
            @Override
            public void onResponse(@NonNull Call<DashControlChartDataAnswer> call,
                                   @NonNull retrofit2.Response<DashControlChartDataAnswer> response) {
                if (response.isSuccessful()) {
                    DashControlChartDataAnswer responseBody = response.body();
                    if (responseBody != null) {
                        List<ChartRecord> responseBodyRecords = responseBody.getRecords();
                        Log.d(TAG, String.format(Locale.getDefault(), "downloaded %d records", responseBodyRecords.size()));
                        if (responseBodyRecords.size() > 0) {
                            saveChartData(exchange, market, startTime, endTime, responseBodyRecords, historical);
                            downloadingNextChunkOrFinish(exchange, market, endTime, historical);
                        } else {
                            Log.d(TAG, String.format(Locale.getDefault(), "downloading finished (%s) (%s) - no more data", exchange, market));
                            callback.onFinished(exchange, market);
                        }
                    }
                    return;
                }
                handleError(null);
            }

            @Override
            public void onFailure(@NonNull Call<DashControlChartDataAnswer> call, @NonNull Throwable t) {
                handleError(t);
            }
        });
    }

    private void handleError(Throwable t) {
        callback.onFailure(t);
    }

    private void saveChartData(String exchange, String market,
                               long startTime, long endTime, List<ChartRecord> responseBodyRecords, boolean historical) {
        if (historical) {
            responseBodyRecords = addMissingHistoricalRecords(exchange, market, responseBodyRecords, endTime);
        } else {
            responseBodyRecords = addMissingRecords(exchange, market, responseBodyRecords, startTime, endTime);
        }

        ChartDataHelper.persist(exchange, market, responseBodyRecords);
    }

    private void downloadingNextChunkOrFinish(String exchange, String market, long endTime, boolean historical) {
        if (historical && (endTime > (currentTimeUtc - COMPLETE_TIME_WINDOW))) {
            endTime -= PACKAGE_TIME_WINDOW;
            downloadHistorical(exchange, market, endTime);
        } else {
            Log.d(TAG, String.format(Locale.getDefault(), "downloading finished (%s) (%s)", exchange, market));
            callback.onFinished(exchange, market);
        }
    }

    private List<ChartRecord> addMissingHistoricalRecords(String exchange, String market,
                                                          List<ChartRecord> chartRecordList, long endTime) {

        LongSparseArray<ChartRecord> apiRecordsMap = new LongSparseArray<>();
        for (ChartRecord record : chartRecordList) {
            apiRecordsMap.append(record.time.getTime(), record);
        }

        PriceChartRecord oldestPersistedRecord = ChartDataHelper.oldestPersistedRecord(exchange, market);
        if (oldestPersistedRecord != null) {
            endTime = oldestPersistedRecord.time - SINGLE_RECORD_TIME;
        }
        long startTime = chartRecordList.get(0).time.getTime();

        List<ChartRecord> recordListFull = new ArrayList<>();
        ChartRecord previousRecord = null;
        for (long time = startTime; time <= endTime; time += SINGLE_RECORD_TIME) {
            ChartRecord record;
            if (apiRecordsMap.get(time) != null) {
                record = apiRecordsMap.get(time);
            } else {
                float previousRecordClose = Objects.requireNonNull(previousRecord).close;
                record = createNoTradesRecord(time, previousRecordClose);
            }
            recordListFull.add(record);
            previousRecord = record;
        }
        return recordListFull;
    }

    private List<ChartRecord> addMissingRecords(String exchange, String market,
                                                List<ChartRecord> chartRecordList, long startTime, long endTime) {

        LongSparseArray<ChartRecord> apiRecordsMap = new LongSparseArray<>();
        for (ChartRecord record : chartRecordList) {
            apiRecordsMap.append(record.time.getTime(), record);
        }

        List<ChartRecord> recordListFull = new ArrayList<>();
        PriceChartRecord previousRecord = ChartDataHelper.mostRecentPersistedRecord(exchange, market);
        for (long time = startTime; time <= endTime; time += SINGLE_RECORD_TIME) {
            ChartRecord record;
            if (apiRecordsMap.get(time) != null) {
                record = apiRecordsMap.get(time);
            } else {
                float previousRecordClose = Objects.requireNonNull(previousRecord).close;
                record = createNoTradesRecord(time, previousRecordClose);
            }
            recordListFull.add(record);
            previousRecord = record.convert();
        }
        return recordListFull;
    }

    private ChartRecord createNoTradesRecord(long time, float previousClose) {
        ChartRecord record = new ChartRecord();
        record.time = new Date(time);
        record.close = previousClose;
        record.high = previousClose;
        record.low = previousClose;
        record.open = previousClose;
        record.pairVolume = 0;
        record.trades = 0;
        record.volume = 0;
        return record;
    }

    public interface DownloadCallback {
        void onFinished(String exchange, String market);

        void onFailure(Throwable t);
    }
}
