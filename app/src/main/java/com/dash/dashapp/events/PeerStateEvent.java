package com.dash.dashapp.events;

public class PeerStateEvent {

    private int numPeers;

    public PeerStateEvent(int numPeers) {
        this.numPeers = numPeers;
    }

    public int getNumPeers() {
        return numPeers;
    }
}
