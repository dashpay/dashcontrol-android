package org.dash.dashwalletkit.event;

import org.dash.dashwalletkit.data.BlockchainState;

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
