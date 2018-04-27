package com.dash.dashapp.models;

import java.util.List;

/**
 * Created by sebas on 12/2/2017.
 */

public class Exchange {

    private String name;
    private List<Market> listMarket;

    public Exchange(String name, List<Market> listCurrencies) {
        this.name = name;
        this.listMarket = listCurrencies;
    }

    public Exchange() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Market> getListMarket() {
        return listMarket;
    }

    public void setListMarket(List<Market> listCurrencies) {
        this.listMarket = listCurrencies;
    }
}
