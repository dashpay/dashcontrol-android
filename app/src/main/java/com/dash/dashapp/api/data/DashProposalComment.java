package com.dash.dashapp.api.data;

import com.dash.dashapp.models.BudgetProposalComment;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

public class DashProposalComment extends RealmObject
        implements BudgetProposalComment.Convertible {

    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("date")
    private Date date;

    @SerializedName("date_human")
    private String dateHuman;

    @SerializedName("order")
    private int order;

    @SerializedName("level")
    private String level;

    @SerializedName("recently_posted")
    private boolean recentlyPosted;

    @SerializedName("posted_by_owner")
    private boolean postedByOwner;

    @SerializedName("reply_url")
    private String replyUrl;

    @SerializedName("content")
    private String content;

    @Override
    public BudgetProposalComment convert() {
        BudgetProposalComment proposalComment = new BudgetProposalComment();
        proposalComment.id = id;
        proposalComment.username = username;
        proposalComment.date = date;
        proposalComment.dateHuman = dateHuman;
        proposalComment.order = order;
        proposalComment.level = level;
        proposalComment.replyUrl = replyUrl;
        proposalComment.content = content;
        return proposalComment;
    }
}
