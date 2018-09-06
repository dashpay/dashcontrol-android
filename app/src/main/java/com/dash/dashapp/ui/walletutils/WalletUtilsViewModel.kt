package com.dash.dashapp.ui.walletutils

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.Peer
import org.bitcoinj.governance.GovernanceObject
import org.dash.dashwalletkit.WalletAppKitService
import org.dash.dashwalletkit.data.BlockchainState

class WalletUtilsViewModel(application: Application) : AndroidViewModel(application), ServiceConnection {

    private var _walletAppKitService: WalletAppKitService? = null

    init {
        application.bindService(Intent(application, WalletAppKitService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        _walletAppKitService = (service as WalletAppKitService.LocalBinder).service
    }

    override fun onServiceDisconnected(name: ComponentName) {
        _walletAppKitService = null
    }

    private val _peerList = PeersLiveData(application)
    val peerList: LiveData<List<Peer>>
        get() = _peerList

    private val _blockchainState = BlockchainStateLiveData(application)
    val blockchainState: LiveData<BlockchainState>
        get() = _blockchainState

    private val _masternodes = MasternodeListLiveData(application)
    val masternodes: LiveData<List<Masternode>>
        get() = _masternodes

    private val _governanceObjects = GovernanceListLiveData(application)
    val governanceObjects: LiveData<List<GovernanceObject>>
        get() = _governanceObjects

}
