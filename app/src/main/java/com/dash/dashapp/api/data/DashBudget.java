package com.dash.dashapp.api.data;

import com.dash.dashapp.models.BudgetSummary;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

public class DashBudget extends RealmObject
        implements BudgetSummary.Convertible {

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


    @Override
    public BudgetSummary convert() {
        BudgetSummary budgetSummary = new BudgetSummary();
        budgetSummary.totalAmount = totalAmount;
        budgetSummary.allotedAmount = allotedAmount;
        budgetSummary.paymentDate = paymentDate;
        budgetSummary.paymentDateHuman = paymentDateHuman;
        budgetSummary.superblock = superblock;
        return budgetSummary;
    }
}
