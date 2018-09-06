package org.dash.dashwalletkit

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.wallet.Wallet
import org.dash.dashwalletkit.config.KitConfigTestnet
import org.dash.dashwalletkit.config.SimpleLogFormatter
import org.dash.dashwalletkit.config.WalletAppKitConfig
import org.dash.dashwalletkit.data.BlockchainState


class WalletAppKitService : Service() {

    companion object {
        private val TAG = WalletAppKitService::class.java.canonicalName
        private const val WALLET_APP_KI_TDIR = "walletappkit"
        private const val MIN_BROADCAST_CONNECTIONS = 2
        private const val MAX_CONNECTIONS = 14
    }

    private lateinit var kit: WalletAppKit
    private lateinit var kitConfig: WalletAppKitConfig
    private val mBinder = LocalBinder()
    private var isSetupComplete = false
    private val onSetupCompleteListeners = mutableListOf<OnSetupCompleteListener>()

    val blockchainState: BlockchainState?
        get() = kit.chain()?.let {
            val chainHead = kit.chain().chainHead
            val bestChainDate = chainHead.header.time
            val bestChainHeight = chainHead.height
            val blocksLeft = kit.peerGroup().mostCommonChainHeight - chainHead.height

            return BlockchainState(bestChainDate, bestChainHeight, blocksLeft)
        }

    val wallet: Wallet
        get() = kit.wallet()

    val peerGroup: PeerGroup
        get() = kit.peerGroup()

    inner class LocalBinder : Binder() {
        val service: WalletAppKitService
            get() = this@WalletAppKitService
    }

    override fun onCreate() {
        super.onCreate()
        SimpleLogFormatter.init(this)

        kitConfig = KitConfigTestnet()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        initWalletAppKit()
        return Service.START_NOT_STICKY
    }

    private fun initWalletAppKit() {
        val walletAppKitDir = application.getDir(WALLET_APP_KI_TDIR, Context.MODE_PRIVATE)
        kit = object : WalletAppKit(kitConfig.networkParams, walletAppKitDir, kitConfig.filesPrefix, false) {
            override fun onSetupCompleted() {
                this@WalletAppKitService.onSetupCompleted()
            }
        }
        kit.startAsync()
    }

    private fun onSetupCompleted() {
        isSetupComplete = true

        kit.peerGroup().minBroadcastConnections = MIN_BROADCAST_CONNECTIONS
        kit.peerGroup().maxConnections = MAX_CONNECTIONS

        kit.wallet().let {
            if (it.keyChainGroupSize < 1) {
                it.importKey(ECKey())
            }
        }

        kitConfig.getCheckpoints(this)?.let {
            kit.setCheckpoints(it)
        }

        notifyOnSetupCompletedListeners()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    fun registerListener(listener: OnSetupCompleteListener) {
        onSetupCompleteListeners.add(listener)
        if (isSetupComplete) {
            listener.onServiceSetupComplete()
        }
    }

    fun unregisterListener(listener: OnSetupCompleteListener) {
        onSetupCompleteListeners.remove(listener)
    }

    private fun notifyOnSetupCompletedListeners() {
        onSetupCompleteListeners.forEach {
            it.onServiceSetupComplete()
        }
    }

    override fun onDestroy() {
        onSetupCompleteListeners.clear()
        kit.stopAsync()
        super.onDestroy()
    }

    interface OnSetupCompleteListener {
        fun onServiceSetupComplete()
    }
}
