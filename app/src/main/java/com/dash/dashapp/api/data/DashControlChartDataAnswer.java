package com.dash.dashapp.api.data;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashControlChartDataAnswer {

    @SerializedName("files")
    public JsonArray files;

    @SerializedName("records")
    private List<ChartRecord> records;

    public List<ChartRecord> getRecords() {
        return records;
    }
}
