package com.dash.dashapp.ui.walletutils

import android.app.Application
import org.bitcoinj.core.Masternode
import org.dash.dashwalletkit.event.MasternodesEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MasternodeListLiveData(application: Application) : WalletAppKitServiceLiveData<List<Masternode>>(application) {

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMasternodesEvent(event: MasternodesEvent) {
        updateValue()
    }

    override fun updateValue() {
        walletAppKitService?.masternodes?.let {
            value = it
        }
    }
}
