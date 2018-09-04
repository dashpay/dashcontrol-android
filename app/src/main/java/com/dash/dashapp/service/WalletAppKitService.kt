package com.dash.dashapp.service

import android.app.Service
import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.dash.dashapp.events.BlockchainStateEvent
import com.dash.dashapp.events.NewTransactionEvent
import com.dash.dashapp.events.PeerStateEvent
import com.dash.dashapp.helpers.SimpleLogFormatter
import com.dash.dashapp.ui.walletutils.*
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Peer
import org.bitcoinj.core.Transaction
import org.bitcoinj.kits.WalletAppKit
import org.greenrobot.eventbus.EventBus

class WalletAppKitService : LifecycleService() {

    companion object {
        private val TAG = WalletAppKitService::class.java.canonicalName
    }

    private lateinit var kit: WalletAppKit
    private lateinit var kitConfig: WalletAppKitConfig
    private lateinit var eventBus: EventBus

    private lateinit var peerConnectivityLiveData: PeerConnectivityLiveData
    private lateinit var blocksDownloadedLiveData: BlocksDownloadedLiveData
    private lateinit var newTransactionLiveData: NewTransactionLiveData

    private val mBinder = LocalBinder()

    val connectedPeers: List<Peer>?
        get() = kit.peerGroup()?.connectedPeers

    val blockchainState: BlockchainState?
        get() = kit.chain()?.let {
            val chainHead = kit.chain().chainHead
            val bestChainDate = chainHead.header.time
            val bestChainHeight = chainHead.height

            return BlockchainState(bestChainDate, bestChainHeight)
        }

    inner class LocalBinder : Binder() {
        val service: WalletAppKitService
            get() = this@WalletAppKitService
    }

    override fun onCreate() {
        super.onCreate()
        SimpleLogFormatter.init(this)
        eventBus = EventBus.getDefault()

        kitConfig = KitConfigMainnet()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        initWalletAppKit()
        return Service.START_NOT_STICKY
    }

    private fun initWalletAppKit() {
        Log.d(TAG, "WalletAppKitService.initWalletAppKit()")

        val walletAppKitDir = application.getDir("walletappkit", Context.MODE_PRIVATE)
        kit = object : WalletAppKit(kitConfig.networkParams, walletAppKitDir, kitConfig.filesPrefix, false) {
            override fun onSetupCompleted() {
                Log.d(TAG, "WalletAppKit.onSetupCompleted()")
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().keyChainGroupSize < 1) {
                    wallet().importKey(ECKey())
                }
                this@WalletAppKitService.onSetupCompleted()
            }
        }

        broadcastPeerState(0)

        // Download the block chain and wait until it's done.
        kit.startAsync()
//        kit.awaitRunning();
    }

    private fun onSetupCompleted() {
        kit.setCheckpoints(kitConfig.getCheckpoints(this))

        bindLiveData()

        val newTransaction = NewTransactionLiveData(kit.wallet())
        newTransaction.observe(this, Observer { Log.d(TAG, "NewTransactionLiveData.onChanged()") })
    }

    private fun bindLiveData() {
        peerConnectivityLiveData = PeerConnectivityLiveData(kit.peerGroup())
        peerConnectivityLiveData.observe(this, Observer { data ->
            broadcastPeerState(data!!.second)
        })

        blocksDownloadedLiveData = BlocksDownloadedLiveData(kit.peerGroup())
        blocksDownloadedLiveData.observe(this, Observer {
            broadcastBlockchainState()
        })

        newTransactionLiveData = NewTransactionLiveData(kit.wallet())
        newTransactionLiveData.observe(this, Observer { data ->
            broadcastNewTransaction(data!!)
        })
    }

    private fun broadcastPeerState(numPeers: Int) {
        if (eventBus.hasSubscriberForEvent(PeerStateEvent::class.java)) {
            eventBus.post(PeerStateEvent(numPeers))
        }
    }

    private fun broadcastBlockchainState() {
        if (eventBus.hasSubscriberForEvent(BlockchainStateEvent::class.java)) {
            eventBus.post(BlockchainStateEvent(blockchainState))
        }
    }

    private fun broadcastNewTransaction(transaction: Transaction) {
        if (eventBus.hasSubscriberForEvent(BlockchainStateEvent::class.java)) {
            eventBus.post(NewTransactionEvent(transaction))
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return mBinder
    }
}
