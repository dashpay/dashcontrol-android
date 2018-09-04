package com.dash.dashapp.ui.walletutils

import android.arch.lifecycle.LiveData
import org.bitcoinj.core.Block
import org.bitcoinj.core.FilteredBlock
import org.bitcoinj.core.Peer
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener
import org.bitcoinj.utils.Threading

class BlocksDownloadedLiveData(private val peerGroup: PeerGroup) :
        LiveData<Pair<Block, Int>>(), BlocksDownloadedEventListener {

    override fun onActive() {
        peerGroup.addBlocksDownloadedEventListener(Threading.SAME_THREAD, this)
    }

    override fun onInactive() {
        peerGroup.removeBlocksDownloadedEventListener(this)
    }

    override fun onBlocksDownloaded(peer: Peer, block: Block, filteredBlock: FilteredBlock?, blocksLeft: Int) {
        postValue(Pair(block, blocksLeft))
    }
}
