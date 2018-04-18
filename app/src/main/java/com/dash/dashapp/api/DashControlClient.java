package com.dash.dashapp.api;

import com.dash.dashapp.api.data.DashBlogNews;
import com.dash.dashapp.api.service.DashBlogService;
import com.dash.dashapp.utils.URLs;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashControlClient {

    private String TAG = DashControlClient.class.getSimpleName();

    private final DashBlogService dashBlogService;

    private static final DashControlClient INSTANCE = new DashControlClient();

    private DashControlClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.DASH_CONTROL_BASE_API)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dashBlogService = retrofit.create(DashBlogService.class);
    }

    public static DashControlClient getInstance() {
        return INSTANCE;
    }

    public Call<List<DashBlogNews>> getBlogNews(int page) {
        return dashBlogService.blogNews(page);
    }
}
