package com.dash.dashapp.api.data;

import com.google.gson.annotations.SerializedName;

public class BudgetApiAnswer {

    @SerializedName("status")
    public String status;

    @SerializedName("error_type")
    public String errorType;
}
