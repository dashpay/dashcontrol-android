package com.dash.dashapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by sebas on 8/5/2017.
 */


public class Proposal {

    @SerializedName("hash")
    @Expose
    private String hash; // proposal hash [string]

    @SerializedName("name")
    @Expose
    private String name; // proposal name [string]

    @SerializedName("url")
    @Expose
    private String url; // proposal URL set by the proposal owner during proposal submission [string]

    @SerializedName("dw_url")
    @Expose
    private String dw_url; // URL pointing to the DashCentral proposal page [string]

    @SerializedName("dw_url_comments")
    @Expose
    private String dw_url_comments; // URL pointing to the comment section on the DashCentral proposal page [string]

    @SerializedName("title")
    @Expose
    private String title; // proposal title entered by the proposal owner on DashCentral [string]

    @SerializedName("date_added")
    @Expose
    private Date date_added; // date, when the proposal was first seen on the network and has been added to the DashCentral database [datetime, UTC]

    @SerializedName("date_added_human")
    @Expose
    private String date_added_human; // time since proposal has been added to the DashCentral database in words, eg. "6 days ago" [string]

    @SerializedName("date_end")
    @Expose
    private Date date_end; // date, when proposal payouts are expected to end [datetime, UTC]

    @SerializedName("voting_deadline_human")
    @Expose
    private String voting_deadline_human; // time until voting will be closed for this proposal, e.g "in 15 days" or "passed" [string]

    @SerializedName("will_be_funded")
    @Expose
    private boolean will_be_funded; // is true when proposal has enough yes votes to become funded [boolean]

    @SerializedName("remaining_yes_votes_until_funding")
    @Expose
    private Integer remaining_yes_votes_until_funding; // amount of yes votes required for funding of this proposal [integer]

    @SerializedName("in_next_budget")
    @Expose
    private boolean in_next_budget; // indicates, if proposal will be included within next budget and will be paid [boolean]

    @SerializedName("monthly_amount")
    @Expose
    private Integer monthly_amount; // amount of DASH that will be paid per month [integer]

    @SerializedName("total_payment_count")
    @Expose
    private Integer total_payment_count; // amount of payment cycles this proposal was intended to run [integer]

    @SerializedName("remaining_payment_count")
    @Expose
    private Integer remaining_payment_count; // remaining payment cycles [integer]

    @SerializedName("yes")
    @Expose
    private Integer yes; // yes votes on this proposal [integer]

    @SerializedName("no")
    @Expose
    private Integer no; // no votes on this proposal [integer]

    @SerializedName("order")
    @Expose
    private Integer order; // the order value should be used to sort proposals in case the JSON order is not preserved correctly. The order is defined by a reddit like algo covering the time and the upvotes and downvotes on DashCentral [integer]

    @SerializedName("comment_amount")
    @Expose
    private Integer comment_amount; // amount of proposal comments posted on DashCentral [integer]

    @SerializedName("owner_username")
    @Expose
    private String owner_username; // username of the proposal owner on DashCentral [string]

    public Proposal() {
    }

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

    public String getDw_url() {
        return dw_url;
    }

    public void setDw_url(String dw_url) {
        this.dw_url = dw_url;
    }

    public String getDw_url_comments() {
        return dw_url_comments;
    }

    public void setDw_url_comments(String dw_url_comments) {
        this.dw_url_comments = dw_url_comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate_added() {
        return date_added;
    }

    public void setDate_added(Date date_added) {
        this.date_added = date_added;
    }

    public String getDate_added_human() {
        return date_added_human;
    }

    public void setDate_added_human(String date_added_human) {
        this.date_added_human = date_added_human;
    }

    public Date getDate_end() {
        return date_end;
    }

    public void setDate_end(Date date_end) {
        this.date_end = date_end;
    }

    public String getVoting_deadline_human() {
        return voting_deadline_human;
    }

    public void setVoting_deadline_human(String voting_deadline_human) {
        this.voting_deadline_human = voting_deadline_human;
    }

    public boolean isWill_be_funded() {
        return will_be_funded;
    }

    public void setWill_be_funded(boolean will_be_funded) {
        this.will_be_funded = will_be_funded;
    }

    public Integer getRemaining_yes_votes_until_funding() {
        return remaining_yes_votes_until_funding;
    }

    public void setRemaining_yes_votes_until_funding(Integer remaining_yes_votes_until_funding) {
        this.remaining_yes_votes_until_funding = remaining_yes_votes_until_funding;
    }

    public boolean isIn_next_budget() {
        return in_next_budget;
    }

    public void setIn_next_budget(boolean in_next_budget) {
        this.in_next_budget = in_next_budget;
    }

    public Integer getMonthly_amount() {
        return monthly_amount;
    }

    public void setMonthly_amount(Integer monthly_amount) {
        this.monthly_amount = monthly_amount;
    }

    public Integer getTotal_payment_count() {
        return total_payment_count;
    }

    public void setTotal_payment_count(Integer total_payment_count) {
        this.total_payment_count = total_payment_count;
    }

    public Integer getRemaining_payment_count() {
        return remaining_payment_count;
    }

    public void setRemaining_payment_count(Integer remaining_payment_count) {
        this.remaining_payment_count = remaining_payment_count;
    }

    public Integer getYes() {
        return yes;
    }

    public void setYes(Integer yes) {
        this.yes = yes;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getComment_amount() {
        return comment_amount;
    }

    public void setComment_amount(Integer comment_amount) {
        this.comment_amount = comment_amount;
    }

    public String getOwner_username() {
        return owner_username;
    }

    public void setOwner_username(String owner_username) {
        this.owner_username = owner_username;
    }
}