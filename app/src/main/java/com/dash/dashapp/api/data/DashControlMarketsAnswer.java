package com.dash.dashapp.api.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DashControlMarketsAnswer {

    @SerializedName("markets")
    private JsonObject markets;

    @SerializedName("default")
    public DefaultMarket defaultMarket;

    public Map<String, Set<String>> getByExchange() {
        Set<String> marketNames = markets.keySet();
        Map<String, Set<String>> exchanges = new LinkedHashMap<>();
        for (String market : marketNames) {
            JsonArray marketExchanges = markets.getAsJsonArray(market);
            for (int i = 0; i < marketExchanges.size(); i++) {
                String exchange = marketExchanges.get(i).getAsString();
                if (!exchanges.containsKey(exchange)) {
                    exchanges.put(exchange, new LinkedHashSet<String>());
                }
                exchanges.get(exchange).add(market);
            }
        }
        return exchanges;
    }

    public static class DefaultMarket {

        @SerializedName("exchange")
        public String exchange;

        @SerializedName("market")
        public String market;
    }
}
