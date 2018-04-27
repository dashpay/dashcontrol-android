package com.dash.dashapp.models;

import java.io.Serializable;
import java.util.Date;

public class BudgetSummary implements Serializable {

    public float totalAmount;
    public float allotedAmount;
    public Date paymentDate;
    public String paymentDateHuman;
    public int superblock;

    public interface Convertible {
        BudgetSummary convert();
    }
}