package com.dash.dashapp.api.service;

import com.dash.dashapp.api.data.BudgetApiBudgetAnswer;
import com.dash.dashapp.api.data.BudgetApiBudgetHistoryAnswer;
import com.dash.dashapp.api.data.BudgetApiProposalAnswer;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DashCentralService {

    @GET("budget")
    Call<BudgetApiBudgetAnswer> budget();

    @GET("budgethistory")
    Call<BudgetApiBudgetHistoryAnswer> budgetHistory();

    @GET("proposal")
    Call<BudgetApiProposalAnswer> proposalDetails(
            @Query("hash") String proposalHash);

    @FormUrlEncoded
    @POST("setappdata?do=post_proposal_comment")
    Call<JsonObject> postProposalComment(
            @Query("api_key") String apiKey,
            @Field("comment ") String comment,
            @Field("proposal_hash") String proposalHash,
            @Field("comment_identifier") String commentIdentifier);
}
