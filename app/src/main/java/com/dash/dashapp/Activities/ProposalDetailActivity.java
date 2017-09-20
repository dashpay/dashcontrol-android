package com.dash.dashapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dash.dashapp.Model.Proposal;
import com.dash.dashapp.R;

public class ProposalDetailActivity extends AppCompatActivity {

    private static final String CONTENT_PROPOSAL = "proposal";

    private ProgressBar pieYesNo;

    private TextView approvalRateTextview, idProposalTextview, titleProposalTextview, ownerTextview, titleTextview,
    oneTimePaymentTextview, monthRemainingTextview, completedPaymentsTextview, yesTextview, noTextview,
    abstaintextview, proposalDescriptionTextview, acceptProposalTextview;

    private Button acceptProposalButton, abstainButton, noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_proposal);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Proposal proposal = (Proposal) intent.getSerializableExtra(CONTENT_PROPOSAL);

        pieYesNo = (ProgressBar) findViewById(R.id.pie_yes_no);
        approvalRateTextview = (TextView) findViewById(R.id.approval_rate_textview);

        double ratioYes = ((double)proposal.getYes()/(proposal.getYes() + proposal.getNo()))*100;
        int ratioYesInt = (int) ratioYes;
        pieYesNo.setProgress(ratioYesInt);
        approvalRateTextview.setText(ratioYesInt + "%");

        idProposalTextview = (TextView) findViewById(R.id.textView_id_proposal);
        idProposalTextview.setText(proposal.getTitle());

        titleProposalTextview = (TextView) findViewById(R.id.textView_title_proposal);
        titleProposalTextview.setText(proposal.getTitle());

        ownerTextview = (TextView) findViewById(R.id.textView_owner);
        ownerTextview.setText("Owned by " + proposal.getOwner_username());

        titleTextview = (TextView) findViewById(R.id.textView_title);
        titleTextview.setText(proposal.getTitle());

        oneTimePaymentTextview = (TextView) findViewById(R.id.textView_one_time_payment);
        // TODO oneTimePaymentTextview.setText("TODO");

        monthRemainingTextview = (TextView) findViewById(R.id.textView_month_remaining);
        //TODO monthRemainingTextview.setText(proposal.getVoting_deadline_human());

        completedPaymentsTextview = (TextView) findViewById(R.id.textView_completed_payments);
        completedPaymentsTextview.setText(proposal.getTotal_payment_count() + "");

        yesTextview = (TextView) findViewById(R.id.textView_yes);
        yesTextview.setText(proposal.getYes() + " Yes");

        noTextview = (TextView) findViewById(R.id.textView_no);
        noTextview.setText(proposal.getNo() + " No");

        abstaintextview = (TextView) findViewById(R.id.textView_abstain);
        // TODO abstaintextview.setText(proposal.getTitle());

        proposalDescriptionTextview = (TextView) findViewById(R.id.textView_proposal_description);
        // TODO RECUPERER LA PROP : proposalDescriptionTextview.setText(proposal.getTitle());


        acceptProposalButton = (Button) findViewById(R.id.button_accept_proposal);
        abstainButton = (Button) findViewById(R.id.button_abstain);
        noButton = (Button) findViewById(R.id.button_no);
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
