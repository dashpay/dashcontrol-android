package com.dash.dashapp.Model;

import java.util.ArrayList;

/**
 * Created by sebas on 12/2/2017.
 */

public class Exchange {
    private String name;
    private ArrayList<Market> listCurrencies;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Market> getListCurrencies() {
        return listCurrencies;
    }

    public void setListCurrencies(ArrayList<Market> listCurrencies) {
        this.listCurrencies = listCurrencies;
    }
}
