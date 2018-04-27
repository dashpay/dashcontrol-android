package com.dash.dashapp.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BudgetApiAnswer {

    @SerializedName("status")
    public String status;

    @SerializedName("error_type")
    public String errorType;

    @SerializedName("budget")
    public DashBudget dashBudget;

    @SerializedName("proposals")
    public List<DashProposal> proposals;
}
