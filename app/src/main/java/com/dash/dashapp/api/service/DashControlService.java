package com.dash.dashapp.api.service;

import com.dash.dashapp.api.data.ChartRecord;
import com.dash.dashapp.api.data.DashControlChartDataAnswer;
import com.dash.dashapp.api.data.DashControlMarketsAnswer;
import com.dash.dashapp.api.data.DashControlPricesAnswer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DashControlService {

    @GET("markets")
    Call<DashControlMarketsAnswer> markets();

    @GET("prices")
    Call<DashControlPricesAnswer> prices();

    @GET("chart_data")
    Call<List<ChartRecord>> chartData(
            @Query("noLimit") boolean noLimit,
            @Query("exchange") String exchange,
            @Query("market") String market,
            @Query("start") long startTime,
            @Query("end") long endTime);
}
