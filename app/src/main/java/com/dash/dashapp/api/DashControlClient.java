package com.dash.dashapp.api;

import com.dash.dashapp.api.data.BudgetApiAnswer;
import com.dash.dashapp.api.data.DashBlogNews;
import com.dash.dashapp.api.service.DashBlogService;
import com.dash.dashapp.api.service.DashCentralService;
import com.dash.dashapp.utils.URLs;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashControlClient {

    private String TAG = DashControlClient.class.getSimpleName();

    private final DashBlogService dashBlogService;
    private final DashCentralService dashCentralService;

    private static final DashControlClient INSTANCE = new DashControlClient();

    private DashControlClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss Z")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.DASH_BLOG_API)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        dashBlogService = retrofit.create(DashBlogService.class);

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.DASH_CENTRAL_API)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        dashCentralService = retrofit.create(DashCentralService.class);
    }

    public static DashControlClient getInstance() {
        return INSTANCE;
    }

    public Call<List<DashBlogNews>> getBlogNews(int page) {
        return dashBlogService.blogNews(page);
    }

    public Call<BudgetApiAnswer> getDashProposals(int page) {
        return dashCentralService.proposals();
    }
}
