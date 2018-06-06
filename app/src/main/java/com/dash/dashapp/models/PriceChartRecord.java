package com.dash.dashapp.models;

import io.realm.RealmObject;

public class PriceChartRecord extends RealmObject {

    public interface Field {
        String TIME = "time";
        String EXCHANGE = "exchange";
        String MARKET = "market";
    }

    public String exchange;
    public String market;
    public long time;

    public float close;
    public float high;
    public float low;
    public float open;
    public float pairVolume;
    public float trades;
    public float volume;

    public interface Convertible {
        PriceChartRecord convert();
    }

    public PriceChartRecord copy() {
        PriceChartRecord copy = new PriceChartRecord();
        copy.exchange = exchange;
        copy.market = market;
        copy.time = time;
        copy.close = close;
        copy.high = high;
        copy.low = low;
        copy.open = open;
        copy.pairVolume = pairVolume;
        copy.trades = trades;
        copy.volume = volume;
        return copy;
    }
}
