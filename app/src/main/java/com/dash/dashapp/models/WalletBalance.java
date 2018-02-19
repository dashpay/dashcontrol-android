package com.dash.dashapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dexter Barretto on 18/2/18.
 * Github : @dbarretto
 */

public class WalletBalance {


    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("txid")
    @Expose
    private String transactionId;
    @SerializedName("vout")
    @Expose
    private Integer vout;
    @SerializedName("scriptPubKey")
    @Expose
    private String scriptPubKey;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("satoshis")
    @Expose
    private Integer satoshis;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("confirmations")
    @Expose
    private Integer confirmations;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getVout() {
        return vout;
    }

    public void setVout(Integer vout) {
        this.vout = vout;
    }

    public String getScriptPubKey() {
        return scriptPubKey;
    }

    public void setScriptPubKey(String scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getSatoshis() {
        return satoshis;
    }

    public void setSatoshis(Integer satoshis) {
        this.satoshis = satoshis;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Integer confirmations) {
        this.confirmations = confirmations;
    }

}


