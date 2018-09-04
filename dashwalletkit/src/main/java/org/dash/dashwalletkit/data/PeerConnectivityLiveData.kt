package org.dash.dashwalletkit.data

import android.arch.lifecycle.LiveData
import org.bitcoinj.core.Peer
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.core.listeners.PeerConnectedEventListener
import org.bitcoinj.core.listeners.PeerDisconnectedEventListener
import org.bitcoinj.utils.Threading

class PeerConnectivityLiveData(private val peerGroup: PeerGroup) :
        LiveData<Pair<Peer, Int>>(), PeerConnectedEventListener, PeerDisconnectedEventListener {

    override fun onActive() {
        peerGroup.addConnectedEventListener(Threading.SAME_THREAD, this)
        peerGroup.addDisconnectedEventListener(Threading.SAME_THREAD, this)
    }

    override fun onInactive() {
        peerGroup.removeConnectedEventListener(this)
        peerGroup.removeDisconnectedEventListener(this)
    }

    override fun onPeerConnected(peer: Peer, peerCount: Int) {
        postValue(Pair(peer, peerCount))
    }

    override fun onPeerDisconnected(peer: Peer, peerCount: Int) {
        postValue(Pair(peer, peerCount))
    }
}
