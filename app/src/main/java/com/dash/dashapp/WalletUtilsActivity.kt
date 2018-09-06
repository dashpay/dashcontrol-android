package com.dash.dashapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dash.dashapp.ui.walletutils.WalletUtilsFragment

class WalletUtilsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wallet_utils_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, WalletUtilsFragment.newInstance())
                    .commitNow()
        }
    }
}
