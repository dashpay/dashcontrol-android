package com.dash.dashapp.api.data;

import com.dash.dashapp.models.Exchange;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.realm.RealmList;

public class DashControlExchange implements Exchange.Convertible {

    private String name;
    private List<DashControlMarket> markets;

    private static List<DashControlMarket> initMarkets(JsonObject exchangeData) {
        Set<String> marketNames = exchangeData.keySet();
        List<DashControlMarket> markets = new ArrayList<>();
        for (String pair : marketNames) {
            JsonPrimitive price = exchangeData.getAsJsonPrimitive(pair);
            DashControlMarket dcMarket = DashControlMarket.create(pair, price.getAsDouble());
            markets.add(dcMarket);
        }
        return markets;
    }

    @Override
    public Exchange convert() {
        Exchange exchange = new Exchange();
        exchange.name = name;
        exchange.markets = new RealmList<>();
        for (DashControlMarket dcMarket : markets) {
            exchange.markets.add(dcMarket.convert());
        }
        return exchange;
    }

    public static DashControlExchange create(String name, JsonObject exchangeData) {
        DashControlExchange dashControlExchange = new DashControlExchange();
        dashControlExchange.name = name;
        dashControlExchange.markets = initMarkets(exchangeData);
        return dashControlExchange;
    }
}