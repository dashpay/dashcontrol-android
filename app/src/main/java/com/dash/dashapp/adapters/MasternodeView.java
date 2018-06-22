package com.dash.dashapp.adapters;

import android.content.Context;
import android.widget.TextView;

import com.dash.dashapp.models.Masternode;
import com.dash.dashapp.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.ChildPosition;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;

/**
 * Created by sebas on 10/26/2017.
 */

@Layout(R.layout.masternode_view)
public class MasternodeView {

    @ParentPosition
    public int mParentPosition;

    @ChildPosition
    public int mChildPosition;

    @View(R.id.masternode_textview)
    public TextView titleTxt;

    private Masternode mMasternode;
    private Context mContext;

    public MasternodeView(Context context, Masternode masternode) {
        mContext = context;
        mMasternode = masternode;
    }

    @Resolve
    public void onResolved() {
        titleTxt.setText(mMasternode.getName());
    }
}