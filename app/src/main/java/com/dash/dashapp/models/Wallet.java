package com.dash.dashapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sebas on 10/26/2017.
 */

public class Wallet {
    @SerializedName("name")
    @Expose
    private String name;

    public Wallet(String wallet) {
        name = wallet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
