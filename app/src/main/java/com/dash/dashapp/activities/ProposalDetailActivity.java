package com.dash.dashapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.BudgetApiProposalAnswer;
import com.dash.dashapp.helpers.AssetsHelper;
import com.dash.dashapp.helpers.DimenHelper;
import com.dash.dashapp.models.BudgetProposal;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
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

    @BindView(R.id.payment_type)
    TextView paymentTypeView;

    @BindView(R.id.payment_type_value)
    TextView paymentTypeValueView;

    @BindView(R.id.completed_payments)
    TextView completedPaymentsView;

    @BindView(R.id.time_remaining)
    TextView timeRemainingView;

    @BindView(R.id.proposal_description)
    WebView proposalDescriptionView;

    BudgetProposal budgetProposal;

    private int targetWebViewHeightDp = -1;
    private boolean webViewExpanded = false;

    public static Intent createIntent(Context context, BudgetProposal proposal) {
        Intent intent = new Intent(context, ProposalDetailActivity.class);
        intent.putExtra(PROPOSAL_EXTRA, proposal);
        return intent;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_proposal_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showBackAction();

        setupWebView(proposalDescriptionView);

        Intent intent = getIntent();
        BudgetProposal budgetProposal = (BudgetProposal) intent.getSerializableExtra(PROPOSAL_EXTRA);
        displayBasicInfo(budgetProposal);
        loadProposalDetails(budgetProposal.getHash());
    }

    @OnClick({R.id.comments})
    public void onCommentsClick() {
        Intent proposalCommentsIntent = ProposalCommentsActivity.createIntent(this, budgetProposal);
        startActivity(proposalCommentsIntent);
    }

    private void loadProposalDetails(String hash) {
        Call<BudgetApiProposalAnswer> proposalDetails = DashControlClient.getInstance().getProposalDetails(hash);
        proposalDetails.enqueue(new Callback<BudgetApiProposalAnswer>() {
            @Override
            public void onResponse(@NonNull Call<BudgetApiProposalAnswer> call, @NonNull Response<BudgetApiProposalAnswer> response) {
                if (response.isSuccessful()) {
                    BudgetApiProposalAnswer proposalAnswer = Objects.requireNonNull(response.body());
                    BudgetProposal proposal = proposalAnswer.proposal.convert(false);
                    displayDetails(proposal);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BudgetApiProposalAnswer> call, @NonNull Throwable t) {
                Toast.makeText(ProposalDetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayBasicInfo(BudgetProposal proposal) {
        this.budgetProposal = proposal;
        yesVotesView.setText(String.valueOf(proposal.getYesVotes()));
        noVotesView.setText(String.valueOf(proposal.getNoVotes()));
        abstainVotesView.setText(String.valueOf(proposal.getAbstainVotes()));
        titleView.setText(String.valueOf(proposal.getTitle()));

        yesVotesRatioView.setProgress(proposal.getRatioYes());
        yesVotesRatioValueView.setText(getString(R.string.simple_percentage_value, proposal.getRatioYes()));

        ownerView.setText(getString(R.string.owner_format, proposal.getOwner()));

        String completedPaymentStr;
        if (proposal.getTotalPaymentCount() == 1) {
            paymentTypeView.setText(R.string.payment_type_one_time);
            completedPaymentStr = getString(R.string.no_payments_occurred_yet);
        } else {
            paymentTypeView.setText(R.string.payment_type_monthly);
            int completedPayments = proposal.getTotalPaymentCount() - proposal.getRemainingPaymentCount();
            if (completedPayments == 0) {
                completedPaymentStr = getString(R.string.no_payments_occurred_yet);
            } else {
                float spentAmount = completedPayments * proposal.getMonthlyAmount();
                completedPaymentStr = getString(R.string.completed_payments_format, completedPayments, spentAmount);
            }
        }

        String amountStr = getString(R.string.dash_amount_float, proposal.getMonthlyAmount());
        paymentTypeValueView.setText(amountStr);

        completedPaymentsView.setText(completedPaymentStr);

        int remainingPaymentCount = proposal.getRemainingPaymentCount();
        String monthsRemaining = getResources().getQuantityString(R.plurals.months_remaining, remainingPaymentCount, remainingPaymentCount);
        timeRemainingView.setText(monthsRemaining);
    }

    private void displayDetails(BudgetProposal budgetProposal) {
        displayBasicInfo(budgetProposal);
        if (budgetProposal.getDescriptionHtml() != null) {
            String completeDescription = AssetsHelper.applyProposalDescriptionTemplate(ProposalDetailActivity.this, budgetProposal.getDescriptionHtml());
            proposalDescriptionView.loadData(completeDescription, DESCRIPTION_CONTENT_MIME_TYPE, DESCRIPTION_CONTENT_ENCODING);
        }
    }

    @OnClick(R.id.show_more)
    public void onShowMoreClick(Button button) {
        if (targetWebViewHeightDp == 0) {
            return;
        }
        int targetHeight;
        if (webViewExpanded) {
            targetHeight = DimenHelper.dpToPx(256);
            button.setText(R.string.show_full_description);
            webViewExpanded = false;
        } else {
            targetHeight = DimenHelper.dpToPx(targetWebViewHeightDp);
            webViewExpanded = true;
            button.setText(R.string.hide_full_description);
        }
        proposalDescriptionView.setLayoutParams(
                new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, targetHeight));
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
                webView.loadUrl("javascript:SaveTargetHeightScript.saveHeight(document.body.getBoundingClientRect().height)");
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }
        });
        webView.addJavascriptInterface(this, "SaveTargetHeightScript");

        disableScrollingWhenWebViewCollapsed();
    }

    @JavascriptInterface
    public void saveHeight(final float height) {
        targetWebViewHeightDp = (int) height;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void disableScrollingWhenWebViewCollapsed() {
        proposalDescriptionView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !webViewExpanded && (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
    }
}
