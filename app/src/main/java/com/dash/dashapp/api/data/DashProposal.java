package com.dash.dashapp.api.data;

import android.util.Base64;

import com.dash.dashapp.models.BudgetProposal;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

public class DashProposal extends RealmObject
        implements BudgetProposal.Convertible {

    @SerializedName("hash")
    private String hash;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    @SerializedName("dw_url")
    private String dwUrl;

    @SerializedName("dw_url_comments")
    private String dwUrlComments;

    @SerializedName("title")
    private String title;

    @SerializedName("date_added")
    private Date dateAdded;

    @SerializedName("date_added_human")
    private String dateAddedHuman;

    @SerializedName("date_end")
    private Date dateEnd;

    @SerializedName("voting_deadline")
    private Date votingDeadline;

    @SerializedName("voting_deadline_human")
    private String votingDeadlineHuman;

    @SerializedName("will_be_funded")
    private boolean willBeFunded;

    @SerializedName("remaining_yes_votes_until_funding")
    private int remainingYesVotesUntilFunding;

    @SerializedName("in_next_budget")
    private boolean inNextBudget;

    @SerializedName("monthly_amount")
    private float monthlyAmount;

    @SerializedName("total_payment_count")
    private int totalPaymentCount;

    @SerializedName("remaining_payment_count")
    private int remainingPaymentCount;

    @SerializedName("yes")
    private int yesVotes;

    @SerializedName("no")
    private int noVotes;

    @SerializedName("abstain")
    private int abstainVotes;

    @SerializedName("order")
    private int order;

    @SerializedName("comment_amount")
    private int commentAmount;

    @SerializedName("owner_username")
    private String owner;

    @SerializedName("description_base64_bb")
    private String descriptionBase64Bb;

    @SerializedName("description_base64_html")
    private String descriptionBase64Html;

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
        if (descriptionBase64Html != null) {
            budgetProposal.descriptionHtml = new String(Base64.decode(descriptionBase64Html, Base64.DEFAULT));
        }
        return budgetProposal;
    }
}
