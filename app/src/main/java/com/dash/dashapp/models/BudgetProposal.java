package com.dash.dashapp.models;

import java.io.Serializable;
import java.util.Date;

public class BudgetProposal implements Serializable {

    public String hash;
    public String name;
    public String url;
    public String dwUrl;
    public String dwUrlComments;
    public String title;
    public Date dateAdded;
    public String dateAddedHuman;
    public Date dateEnd;
    public Date votingDeadline;
    public String votingDeadlineHuman;
    public boolean willBeFunded;
    public int remainingYesVotesUntilFunding;
    public boolean inNextBudget;
    public float monthlyAmount;
    public int totalPaymentCount;
    public int remainingPaymentCount;
    public int yesVotes;
    public int noVotes;
    public int abstainVotes;
    public int order;
    public int commentAmount;
    public String owner;
    public String descriptionHtml;

    public interface Convertible {
        BudgetProposal convert();
    }

    public boolean isOngoing() {
        Date today = new Date();
        return dateEnd.after(today) && (remainingPaymentCount > 0) && willBeFunded && inNextBudget;
    }

    public boolean isPast() {
        Date today = new Date();
        return dateEnd.before(today);
    }

    public int getRatioYes() {
        float ratioYes = ((float) yesVotes / (yesVotes + noVotes)) * 100;
        return (int) ratioYes;
    }
}