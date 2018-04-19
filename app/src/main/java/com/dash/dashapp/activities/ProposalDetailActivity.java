package com.dash.dashapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dash.dashapp.R;
import com.dash.dashapp.models.BudgetProposal;

import butterknife.BindView;

public class ProposalDetailActivity extends BaseActivity {

    private static final String CONTENT_PROPOSAL = "proposal";
    @BindView(R.id.pie_yes_no)
    ProgressBar pieYesNo;
    @BindView(R.id.approval_rate_textview)
    TextView approvalRateTextview;
    @BindView(R.id.textView_id_proposal)
    TextView textViewIdProposal;
    @BindView(R.id.textView_title_proposal)
    TextView textViewTitleProposal;
    @BindView(R.id.textView_owner)
    TextView textViewOwner;
    @BindView(R.id.textView_title)
    TextView textViewTitle;
    @BindView(R.id.textView_one_time_payment)
    TextView textViewOneTimePayment;
    @BindView(R.id.textView_month_remaining)
    TextView textViewMonthRemaining;
    @BindView(R.id.textView_completed_payments)
    TextView textViewCompletedPayments;
    @BindView(R.id.textView_yes)
    TextView textViewYes;
    @BindView(R.id.textView_no)
    TextView textViewNo;
    @BindView(R.id.textView_abstain)
    TextView textViewAbstain;
    @BindView(R.id.textView_proposal_description)
    TextView textViewProposalDescription;
    @BindView(R.id.button_accept_proposal)
    Button buttonAcceptProposal;
    @BindView(R.id.button_abstain)
    Button buttonAbstain;
    @BindView(R.id.button_no)
    Button buttonNo;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content_proposal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        BudgetProposal budgetProposal = (BudgetProposal) intent.getSerializableExtra(CONTENT_PROPOSAL);

        double ratioYes = ((double) budgetProposal.yesVotes / (budgetProposal.yesVotes + budgetProposal.noVotes)) * 100;
        int ratioYesInt = (int) ratioYes;
        pieYesNo.setProgress(ratioYesInt);
        approvalRateTextview.setText(ratioYesInt + "%");

        textViewIdProposal.setText(budgetProposal.title);

        textViewTitleProposal.setText(budgetProposal.title);

        textViewOwner.setText("Owned by " + budgetProposal.owner);

        textViewTitle.setText(budgetProposal.title);

        // TODO textViewOneTimePayment.setText("TODO");

        //TODO textViewMonthRemaining.setText(budgetProposal.getVoting_deadline_human());

        textViewCompletedPayments.setText(budgetProposal.totalPaymentCount + "");

        textViewYes.setText(budgetProposal.yesVotes + " Yes");

        textViewNo.setText(budgetProposal.name + " No");

        // TODO textViewAbstain.setText(budgetProposal.getTitle());

        // TODO RECUPERER LA PROP : textViewProposalDescription.setText(budgetProposal.getTitle());

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
