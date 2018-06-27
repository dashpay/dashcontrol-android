package com.dash.dashapp.models;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

public class BudgetProposal extends RealmObject
        implements Serializable {

    public interface Field {
        String TITLE = "title";
        String DATE_ADDED = "dateAdded";
        String DATE_END = "dateEnd";
        String REMAINING_PAYMENT_COUNT = "remainingPaymentCount";
        String WILL_BE_FUNDED = "willBeFunded";
        String IN_NEXT_BUDGET = "inNextBudget";
        String HISTORICAL = "historical";
    }

    private String hash;
    private String name;
    private String url;
    private String dwUrl;
    private String dwUrlComments;
    private String title;
    private Date dateAdded;
    private String dateAddedHuman;
    private Date dateEnd;
    private Date votingDeadline;
    private String votingDeadlineHuman;
    private boolean willBeFunded;
    private int remainingYesVotesUntilFunding;
    private boolean inNextBudget;
    private float monthlyAmount;
    private int totalPaymentCount;
    private int remainingPaymentCount;
    private int yesVotes;
    private int noVotes;
    private int abstainVotes;
    private int order;
    private int commentAmount;
    private String owner;
    private String descriptionHtml;
    private boolean historical;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDwUrl() {
        return dwUrl;
    }

    public void setDwUrl(String dwUrl) {
        this.dwUrl = dwUrl;
    }

    public String getDwUrlComments() {
        return dwUrlComments;
    }

    public void setDwUrlComments(String dwUrlComments) {
        this.dwUrlComments = dwUrlComments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDateAddedHuman() {
        return dateAddedHuman;
    }

    public void setDateAddedHuman(String dateAddedHuman) {
        this.dateAddedHuman = dateAddedHuman;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Date getVotingDeadline() {
        return votingDeadline;
    }

    public void setVotingDeadline(Date votingDeadline) {
        this.votingDeadline = votingDeadline;
    }

    public String getVotingDeadlineHuman() {
        return votingDeadlineHuman;
    }

    public void setVotingDeadlineHuman(String votingDeadlineHuman) {
        this.votingDeadlineHuman = votingDeadlineHuman;
    }

    public boolean isWillBeFunded() {
        return willBeFunded;
    }

    public void setWillBeFunded(boolean willBeFunded) {
        this.willBeFunded = willBeFunded;
    }

    public int getRemainingYesVotesUntilFunding() {
        return remainingYesVotesUntilFunding;
    }

    public void setRemainingYesVotesUntilFunding(int remainingYesVotesUntilFunding) {
        this.remainingYesVotesUntilFunding = remainingYesVotesUntilFunding;
    }

    public boolean isInNextBudget() {
        return inNextBudget;
    }

    public void setInNextBudget(boolean inNextBudget) {
        this.inNextBudget = inNextBudget;
    }

    public float getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(float monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public int getTotalPaymentCount() {
        return totalPaymentCount;
    }

    public void setTotalPaymentCount(int totalPaymentCount) {
        this.totalPaymentCount = totalPaymentCount;
    }

    public int getRemainingPaymentCount() {
        return remainingPaymentCount;
    }

    public void setRemainingPaymentCount(int remainingPaymentCount) {
        this.remainingPaymentCount = remainingPaymentCount;
    }

    public int getYesVotes() {
        return yesVotes;
    }

    public void setYesVotes(int yesVotes) {
        this.yesVotes = yesVotes;
    }

    public int getNoVotes() {
        return noVotes;
    }

    public void setNoVotes(int noVotes) {
        this.noVotes = noVotes;
    }

    public int getAbstainVotes() {
        return abstainVotes;
    }

    public void setAbstainVotes(int abstainVotes) {
        this.abstainVotes = abstainVotes;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getCommentAmount() {
        return commentAmount;
    }

    public void setCommentAmount(int commentAmount) {
        this.commentAmount = commentAmount;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public boolean isOngoing() {
        Date today = new Date();
        return getDateEnd().after(today) && (getRemainingPaymentCount() > 0) && isWillBeFunded() && isInNextBudget();
    }

    public boolean isPast() {
        Date today = new Date();
        return getDateEnd().before(today);
    }

    public int getRatioYes() {
        float ratioYes = ((float) getYesVotes() / (getYesVotes() + getNoVotes())) * 100;
        return (int) ratioYes;
    }

    public boolean isHistorical() {
        return historical;
    }

    public void setHistorical(boolean historical) {
        this.historical = historical;
    }
}