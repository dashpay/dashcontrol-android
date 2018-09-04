package com.dash.dashapp.ui.walletutils

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.dash.dashapp.events.PeerStateEvent
import com.dash.dashapp.service.WalletAppKitService
import org.bitcoinj.core.Peer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class PeersLiveData(private val application: Application) : LiveData<List<Peer>>(), ServiceConnection {

    private var walletAppKitService: WalletAppKitService? = null

    override fun onActive() {
        application.bindService(Intent(application, WalletAppKitService::class.java), this, Context.BIND_AUTO_CREATE)
        EventBus.getDefault().register(this)
    }

    override fun onInactive() {
        application.unbindService(this)
        EventBus.getDefault().unregister(this)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        walletAppKitService = (service as WalletAppKitService.LocalBinder).service
        updateValue()
    }

    override fun onServiceDisconnected(name: ComponentName) {
        walletAppKitService = null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPeerListUpdateEvent(event: PeerStateEvent) {
        updateValue()
    }

    private fun updateValue() {
        walletAppKitService?.connectedPeers?.let {
            value = it
        }
    }
}
