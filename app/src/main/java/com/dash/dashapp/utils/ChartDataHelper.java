package com.dash.dashapp.utils;

import android.support.annotation.NonNull;

import com.dash.dashapp.api.data.ChartRecord;
import com.dash.dashapp.models.PriceChartRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChartDataHelper {

    public static boolean isDatabaseEmpty(String exchange, String market) {
        try (Realm realm = Realm.getDefaultInstance()) {
            long count = realm.where(PriceChartRecord.class)
                    .equalTo(PriceChartRecord.Field.EXCHANGE, exchange)
                    .equalTo(PriceChartRecord.Field.MARKET, market)
                    .count();
            return (count == 0);
        }
    }

    public static void persist(final String exchange, final String market, final List<ChartRecord> chartRecordList) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    List<PriceChartRecord> chartPriceRecordList = new ArrayList<>();
                    for (ChartRecord chartRecord : chartRecordList) {
                        chartRecord.exchange = exchange;
                        chartRecord.market = market;
                        chartPriceRecordList.add(chartRecord.convert());
                    }
                    realm.insert(chartPriceRecordList);
                }
            });
        }
    }

    public void clearDatabase(final String exchange, final String market) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.where(PriceChartRecord.class)
                            .equalTo(PriceChartRecord.Field.EXCHANGE, exchange)
                            .equalTo(PriceChartRecord.Field.MARKET, market)
                            .findAll()
                            .deleteAllFromRealm();
                }
            });
        }
    }

    public static PriceChartRecord oldestPersistedRecord(String exchange, String market) {
        try (Realm realm = Realm.getDefaultInstance()) {
            PriceChartRecord record = realm.where(PriceChartRecord.class)
                    .equalTo(PriceChartRecord.Field.EXCHANGE, exchange)
                    .equalTo(PriceChartRecord.Field.MARKET, market)
                    .sort(PriceChartRecord.Field.TIME, Sort.ASCENDING)
                    .findFirst();
            return record != null ? realm.copyFromRealm(record) : null;
        }
    }

    public static PriceChartRecord mostRecentPersistedRecord(String exchange, String market) {
        try (Realm realm = Realm.getDefaultInstance()) {
            PriceChartRecord record = realm.where(PriceChartRecord.class)
                    .equalTo(PriceChartRecord.Field.EXCHANGE, exchange)
                    .equalTo(PriceChartRecord.Field.MARKET, market)
                    .sort(PriceChartRecord.Field.TIME, Sort.DESCENDING)
                    .findFirst();
            return record != null ? realm.copyFromRealm(record) : null;
        }
    }

    private static List<PriceChartRecord> getChartData(String exchange, String market, long startTime) {
        return getChartData(exchange, market, startTime, -1);
    }

    private static List<PriceChartRecord> getChartData(String exchange, String market, long startTime, long endTime) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmQuery<PriceChartRecord> query = realm.where(PriceChartRecord.class)
                    .equalTo(PriceChartRecord.Field.EXCHANGE, exchange)
                    .equalTo(PriceChartRecord.Field.MARKET, market)
                    .greaterThanOrEqualTo(PriceChartRecord.Field.TIME, startTime);

            if (endTime > 0) {
                query = query.lessThanOrEqualTo(PriceChartRecord.Field.TIME, new Date(endTime));
            }

            RealmResults<PriceChartRecord> queryResult = query
                    .sort(PriceChartRecord.Field.TIME, Sort.ASCENDING)
                    .findAll();

            return realm.copyFromRealm(queryResult);
        }
    }

    public static List<PriceChartRecord> getChartData(String exchange, String market, long startTime, Candlestick candlestick) {
        List<PriceChartRecord> baseChartData = getChartData(exchange, market, startTime);
        return getChartData(baseChartData, candlestick);
    }

    private static List<PriceChartRecord> getChartData(List<PriceChartRecord> baseChartData, Candlestick candlestick) {
        if (candlestick == null || candlestick == Candlestick.FIVE_MINUTES) {
            return baseChartData;
        } else {
            List<PriceChartRecord> customCandlestickChartData = new ArrayList<>();
            long candlestickDuration = candlestick.getDuration();
            long numOfBaseRecords = candlestickDuration / Candlestick.FIVE_MINUTES.getDuration();
            PriceChartRecord customCandlestickRecord = new PriceChartRecord();
            for (int i = 0; i < baseChartData.size(); i++) {
                PriceChartRecord record = baseChartData.get(i);
                if (i % numOfBaseRecords == 0) {
                    customCandlestickRecord = record.copy();
                    customCandlestickChartData.add(customCandlestickRecord);
                } else {
                    customCandlestickRecord.pairVolume += record.pairVolume;
                    customCandlestickRecord.trades += record.trades;
                    customCandlestickRecord.volume += record.volume;
                    customCandlestickRecord.low = Math.min(customCandlestickRecord.low, record.low);
                    customCandlestickRecord.high = Math.max(customCandlestickRecord.high, record.high);
                    customCandlestickRecord.close = record.close;
                }
            }
            return customCandlestickChartData;
        }
    }

    public enum TimeFrame {

        SIX_HOURS(TimeUnit.HOURS.toMillis(6)),
        TWENTY_FOUR_HOURS(TimeUnit.HOURS.toMillis(24)),
        TWO_DAYS(TimeUnit.DAYS.toMillis(2)),
        FOUR_DAYS(TimeUnit.DAYS.toMillis(4)),
        ONE_WEEK(TimeUnit.DAYS.toMillis(7)),
        TWO_WEEKS(TimeUnit.DAYS.toMillis(14)),
        ONE_MONTH(TimeUnit.DAYS.toMillis(30)),
        THREE_MONTHS(TimeUnit.DAYS.toMillis(90));

        private long duration;

        TimeFrame(long duration) {
            this.duration = duration;
        }

        public long getDuration() {
            return duration;
        }

        public static TimeFrame valueOf(long duration) {
            for (TimeFrame timeFrame : values()) {
                if (timeFrame.duration == duration) {
                    return timeFrame;
                }
            }
            throw new IllegalArgumentException("Unsupported duration " + duration);
        }
    }

    public enum Candlestick {
        FIVE_MINUTES(TimeUnit.MINUTES.toMillis(5)),
        FIFTEEN_MINUTES(TimeUnit.MINUTES.toMillis(15)),
        THIRTY_MINUTES(TimeUnit.MINUTES.toMillis(30)),
        TWO_HOURS(TimeUnit.HOURS.toMillis(2)),
        FOUR_HOURS(TimeUnit.HOURS.toMillis(4)),
        TWENTY_FOUR_HOURS(TimeUnit.HOURS.toMillis(24));

        private long duration;

        Candlestick(long duration) {
            this.duration = duration;
        }

        public long getDuration() {
            return duration;
        }

        public static Candlestick valueOf(long duration) {
            for (Candlestick candidate : values()) {
                if (candidate.duration == duration) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Unsupported duration " + duration);
        }
    }
}
