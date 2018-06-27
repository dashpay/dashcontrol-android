package com.dash.dashapp.models;

import java.util.Date;

import io.realm.RealmObject;

public class BudgetSummary extends RealmObject {

    private float totalAmount;
    private float allotedAmount;
    private Date paymentDate;
    private String paymentDateHuman;
    private int superblock;

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getAllotedAmount() {
        return allotedAmount;
    }

    public void setAllotedAmount(float allotedAmount) {
        this.allotedAmount = allotedAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentDateHuman() {
        return paymentDateHuman;
    }

    public void setPaymentDateHuman(String paymentDateHuman) {
        this.paymentDateHuman = paymentDateHuman;
    }

    public int getSuperblock() {
        return superblock;
    }

    public void setSuperblock(int superblock) {
        this.superblock = superblock;
    }
}