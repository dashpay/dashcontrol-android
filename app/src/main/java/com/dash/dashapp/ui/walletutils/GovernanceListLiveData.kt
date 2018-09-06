package com.dash.dashapp.ui.walletutils

import android.app.Application
import org.bitcoinj.governance.GovernanceObject
import org.dash.dashwalletkit.event.GovernanceEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GovernanceListLiveData(application: Application) : WalletAppKitServiceLiveData<List<GovernanceObject>>(application) {

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGovernanceEvent(event: GovernanceEvent) {
        updateValue()
    }

    override fun updateValue() {
        walletAppKitService?.governanceObjects?.let {
            value = it
        }
    }
}
