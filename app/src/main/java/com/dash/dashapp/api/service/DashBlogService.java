package com.dash.dashapp.api.service;

import com.dash.dashapp.api.data.DashBlogNews;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DashBlogService {

    @GET("blogapi/feed-{page}.json")
    Call<List<DashBlogNews>> blogNews(@Path("page") int page);
}
