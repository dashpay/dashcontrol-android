package com.dash.dashapp.api.service;

import com.dash.dashapp.api.data.BudgetApiAnswer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DashCentralService {

    @GET("budget")
    Call<BudgetApiAnswer> proposals();
}
