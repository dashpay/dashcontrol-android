package com.dash.dashapp.models;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Exchange extends RealmObject {

    public String name;
    public RealmList<Market> markets;

    public interface Convertible {
        Exchange convert();
    }

    @Override
    public String toString() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
