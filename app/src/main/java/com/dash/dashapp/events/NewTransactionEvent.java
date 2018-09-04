package com.dash.dashapp.events;

import org.bitcoinj.core.Transaction;

public class NewTransactionEvent {

    private Transaction transaction;

    public NewTransactionEvent(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public boolean hasData() {
        return transaction != null;
    }
}
