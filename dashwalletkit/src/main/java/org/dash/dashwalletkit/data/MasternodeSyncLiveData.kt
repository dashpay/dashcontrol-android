package org.dash.dashwalletkit.data

import android.arch.lifecycle.LiveData
import org.bitcoinj.core.MasternodeSync
import org.bitcoinj.core.MasternodeSyncListener
import org.bitcoinj.utils.Threading

class MasternodeSyncLiveData(private val masternodeSync: MasternodeSync) : LiveData<Pair<Int, Double>>(), MasternodeSyncListener {

    override fun onSyncStatusChanged(newStatus: Int, syncStatus: Double) {
        postValue(Pair(newStatus, syncStatus))
    }

    override fun onActive() {
        masternodeSync.addEventListener(this, Threading.SAME_THREAD)
    }

    override fun onInactive() {
        masternodeSync.removeEventListener(this)
    }
}
