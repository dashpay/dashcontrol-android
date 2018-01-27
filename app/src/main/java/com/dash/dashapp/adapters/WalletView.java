package com.dash.dashapp.adapters;

import android.content.Context;
import android.widget.TextView;

import com.dash.dashapp.models.Wallet;
import com.dash.dashapp.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.ChildPosition;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;

/**
 * Created by sebas on 10/26/2017.
 */

@Layout(R.layout.wallet_view)
public class WalletView {

    @ParentPosition
    private int mParentPosition;

    @ChildPosition
    private int mChildPosition;

    @View(R.id.wallet_textview)
    private TextView titleTxt;

    private Wallet mWallet;
    private Context mContext;

    public WalletView(Context context, Wallet wallet) {
        mContext = context;
        mWallet = wallet;
    }

    @Resolve
    private void onResolved() {
        titleTxt.setText(mWallet.getName());
    }
}
