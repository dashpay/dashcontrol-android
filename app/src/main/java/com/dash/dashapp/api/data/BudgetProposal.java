package com.dash.dashapp.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class BudgetProposal extends BudgetApiAnswer {

    @SerializedName("proposals")
    public Proposal[] proposals;

    static class Proposal implements com.dash.dashapp.models.BudgetProposal.Convertible {

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
        public int monthlyAmount;

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
        public com.dash.dashapp.models.BudgetProposal convert() {
            com.dash.dashapp.models.BudgetProposal budgetProposal = new com.dash.dashapp.models.BudgetProposal();
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
            return budgetProposal;
        }
    }
}
