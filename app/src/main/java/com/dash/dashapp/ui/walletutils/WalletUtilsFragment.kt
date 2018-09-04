package com.dash.dashapp.ui.walletutils

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dash.dashapp.R
import kotlinx.android.synthetic.main.wallet_utils_fragment.view.*


class WalletUtilsFragment : Fragment() {

    companion object {
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
        viewModel.peerList.observe(this, Observer { peerList ->
            layoutView.message1.text = "${peerList!!.size} \n $peerList"
        })
        viewModel.blockchainState.observe(this, Observer { blockchainState ->
            blockchainState?.let {
                layoutView.message2.text = "bestChainDate:\t${it.bestChainDate}\nbestChainHeight:\t${it.bestChainHeight}\nblocksLeft:\t${it.blocksLeft}"
            }
        })
    }

}
