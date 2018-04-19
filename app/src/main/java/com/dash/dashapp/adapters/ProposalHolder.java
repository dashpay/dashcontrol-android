package com.dash.dashapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dash.dashapp.R;
import com.dash.dashapp.activities.ProposalDetailActivity;
import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.utils.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProposalHolder extends RecyclerView.ViewHolder {

    private static final String CONTENT_PROPOSAL = "budgetProposal";

    @BindView(R.id.approval_progress_bar)
    public ProgressBar approvalRatePie;

    @BindView(R.id.approval_rate_textview)
    public TextView approvalRateTextView;

    @BindView(R.id.title_textView)
    public TextView titleTxt;

    @BindView(R.id.title_owner_textview)
    public TextView titleOwnerTextView;

    @BindView(R.id.textView_month_remaining)
    public TextView monthRemainingTextView;

    @BindView(R.id.textView_comments_number)
    public TextView commentsNumberTextView;

    @BindView(R.id.textview_dash_amount)
    public TextView dashAmountTextView;

    @BindView(R.id.textView_by_owner)
    public TextView byOwnerTextView;

    private Context context;

    private BudgetProposal budgetProposal;

    ProposalHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public void bind(BudgetProposal budgetProposal) {
        this.budgetProposal = budgetProposal;

        titleTxt.setText(budgetProposal.title);

        double ratioYes = ((double) budgetProposal.yesVotes / (budgetProposal.yesVotes + budgetProposal.noVotes)) * 100;
        int ratioYesInt = (int) ratioYes;
        approvalRatePie.setProgress(ratioYesInt);
        approvalRateTextView.setText(ratioYesInt + "%");

        //TODO What's the owner title ?
        if (!budgetProposal.title.equals("")) {
            titleOwnerTextView.setText(budgetProposal.title);
        }

        //TODO calculate the month
        Date startDate = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        int monthRemaining = DateUtil.monthDifference(startDate, budgetProposal.dateEnd);
        if (monthRemaining == 1) {
            monthRemainingTextView.setText(context.getString(R.string.month_remaining, monthRemaining));
        } else {
            monthRemainingTextView.setText(context.getString(R.string.months_remaining, monthRemaining));
        }

        commentsNumberTextView.setText(context.getString(R.string.comments, budgetProposal.commentAmount));

        dashAmountTextView.setText(context.getString(R.string.dash, budgetProposal.monthlyAmount));

        if (!budgetProposal.owner.equals("")) {
            byOwnerTextView.setText(context.getString(R.string.by, budgetProposal.owner));
        }
    }

    @OnClick
    public void onClick() {
        Intent intent = new Intent(context, ProposalDetailActivity.class);
        intent.putExtra(CONTENT_PROPOSAL, budgetProposal);
        context.startActivity(intent);
    }
}