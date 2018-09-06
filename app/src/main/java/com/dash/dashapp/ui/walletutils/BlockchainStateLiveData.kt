package com.dash.dashapp.ui.walletutils

import android.app.Application
import org.dash.dashwalletkit.data.BlockchainState
import org.dash.dashwalletkit.event.BlockchainStateEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class BlockchainStateLiveData(application: Application) : WalletAppKitServiceLiveData<BlockchainState>(application) {

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPeerListUpdateEvent(event: BlockchainStateEvent) {
        walletAppKitService?.blockchainState?.let {
            value = event.blockchainState
        }
    }

    override fun updateValue() {
        walletAppKitService?.blockchainState?.let {
            value = it
        }
    }
}
