package com.dash.dashapp.api.data;

import com.dash.dashapp.models.Market;

import io.realm.RealmObject;

public class DashControlMarket
        extends RealmObject implements Market.Convertible {

    private String name;
    private double price;

    @Override
    public Market convert() {
        Market market = new Market();
        market.name = name;
        market.price = price;
        return market;
    }

    public static DashControlMarket create(String name, double price) {
        DashControlMarket dashControlMarket = new DashControlMarket();
        dashControlMarket.name = name;
        dashControlMarket.price = price;
        return dashControlMarket;
    }
}