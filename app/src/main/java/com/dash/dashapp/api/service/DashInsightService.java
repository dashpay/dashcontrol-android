package com.dash.dashapp.api.service;

import com.dash.dashapp.api.data.InsightResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DashInsightService {

    @GET("addrs/{addrs}/utxo")
    Call<List<InsightResponse>> utxos(
            @Path("addrs") String addrs);
}
