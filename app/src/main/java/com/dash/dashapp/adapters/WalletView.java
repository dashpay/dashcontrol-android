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
    public int mParentPosition;

    @ChildPosition
    public int mChildPosition;

    @View(R.id.wallet_textview)
    public TextView titleTxt;

    private Wallet mWallet;
    private Context mContext;

    public WalletView(Context context, Wallet wallet) {
        mContext = context;
        mWallet = wallet;
    }

    @Resolve
    public void onResolved() {
        titleTxt.setText(mWallet.getName());
    }
}
