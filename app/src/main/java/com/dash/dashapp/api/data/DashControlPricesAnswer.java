package com.dash.dashapp.api.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashSet;
import java.util.Set;

public class DashControlPricesAnswer {

    public static final String DASH_BTC_PAIR = "DASH_BTC";
    public static final String DASH_USD_PAIR = "DASH_USD";
    public static final String DASH_USDT_PAIR = "DASH_USDT";

    @SerializedName("intl")
    private JsonObject intlExchanges;

    public Set<DashControlExchange> getIntlExchanges() {
        Set<String> exchangeNames = intlExchanges.keySet();
        Set<DashControlExchange> exchanges = new LinkedHashSet<>();
        for (String name : exchangeNames) {
            JsonObject exchangeData = intlExchanges.getAsJsonObject(name);
            DashControlExchange dcExchange = new DashControlExchange(name, exchangeData);
            exchanges.add(dcExchange);
        }
        return exchanges;
    }

    public static class DashControlExchange {

        private JsonObject exchangeData;
        private String name;

        private DashControlExchange(String name, JsonObject exchangeData) {
            this.name = name;
            this.exchangeData = exchangeData;
        }

        public String getName() {
            return name;
        }

        public Set<DashControlMarket> getMarkets() {
            Set<String> marketNames = exchangeData.keySet();
            Set<DashControlMarket> markets = new LinkedHashSet<>();
            for (String pair : marketNames) {
                JsonPrimitive price = exchangeData.getAsJsonPrimitive(pair);
                DashControlMarket dcMarket = new DashControlMarket(pair, price.getAsDouble());
                markets.add(dcMarket);
            }
            return markets;
        }
    }

    public static class DashControlMarket {

        private String name;
        private double price;

        private DashControlMarket(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }
}
