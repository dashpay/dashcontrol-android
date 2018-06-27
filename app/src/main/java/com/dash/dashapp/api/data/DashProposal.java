package com.dash.dashapp.api.data;

import android.util.Base64;

import com.dash.dashapp.models.BudgetProposal;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DashProposal {

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

    public BudgetProposal convert(boolean historical) {
        BudgetProposal budgetProposal = new BudgetProposal();
        budgetProposal.setHash(hash);
        budgetProposal.setName(name);
        budgetProposal.setUrl(url);
        budgetProposal.setDwUrl(dwUrl);
        budgetProposal.setDwUrlComments(dwUrlComments);
        budgetProposal.setTitle(title);
        budgetProposal.setDateAdded(dateAdded);
        budgetProposal.setDateAddedHuman(dateAddedHuman);
        budgetProposal.setDateEnd(dateEnd);
        budgetProposal.setVotingDeadlineHuman(votingDeadlineHuman);
        budgetProposal.setWillBeFunded(willBeFunded);
        budgetProposal.setRemainingYesVotesUntilFunding(remainingYesVotesUntilFunding);
        budgetProposal.setInNextBudget(inNextBudget);
        budgetProposal.setMonthlyAmount(monthlyAmount);
        budgetProposal.setTotalPaymentCount(totalPaymentCount);
        budgetProposal.setRemainingPaymentCount(remainingPaymentCount);
        budgetProposal.setYesVotes(yesVotes);
        budgetProposal.setNoVotes(noVotes);
        budgetProposal.setAbstainVotes(abstainVotes);
        budgetProposal.setOrder(order);
        budgetProposal.setCommentAmount(commentAmount);
        budgetProposal.setOwner(owner);
        if (descriptionBase64Html != null) {
            budgetProposal.setDescriptionHtml(new String(Base64.decode(descriptionBase64Html, Base64.DEFAULT)));
        }
        budgetProposal.setHistorical(historical);
        return budgetProposal;
    }
}
