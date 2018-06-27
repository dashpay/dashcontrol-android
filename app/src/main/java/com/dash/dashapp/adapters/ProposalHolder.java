package com.dash.dashapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dash.dashapp.R;
import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.utils.DateUtil;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        //TODO calculate the month
        Date startDate = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        int monthRemaining = DateUtil.monthDifference(startDate, budgetProposal.getDateEnd());
        if (monthRemaining == 1) {
            monthRemainingView.setText(itemView.getContext().getString(R.string.month_remaining, monthRemaining));
        } else {
            monthRemainingView.setText(itemView.getContext().getString(R.string.months_remaining, monthRemaining));
        }

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