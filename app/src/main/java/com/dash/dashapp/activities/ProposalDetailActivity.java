package com.dash.dashapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.BudgetApiProposalAnswer;
import com.dash.dashapp.helpers.AssetsHelper;
import com.dash.dashapp.models.BudgetProposal;

import java.util.Objects;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProposalDetailActivity extends BaseActivity {

    public static final String PROPOSAL_EXTRA = "proposal_extra";

    private static final String DESCRIPTION_CONTENT_MIME_TYPE = "text/html; charset=utf-8";
    private static final String DESCRIPTION_CONTENT_ENCODING = "utf-8";

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
    WebView proposalDescriptionView;

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

        setupWebView(proposalDescriptionView);

        Intent intent = getIntent();
        BudgetProposal budgetProposal = (BudgetProposal) intent.getSerializableExtra(PROPOSAL_EXTRA);
        displayBasicInfo(budgetProposal);
        loadProposalDetails(budgetProposal.hash);
    }

    private void loadProposalDetails(String hash) {
        Call<BudgetApiProposalAnswer> proposalDetails = DashControlClient.getInstance().getProposalDetails(hash);
        proposalDetails.enqueue(new Callback<BudgetApiProposalAnswer>() {
            @Override
            public void onResponse(@NonNull Call<BudgetApiProposalAnswer> call, @NonNull Response<BudgetApiProposalAnswer> response) {
                if (response.isSuccessful()) {
                    BudgetApiProposalAnswer proposalAnswer = Objects.requireNonNull(response.body());
                    BudgetProposal proposal = proposalAnswer.proposal.convert();
                    displayDetails(proposal);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BudgetApiProposalAnswer> call, @NonNull Throwable t) {
                Toast.makeText(ProposalDetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayBasicInfo(BudgetProposal budgetProposal) {
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
    }

    private void displayDetails(BudgetProposal budgetProposal) {
        displayBasicInfo(budgetProposal);
        if (budgetProposal.descriptionHtml != null) {
            String completeDescription = AssetsHelper.applyProposalDescriptionTemplate(ProposalDetailActivity.this, budgetProposal.descriptionHtml);
            proposalDescriptionView.loadData(completeDescription, DESCRIPTION_CONTENT_MIME_TYPE, DESCRIPTION_CONTENT_ENCODING);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(final WebView webView) {
        webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:AdjustSizeScript.resizeToWrapContentHeight(document.body.getBoundingClientRect().height)");
                super.onPageFinished(view, url);
            }
        });
        webView.addJavascriptInterface(this, "AdjustSizeScript");
    }

    @JavascriptInterface
    public void resizeToWrapContentHeight(final float height) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                proposalDescriptionView.setLayoutParams(
                        new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }
}
