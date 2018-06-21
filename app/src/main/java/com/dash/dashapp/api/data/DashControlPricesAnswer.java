package com.dash.dashapp.api.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashSet;
import java.util.Set;

public class DashControlPricesAnswer {

    @SerializedName("intl")
    private JsonObject intlExchanges;

    public Set<DashControlExchange> getIntlExchanges() {
        Set<String> exchangeNames = intlExchanges.keySet();
        Set<DashControlExchange> exchanges = new LinkedHashSet<>();
        for (String name : exchangeNames) {
            JsonObject exchangeData = intlExchanges.getAsJsonObject(name);
            DashControlExchange dcExchange = DashControlExchange.create(name, exchangeData);
            exchanges.add(dcExchange);
        }
        return exchanges;
    }
}
