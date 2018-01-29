package com.dash.dashapp.models;

/**
 * Created by sebas on 1/29/2018.
 */

public class PriceChartData {

    private String time;
    private double close;
    private double high;
    private double low;
    private double open;
    private double pairVolume;
    private double trades;
    private double volume;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getPairVolume() {
        return pairVolume;
    }

    public void setPairVolume(double pairVolume) {
        this.pairVolume = pairVolume;
    }

    public double getTrades() {
        return trades;
    }

    public void setTrades(double trades) {
        this.trades = trades;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
