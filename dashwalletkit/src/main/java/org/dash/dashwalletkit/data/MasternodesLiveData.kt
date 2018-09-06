package org.dash.dashwalletkit.data

import android.arch.lifecycle.LiveData
import org.bitcoinj.core.MasternodeManager
import org.bitcoinj.core.MasternodeManagerListener
import org.bitcoinj.utils.Threading

class MasternodesLiveData(private val masternodeManager: MasternodeManager) :
        LiveData<Int>(), MasternodeManagerListener {

    override fun onActive() {
        masternodeManager.addEventListener(this, Threading.SAME_THREAD)
    }

    override fun onInactive() {
        masternodeManager.removeEventListener(this)
    }

    override fun onMasternodeCountChanged(newCount: Int) {
        postValue(newCount)
    }
}
