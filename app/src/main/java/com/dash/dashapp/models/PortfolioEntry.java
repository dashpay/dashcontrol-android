package com.dash.dashapp.models;

import java.io.Serializable;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PortfolioEntry extends RealmObject implements Serializable {

    public interface Field {
        String ID = "id";
        String PUB_KEY = "pubKey";
        String TYPE = "type";
    }

    @PrimaryKey
    public String id = UUID.randomUUID().toString();

    public String pubKey;

    public String votingKey;

    public String label;

    public long balance;

    public boolean includeEarnings;

    private String type;

    public PortfolioEntry() {

    }

    public PortfolioEntry(Type type) {
        this.type = type.toString();
    }

    public void saveType(Type type) {
        this.type = type.toString();
    }

    public Type getType() {
        return (type != null) ? Type.valueOf(type) : null;
    }

    public enum Type {
        WALLET,
        MASTERNODE
    }
}
