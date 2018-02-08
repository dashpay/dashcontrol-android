package com.dash.dashapp.models;

/**
 * Created by sebas on 1/29/2018.
 */

public class PriceChartData {

    private long time;
    private long close;
    private long high;
    private long low;
    private long open;
    private long pairVolume;
    private long trades;
    private long volume;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getClose() {
        return close;
    }

    public void setClose(long close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(long high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(long low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(long open) {
        this.open = open;
    }

    public double getPairVolume() {
        return pairVolume;
    }

    public void setPairVolume(long pairVolume) {
        this.pairVolume = pairVolume;
    }

    public double getTrades() {
        return trades;
    }

    public void setTrades(long trades) {
        this.trades = trades;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}
