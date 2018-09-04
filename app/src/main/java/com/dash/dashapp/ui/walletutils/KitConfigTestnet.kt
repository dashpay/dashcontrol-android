package com.dash.dashapp.ui.walletutils

import android.content.ContextWrapper

import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.TestNet3Params

import java.io.IOException
import java.io.InputStream

class KitConfigTestnet : WalletAppKitConfig {

    override val networkParams: NetworkParameters
        get() = TestNet3Params.get()

    override val filesPrefix: String
        get() = "testnet"


    override fun getCheckpoints(context: ContextWrapper): InputStream {
        try {
            return context.assets.open("checkpoints-testnet.txt")
        } catch (e: IOException) {
            throw IllegalStateException()
        }

    }
}
