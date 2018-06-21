package com.dash.dashapp.api.data;

import com.dash.dashapp.models.PriceChartRecord;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ChartRecord
        implements PriceChartRecord.Convertible {

    @SerializedName("time")
    public Date time;

    @SerializedName("close")
    public float close;

    @SerializedName("high")
    public float high;

    @SerializedName("low")
    public float low;

    @SerializedName("open")
    public float open;

    @SerializedName("pairVolume")
    public float pairVolume;

    @SerializedName("trades")
    public float trades;

    @SerializedName("volume")
    public float volume;

    public String exchange;

    public String market;

    @Override
    public PriceChartRecord convert() {
        PriceChartRecord priceChartRecord = new PriceChartRecord();
        priceChartRecord.time = time.getTime();
        priceChartRecord.close = close;
        priceChartRecord.high = high;
        priceChartRecord.low = low;
        priceChartRecord.open = open;
        priceChartRecord.pairVolume = pairVolume;
        priceChartRecord.trades = trades;
        priceChartRecord.volume = volume;
        priceChartRecord.exchange = exchange;
        priceChartRecord.market = market;
        return priceChartRecord;
    }
}