package org.dash.dashwalletkit.data

import android.arch.lifecycle.LiveData

import org.bitcoinj.core.Coin
import org.bitcoinj.core.Transaction
import org.bitcoinj.utils.Threading
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener

class NewTransactionLiveData(private val wallet: Wallet) :
        LiveData<Transaction>(), WalletCoinsReceivedEventListener, WalletCoinsSentEventListener {

    override fun onActive() {
        wallet.addCoinsReceivedEventListener(Threading.SAME_THREAD, this)
        wallet.addCoinsSentEventListener(Threading.SAME_THREAD, this)
    }

    override fun onInactive() {
        wallet.removeCoinsSentEventListener(this)
        wallet.removeCoinsReceivedEventListener(this)
    }

    override fun onCoinsReceived(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) {
        postValue(tx)
    }

    override fun onCoinsSent(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) {
        postValue(tx)
    }
}