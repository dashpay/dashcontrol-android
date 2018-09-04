package com.dash.dashapp.events;

import com.dash.dashapp.service.BlockchainState;

public class BlockchainStateEvent {

    private BlockchainState blockchainState;

    public BlockchainStateEvent(BlockchainState blockchainState) {
        this.blockchainState = blockchainState;
    }

    public BlockchainState getBlockchainState() {
        return blockchainState;
    }

    public boolean hasData() {
        return blockchainState != null;
    }
}
