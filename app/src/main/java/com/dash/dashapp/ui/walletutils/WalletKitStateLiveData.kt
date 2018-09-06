package com.dash.dashapp.ui.walletutils

import android.app.Application
import org.bitcoinj.wallet.Wallet
import org.dash.dashwalletkit.event.KitSetupCompleteEvent
import org.dash.dashwalletkit.event.MasternodeSyncEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WalletKitStateLiveData(application: Application) : WalletAppKitServiceLiveData<Wallet>(application) {

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onKitSetupCompleteEvent(event: KitSetupCompleteEvent) {
        updateValue()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMasternodeSyncEvent(event: MasternodeSyncEvent) {
        updateValue()
    }

    override fun updateValue() {
        walletAppKitService?.wallet?.let {
            value = it
        }
    }
}
