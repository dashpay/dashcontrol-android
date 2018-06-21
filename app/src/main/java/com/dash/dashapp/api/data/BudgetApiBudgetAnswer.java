package com.dash.dashapp.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BudgetApiBudgetAnswer extends BudgetApiAnswer {

    @SerializedName("budget")
    public DashBudget dashBudget;

    @SerializedName("proposals")
    public List<DashProposal> proposals;
}
