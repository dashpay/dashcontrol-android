package com.dash.dashapp.api.data;

import com.google.gson.annotations.SerializedName;

public class InsightResponse {

    @SerializedName("address")
    public String address;

    @SerializedName("txid")
    public String txid;

    @SerializedName("vout")
    public int vout;

    @SerializedName("scriptPubKey")
    public String scriptPubKey;

    @SerializedName("amount")
    public float amount;

    @SerializedName("satoshis")
    public int satoshis;

    @SerializedName("height")
    public int height;

    @SerializedName("confirmations")
    public int confirmations;
}
