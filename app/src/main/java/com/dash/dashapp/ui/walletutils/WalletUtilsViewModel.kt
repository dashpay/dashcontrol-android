package com.dash.dashapp.ui.walletutils

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.dash.dashapp.service.WalletAppKitService
import org.bitcoinj.core.Peer
import java.lang.ref.WeakReference

class WalletUtilsViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var walletAppKitServiceRef: WeakReference<WalletAppKitService>
    private var bound = false

    private val _peerList = MutableLiveData<List<Peer>>()

    val peerList: LiveData<List<Peer>>
        get() = _peerList

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as WalletAppKitService.LocalBinder
            binder.service.setPeerListData(_peerList)
            walletAppKitServiceRef = WeakReference(binder.service)
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            walletAppKitServiceRef.get()?.setPeerListData(null)
            walletAppKitServiceRef.clear()
            bound = false
        }
    }

    init {
        val intent = Intent(getApp(), WalletAppKitService::class.java)
        getApp().bindService(intent, connection, 0)
    }

    override fun onCleared() {
        super.onCleared()
        getApp().unbindService(connection)
        bound = false;
    }

    private fun getApp(): Application {
        return getApplication()
    }
}
