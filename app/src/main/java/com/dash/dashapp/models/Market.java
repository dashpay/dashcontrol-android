package com.dash.dashapp.models;

import io.realm.RealmObject;

public class Market extends RealmObject {

    public String name;
    public double price;
    public boolean isDefault;

    public interface Convertible {
        Market convert();
    }

    @Override
    public String toString() {
        return name.replace("_", "/");
    }
}
