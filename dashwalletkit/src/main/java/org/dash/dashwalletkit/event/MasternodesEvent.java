package org.dash.dashwalletkit.event;

public class MasternodesEvent {

    private int newCount;

    public MasternodesEvent(int newCount) {
        this.newCount = newCount;
    }

    public int getNewCount() {
        return newCount;
    }
}
