package com.dash.dashapp.api.data;

import com.dash.dashapp.models.BudgetSummary;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DashBudget {

    @SerializedName("total_amount")
    private float totalAmount;

    @SerializedName("alloted_amount")
    private float allotedAmount;

    @SerializedName("payment_date")
    private Date paymentDate;

    @SerializedName("payment_date_human")
    private String paymentDateHuman;

    @SerializedName("superblock")
    private int superblock;

    public BudgetSummary convert() {
        BudgetSummary budgetSummary = new BudgetSummary();
        budgetSummary.setTotalAmount(totalAmount);
        budgetSummary.setAllotedAmount(allotedAmount);
        budgetSummary.setPaymentDate(paymentDate);
        budgetSummary.setPaymentDateHuman(paymentDateHuman);
        budgetSummary.setSuperblock(superblock);
        return budgetSummary;
    }
}
