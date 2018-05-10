package com.dash.dashapp.api.data;

import com.dash.dashapp.models.BudgetProposalComment;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BudgetApiProposalAnswer extends BudgetApiAnswer {

    @SerializedName("proposal")
    public DashProposal proposal;

    @SerializedName("comments")
    public List<DashProposalComment> comments;

    public List<BudgetProposalComment> convertComments() {
        List<BudgetProposalComment> result = new ArrayList<>();
        if (comments != null) {
            for (BudgetProposalComment.Convertible comment : comments) {
                result.add(comment.convert());
            }
        }
        return result;
    }
}
