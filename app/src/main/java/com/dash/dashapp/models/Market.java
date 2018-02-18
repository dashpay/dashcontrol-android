package com.dash.dashapp.models;

/**
 * Created by sebas on 12/2/2017.
 */

public class Market {
    private String name;
    private double price;
    private int isDefault;

    public Market(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Market() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
