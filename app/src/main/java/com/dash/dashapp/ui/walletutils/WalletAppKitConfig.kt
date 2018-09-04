package com.dash.dashapp.ui.walletutils

import android.content.ContextWrapper

import org.bitcoinj.core.NetworkParameters

import java.io.InputStream

interface WalletAppKitConfig {

    val networkParams: NetworkParameters

    val filesPrefix: String

    fun getCheckpoints(context: ContextWrapper): InputStream
}
