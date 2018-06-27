package com.dash.dashapp.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BudgetApiBudgetHistoryAnswer extends BudgetApiAnswer {

    @SerializedName("proposals")
    public List<DashProposal> proposals;
}
