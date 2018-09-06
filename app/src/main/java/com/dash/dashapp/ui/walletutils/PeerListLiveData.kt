package com.dash.dashapp.ui.walletutils

import android.app.Application
import org.bitcoinj.core.Peer
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class PeersLiveData(application: Application) : WalletAppKitServiceLiveData<List<Peer>>(application) {

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPeerListUpdateEvent(event: org.dash.dashwalletkit.event.PeerStateEvent) {
        updateValue()
    }

    override fun updateValue() {
        walletAppKitService?.connectedPeers?.let {
            value = it
        }
    }
}
