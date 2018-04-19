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

    public interface Convertible {
        BudgetProposal convert();
    }
}