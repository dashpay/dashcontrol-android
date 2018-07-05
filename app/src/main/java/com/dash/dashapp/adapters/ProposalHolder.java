package com.dash.dashapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dash.dashapp.R;
import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.utils.DateUtil;

import java.text.NumberFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProposalHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.approval_progress)
    ProgressBar approvalProgressView;

    @BindView(R.id.approval_progress_value)
    TextView approvalProgressValueView;

    @BindView(R.id.title)
    TextView titleView;

    @BindView(R.id.month_remaining)
    TextView monthRemainingView;

    @BindView(R.id.owner)
    TextView ownerView;

    @BindView(R.id.comments_number)
    public TextView commentsNumberView;

    @BindView(R.id.dash_amount)
    public TextView dashAmountView;

    private ProposalAdapter.OnItemClickListener onItemClickListener;

    private BudgetProposal budgetProposal;

    ProposalHolder(View itemView, ProposalAdapter.OnItemClickListener onItemClickListener) {
        super(itemView);
        this.onItemClickListener = onItemClickListener;
        ButterKnife.bind(this, itemView);
    }

    public void bind(BudgetProposal budgetProposal) {
        this.budgetProposal = budgetProposal;

        titleView.setText(budgetProposal.getTitle());

        int ratioYes = budgetProposal.getRatioYes();
        approvalProgressView.setProgress(ratioYes);
        approvalProgressValueView.setText(ratioYes + "%");

        Date today = new Date();
        int monthRemaining = DateUtil.monthDifference(today, budgetProposal.getDateEnd());
        int remainingPaymentCount = budgetProposal.getRemainingPaymentCount();
        String monthsRemaining = itemView.getContext().getResources().getQuantityString(R.plurals.months_remaining, remainingPaymentCount, remainingPaymentCount);
        monthRemainingView.setText(monthsRemaining);

        NumberFormat formatter = NumberFormat.getNumberInstance();
//        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        dashAmountView.setText(formatter.format(budgetProposal.getMonthlyAmount()));

        if (!TextUtils.isEmpty(budgetProposal.getOwner())) {
            ownerView.setText(itemView.getContext().getString(R.string.by, budgetProposal.getOwner()));
        }

        commentsNumberView.setText(String.valueOf(budgetProposal.getCommentAmount()));
    }

    @OnClick
    public void onClick() {
        onItemClickListener.onItemClick(budgetProposal);
    }
}