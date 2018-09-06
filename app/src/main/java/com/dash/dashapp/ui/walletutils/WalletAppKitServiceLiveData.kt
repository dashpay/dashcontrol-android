package com.dash.dashapp.ui.walletutils

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.dash.dashwalletkit.WalletAppKitService
import org.greenrobot.eventbus.EventBus

open class WalletAppKitServiceLiveData<T>(private val application: Application) : LiveData<T>(), ServiceConnection {

    private var _walletAppKitService: WalletAppKitService? = null
    protected val walletAppKitService: WalletAppKitService?
        get() = _walletAppKitService

    override fun onActive() {
        application.bindService(Intent(application, WalletAppKitService::class.java), this, Context.BIND_AUTO_CREATE)
        EventBus.getDefault().register(this)
    }

    override fun onInactive() {
        application.unbindService(this)
        EventBus.getDefault().unregister(this)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        _walletAppKitService = (service as WalletAppKitService.LocalBinder).service
        updateValue()
    }

    override fun onServiceDisconnected(name: ComponentName) {
        _walletAppKitService = null
    }

    protected open fun updateValue() {}
}
