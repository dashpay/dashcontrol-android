package com.dash.dashapp.api.data;

import com.dash.dashapp.models.BudgetProposal;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

public class DashProposal extends RealmObject
        implements BudgetProposal.Convertible {

    @SerializedName("hash")
    public String hash;

    @SerializedName("name")
    public String name;

    @SerializedName("url")
    public String url;

    @SerializedName("dw_url")
    public String dwUrl;

    @SerializedName("dw_url_comments")
    public String dwUrlComments;

    @SerializedName("title")
    public String title;

    @SerializedName("date_added")
    public Date dateAdded;

    @SerializedName("date_added_human")
    public String dateAddedHuman;

    @SerializedName("date_end")
    public Date dateEnd;

    @SerializedName("voting_deadline")
    public Date votingDeadline;

    @SerializedName("voting_deadline_human")
    public String votingDeadlineHuman;

    @SerializedName("will_be_funded")
    public boolean willBeFunded;

    @SerializedName("remaining_yes_votes_until_funding")
    public int remainingYesVotesUntilFunding;

    @SerializedName("in_next_budget")
    public boolean inNextBudget;

    @SerializedName("monthly_amount")
    public float monthlyAmount;

    @SerializedName("total_payment_count")
    public int totalPaymentCount;

    @SerializedName("remaining_payment_count")
    public int remainingPaymentCount;

    @SerializedName("yes")
    public int yesVotes;

    @SerializedName("no")
    public int noVotes;

    @SerializedName("abstain")
    public int abstainVotes;

    @SerializedName("order")
    public int order;

    @SerializedName("comment_amount")
    public int commentAmount;

    @SerializedName("owner_username")
    public String owner;

    @Override
    public BudgetProposal convert() {
        BudgetProposal budgetProposal = new BudgetProposal();
        budgetProposal.hash = hash;
        budgetProposal.name = name;
        budgetProposal.url = url;
        budgetProposal.dwUrl = dwUrl;
        budgetProposal.dwUrlComments = dwUrlComments;
        budgetProposal.title = title;
        budgetProposal.dateAdded = dateAdded;
        budgetProposal.dateAddedHuman = dateAddedHuman;
        budgetProposal.dateEnd = dateEnd;
        budgetProposal.votingDeadlineHuman = votingDeadlineHuman;
        budgetProposal.willBeFunded = willBeFunded;
        budgetProposal.remainingYesVotesUntilFunding = remainingYesVotesUntilFunding;
        budgetProposal.inNextBudget = inNextBudget;
        budgetProposal.monthlyAmount = monthlyAmount;
        budgetProposal.totalPaymentCount = totalPaymentCount;
        budgetProposal.remainingPaymentCount = remainingPaymentCount;
        budgetProposal.yesVotes = yesVotes;
        budgetProposal.noVotes = noVotes;
        budgetProposal.abstainVotes = abstainVotes;
        budgetProposal.order = order;
        budgetProposal.commentAmount = commentAmount;
        budgetProposal.owner = owner;
        return budgetProposal;
    }
}
