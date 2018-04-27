package com.dash.dashapp.api.service;

import com.dash.dashapp.api.data.DashControlPricesAnswer;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DashControlService {

    @GET("prices")
    Call<DashControlPricesAnswer> prices();
}
