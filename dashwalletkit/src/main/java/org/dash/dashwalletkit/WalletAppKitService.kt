package org.dash.dashwalletkit

import android.app.Service
import android.arch.lifecycle.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.Peer
import org.bitcoinj.governance.GovernanceObject
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.wallet.Wallet
import org.dash.dashwalletkit.config.KitConfigTestnet
import org.dash.dashwalletkit.config.SimpleLogFormatter
import org.dash.dashwalletkit.config.WalletAppKitConfig
import org.dash.dashwalletkit.data.*
import org.dash.dashwalletkit.event.*
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
    private lateinit var masternodeSyncLiveData: MasternodeSyncLiveData
    private lateinit var masternodesLiveData: MasternodesLiveData
    private lateinit var governanceLiveData: GovernanceLiveData
    private lateinit var newTransactionLiveData: NewTransactionLiveData

    private val mBinder = LocalBinder()

    private var isSetupComplete = false

    val connectedPeers: List<Peer>?
        get() = kit.peerGroup()?.connectedPeers

    val blockchainState: BlockchainState?
        get() = kit.chain()?.let {
            val chainHead = kit.chain().chainHead
            val bestChainDate = chainHead.header.time
            val bestChainHeight = chainHead.height
            val blocksLeft = kit.peerGroup().mostCommonChainHeight - chainHead.height

            return BlockchainState(bestChainDate, bestChainHeight, blocksLeft)
        }

    val masternodes: List<Masternode>?
        get() = kit.wallet()?.context?.masternodeManager?.let {
            return it.masternodes
        }

    val governanceObjects: List<GovernanceObject>?
        get() = kit.wallet()?.context?.governanceManager?.let {
            return it.getAllNewerThan(0)
        }

    val wallet: Wallet?
        get() = when {
            isSetupComplete -> kit.wallet()
            else -> null
        }

    inner class LocalBinder : Binder() {
        val service: WalletAppKitService
            get() = this@WalletAppKitService
    }

    override fun onCreate() {
        super.onCreate()
        SimpleLogFormatter.init(this)
        eventBus = EventBus.getDefault()

        kitConfig = KitConfigTestnet()

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                eventBus.removeStickyEvent(KitSetupCompleteEvent::class.java)
                eventBus.removeStickyEvent(MasternodeSyncEvent::class.java)
            }
        })
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
                this@WalletAppKitService.onSetupCompleted()
            }
        }

        postEventIfHasSubscriber(PeerStateEvent(0))

        // Download the block chain and wait until it's done.
        kit.startAsync()
//        kit.awaitRunning();
    }

    private fun onSetupCompleted() {
        isSetupComplete = true
        postEventIfHasSubscriber(KitSetupCompleteEvent())

        kit.wallet().let {
            if (it.keyChainGroupSize < 1) {
                it.importKey(ECKey())
            }
        }

        kitConfig.getCheckpoints(this)?.let {
            kit.setCheckpoints(it)
        }

        bindLiveData()
    }

    private fun bindLiveData() {
        peerConnectivityLiveData = PeerConnectivityLiveData(kit.peerGroup())
        peerConnectivityLiveData.observe(this, Observer {
            postEventIfHasSubscriber(PeerStateEvent(it!!.second))
        })

        blocksDownloadedLiveData = BlocksDownloadedLiveData(kit.peerGroup())
        blocksDownloadedLiveData.observe(this, Observer {
            postEventIfHasSubscriber(BlockchainStateEvent(blockchainState))
        })

        masternodeSyncLiveData = MasternodeSyncLiveData(kit.wallet().context.masternodeSync)
        masternodeSyncLiveData.observe(this, Observer {
            val syncStatus = MasternodeSyncEvent.MasternodeSyncStatus.valueOf(it!!.first)
            postStickyEventIfHasSubscriber(MasternodeSyncEvent(syncStatus, it.second))
        })

        masternodesLiveData = MasternodesLiveData(kit.wallet().context.masternodeManager)
        masternodesLiveData.observe(this, Observer {
            postEventIfHasSubscriber(MasternodesEvent(it!!))
        })

        governanceLiveData = GovernanceLiveData(kit.wallet().context.governanceManager)
        governanceLiveData.observe(this, Observer {
            postEventIfHasSubscriber(GovernanceEvent(it!!.first, it.second))
        })

        newTransactionLiveData = NewTransactionLiveData(kit.wallet())
        newTransactionLiveData.observe(this, Observer {
            postEventIfHasSubscriber(NewTransactionEvent(it))
        })
    }

    private fun postEventIfHasSubscriber(event: Any) {
        if (eventBus.hasSubscriberForEvent(event.javaClass)) {
            eventBus.post(event)
        }
    }

    private fun postStickyEventIfHasSubscriber(event: Any) {
        if (eventBus.hasSubscriberForEvent(event.javaClass)) {
            eventBus.postSticky(event)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return mBinder
    }
}
