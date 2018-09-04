package com.dash.dashapp.service;

import java.util.Date;

public class BlockchainState {

    private final Date bestChainDate;
    private final int bestChainHeight;

    public BlockchainState(final Date bestChainDate, final int bestChainHeight) {
        this.bestChainDate = bestChainDate;
        this.bestChainHeight = bestChainHeight;
    }

    public Date getBestChainDate() {
        return bestChainDate;
    }

    public int getBestChainHeight() {
        return bestChainHeight;
    }
}
