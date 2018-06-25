package com.dash.dashapp.events;

public class NewsSyncFailedEvent {

    private Throwable cause;

    public NewsSyncFailedEvent(Throwable cause) {
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
