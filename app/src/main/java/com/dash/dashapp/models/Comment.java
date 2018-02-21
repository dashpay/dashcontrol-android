package com.dash.dashapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sebas on 2/21/2018.
 */

public class Comment {

    private String hashProposal;

    @SerializedName("id")
    @Expose
    private String id;  // unique comment identifier [string]

    @SerializedName("username")
    @Expose
    private String username; // DashCentral username of the comment poster [string]

    @SerializedName("date")
    @Expose
    private String date; // comment date [datetime, UTC]

    @SerializedName("date_human")
    @Expose
    private String date_human; // time since comment has been posted in words e.g. "3 days ago" [string]

    @SerializedName("order")
    @Expose
    private Integer order; // sort comments using this order value [integer]

    @SerializedName("level")
    @Expose
    private Integer level; // use the level value to add a css padding (e.g. $level*13px) to the comments in order to create the impression of a tree [integer]

    @SerializedName("recently_posted")
    @Expose
    private Boolean recently_posted; // use this value to highlight comments that have been posted recently [boolean]

    @SerializedName("posted_by_owner")
    @Expose
    private Boolean posted_by_owner; // highlight comments posted by the owner of the proposal [boolean]

    @SerializedName("reply_url")
    @Expose
    private String reply_url; // add a reply link to each comment and use this URL [string]

    @SerializedName("content")
    @Expose
    private String content; //  comment content [string]



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_human() {
        return date_human;
    }

    public void setDate_human(String date_human) {
        this.date_human = date_human;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getRecently_posted() {
        return recently_posted;
    }

    public void setRecently_posted(Boolean recently_posted) {
        this.recently_posted = recently_posted;
    }

    public Boolean getPosted_by_owner() {
        return posted_by_owner;
    }

    public void setPosted_by_owner(Boolean posted_by_owner) {
        this.posted_by_owner = posted_by_owner;
    }

    public String getReply_url() {
        return reply_url;
    }

    public void setReply_url(String reply_url) {
        this.reply_url = reply_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHashProposal() {
        return hashProposal;
    }

    public void setHashProposal(String hashProposal) {
        this.hashProposal = hashProposal;
    }
}
