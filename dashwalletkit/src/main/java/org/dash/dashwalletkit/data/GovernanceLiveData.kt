package org.dash.dashwalletkit.data

import android.arch.lifecycle.LiveData
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.governance.GovernanceManager
import org.bitcoinj.governance.GovernanceObject
import org.bitcoinj.governance.listeners.GovernanceManagerListener
import org.bitcoinj.utils.Threading

class GovernanceLiveData(private val governanceManager: GovernanceManager) :
        LiveData<Pair<Sha256Hash, GovernanceObject>>(), GovernanceManagerListener {

    override fun onActive() {
        governanceManager.addEventListener(this, Threading.SAME_THREAD)
    }

    override fun onInactive() {
        governanceManager.removeEventListener(this)
    }

    override fun onGovernanceObjectAdded(nHash: Sha256Hash, governanceObject: GovernanceObject) {
        postValue(Pair(nHash, governanceObject))
    }
}
