package com.dash.dashapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dash.dashapp.Activities.ProposalDetailActivity;
import com.dash.dashapp.Model.Proposal;
import com.dash.dashapp.R;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

/**
 * Created by sebas on 8/8/2017.
 */

@Layout(R.layout.proposal_view)
public class ProposalView {

    private static final String CONTENT_PROPOSAL = "proposal";

    @View(R.id.approval_progress_bar)
    private ProgressBar approvalRatePie;

    @View(R.id.approval_rate_textview)
    private TextView approvalRateTextview;

    @View(R.id.title_textView)
    private TextView titleTxt;

    private Proposal mProposal;
    private Context mContext;

    public ProposalView(Context context, Proposal proposal) {
        mContext = context;
        mProposal = proposal;
    }

    @Resolve
    private void onResolved() {
        titleTxt.setText(mProposal.getTitle());

        double ratioYes = ((double)mProposal.getYes()/(mProposal.getYes() + mProposal.getNo()))*100;
        int ratioYesInt = (int) ratioYes;
        approvalRatePie.setProgress(ratioYesInt);
        approvalRateTextview.setText(ratioYesInt + "%");
    }



    @Click(R.id.proposal_row)
    private void onClick(){
        Intent intent = new Intent(mContext, ProposalDetailActivity.class);
        intent.putExtra(CONTENT_PROPOSAL, mProposal);
        mContext.startActivity(intent);
    }
}
