package com.dash.dashapp.ui.walletutils

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import org.bitcoinj.core.Peer
import org.dash.dashwalletkit.data.BlockchainState
import org.dash.dashwalletkit.data.BlockchainStateLiveData

class WalletUtilsViewModel(application: Application) : AndroidViewModel(application) {

    private val _peerList = PeersLiveData(application)
    val peerList: LiveData<List<Peer>>
        get() = _peerList

    private val _blockchainState = BlockchainStateLiveData(application)
    val blockchainState: LiveData<BlockchainState>
        get() = _blockchainState
}
