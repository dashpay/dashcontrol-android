package com.dash.dashapp.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BudgetApiProposalAnswer extends BudgetApiAnswer {

    @SerializedName("proposal")
    public DashProposal proposal;

    @SerializedName("comments")
    public List<DashProposalComment> comments;
}
