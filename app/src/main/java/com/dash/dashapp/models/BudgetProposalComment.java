package com.dash.dashapp.models;

import java.io.Serializable;
import java.util.Date;

public class BudgetProposalComment implements Serializable {

    public String id;
    public String username;
    public Date date;
    public String dateHuman;
    public int order;
    public String level;
    public boolean recentlyPosted;
    public boolean postedByOwner;
    public String replyUrl;
    public String content;

    public interface Convertible {
        BudgetProposalComment convert();
    }

    public int getLevelAsInt() {
        return Integer.parseInt(level);
    }
}