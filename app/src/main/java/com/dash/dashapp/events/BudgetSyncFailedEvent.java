package com.dash.dashapp.events;

public class BudgetSyncFailedEvent {

    private Throwable cause;

    public BudgetSyncFailedEvent(Throwable cause) {
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
