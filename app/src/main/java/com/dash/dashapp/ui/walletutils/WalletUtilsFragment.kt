package com.dash.dashapp.ui.walletutils

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dash.dashapp.R
import com.dash.dashapp.utils.DateUtil
import kotlinx.android.synthetic.main.wallet_utils_fragment.view.*
import java.util.*


class WalletUtilsFragment : Fragment() {

    companion object {
        private val TAG = WalletUtilsFragment::class.java.canonicalName
        fun newInstance() = WalletUtilsFragment()
    }

    private lateinit var layoutView: View
    private lateinit var viewModel: WalletUtilsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        layoutView = inflater.inflate(R.layout.wallet_utils_fragment, container, false)
        return layoutView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WalletUtilsViewModel::class.java)

        viewModel.walletInfoLiveData.observe(this, Observer { walletInfo ->
            layoutView.message0.text = "networkParameters: ${walletInfo!!.networkParameters.javaClass.simpleName}\nbalance: ${walletInfo.balance.toFriendlyString()}\naddress: ${walletInfo.currentReceiveAddress}\n"
            layoutView.message0.setOnClickListener {
                Log.d(TAG, "currentReceiveAddress: ${walletInfo.currentReceiveAddress}")
            }
        })
        viewModel.peerConnectivity.observe(this, Observer { peerList ->
            layoutView.message1.text = "peers: ${peerList!!.size}\n${peerList}\n"
        })
        viewModel.blockchainState.observe(this, Observer { blockchainState ->
            blockchainState?.let {
                layoutView.message2.text = "bestChainDate: ${DateUtil.format(it.bestChainDate)}\nbestChainHeight: ${it.bestChainHeight}\nblocksLeft: ${it.blocksLeft}\n"
            }
        })
        viewModel.masternodeSync.observe(this, Observer { data ->
            val status = data!!.first.name.replace("MASTERNODE", "MN")
            layoutView.message3.text = "${layoutView.message3.text}${DateUtil.format(Date())} $status\n"
        })
        viewModel.masternodes.observe(this, Observer { masternodes ->
            masternodes?.let {
                layoutView.message4.text = "masternodes: ${it.size}"//\n$masternodes\n"
            }
        })
        viewModel.governanceObjects.observe(this, Observer { governanceObjects ->
            governanceObjects?.let {
                layoutView.message5.text = "governanceObjects: ${it.size}\n"//\governanceObjects\n"
            }
        })
    }
}
