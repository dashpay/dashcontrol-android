package com.dash.dashapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dash.dashapp.R;
import com.dash.dashapp.models.BudgetProposal;

import butterknife.BindView;

public class ProposalDetailActivity extends BaseActivity {

    public static final String PROPOSAL_EXTRA = "proposal_extra";

    @BindView(R.id.yes_votes)
    TextView yesVotesView;

    @BindView(R.id.no_votes)
    TextView noVotesView;

    @BindView(R.id.abstain_votes)
    TextView abstainVotesView;

    @BindView(R.id.title)
    TextView titleView;

    @BindView(R.id.approval_progress)
    ProgressBar yesVotesRatioView;

    @BindView(R.id.approval_progress_value)
    TextView yesVotesRatioValueView;

    @BindView(R.id.owner)
    TextView ownerView;

    @BindView(R.id.completed_payments)
    TextView completedPaymentsView;

    @BindView(R.id.proposal_description)
    TextView proposalDescriptionView;

    public static Intent createIntent(Context context, BudgetProposal proposal) {
        Intent intent = new Intent(context, ProposalDetailActivity.class);
        intent.putExtra(PROPOSAL_EXTRA, proposal);
        return intent;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content_proposal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showBackAction();

        Intent intent = getIntent();
        BudgetProposal budgetProposal = (BudgetProposal) intent.getSerializableExtra(PROPOSAL_EXTRA);
        display(budgetProposal);
    }

    private void display(BudgetProposal budgetProposal) {

        yesVotesView.setText(String.valueOf(budgetProposal.yesVotes));
        noVotesView.setText(String.valueOf(budgetProposal.noVotes));
        abstainVotesView.setText(String.valueOf(budgetProposal.abstainVotes));
        titleView.setText(String.valueOf(budgetProposal.title));

        double ratioYes = ((double) budgetProposal.yesVotes / (budgetProposal.yesVotes + budgetProposal.noVotes)) * 100;
        int ratioYesInt = (int) ratioYes;
        yesVotesRatioView.setProgress(ratioYesInt);
        yesVotesRatioValueView.setText(getString(R.string.simple_percentage_value, ratioYesInt));

        ownerView.setText(getString(R.string.owner_format, budgetProposal.owner));

        int completedPayments = budgetProposal.totalPaymentCount - budgetProposal.remainingPaymentCount;
        completedPaymentsView.setText(getString(R.string.completed_payments_format, completedPayments, budgetProposal.monthlyAmount));

//        proposalDescriptionView.setText(budgetProposal.);
    }


    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
