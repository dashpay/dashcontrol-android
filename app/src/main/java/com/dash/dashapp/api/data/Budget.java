package com.dash.dashapp.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Budget {

    @SerializedName("total_amount")
    public String totalAmount;

    @SerializedName("allotedAmount")
    public String allotedAmount;

    @SerializedName("payment_date")
    public Date paymentDate;

    @SerializedName("payment_date_human")
    public String paymentDateHuman;

    @SerializedName("superblock")
    public int superblock;
}
