package com.dash.dashapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.BudgetApiProposalAnswer;
import com.dash.dashapp.api.data.DashProposalComment;
import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.models.BudgetProposalComment;
import com.dash.dashapp.view.ProposalCommentView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProposalCommentsActivity extends BaseActivity {

    public static final String PROPOSAL_EXTRA = "proposal_extra";

    @BindView(R.id.comments_container)
    LinearLayout commentsContainerView;

    @BindView(R.id.title)
    TextView titleView;

    @BindView(R.id.approval_progress)
    ProgressBar yesVotesRatioView;

    @BindView(R.id.approval_progress_value)
    TextView yesVotesRatioValueView;

    @BindView(R.id.owner)
    TextView ownerView;

    public static Intent createIntent(Context context, BudgetProposal proposal) {
        Intent intent = new Intent(context, ProposalCommentsActivity.class);
        intent.putExtra(PROPOSAL_EXTRA, proposal);
        return intent;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_proposal_coments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showBackAction();

        Intent intent = getIntent();
        BudgetProposal budgetProposal = (BudgetProposal) intent.getSerializableExtra(PROPOSAL_EXTRA);
        displayBasicInfo(budgetProposal);
        loadProposalComments(budgetProposal.hash);
    }

    private void loadProposalComments(String hash) {
        Call<BudgetApiProposalAnswer> proposalDetails = DashControlClient.getInstance().getProposalDetails(hash);
        proposalDetails.enqueue(new Callback<BudgetApiProposalAnswer>() {
            @Override
            public void onResponse(@NonNull Call<BudgetApiProposalAnswer> call, @NonNull Response<BudgetApiProposalAnswer> response) {
                if (response.isSuccessful()) {
                    BudgetApiProposalAnswer proposalAnswer = Objects.requireNonNull(response.body());
                    List<DashProposalComment> proposalComments = proposalAnswer.comments;
                    displayComments(new ArrayList<BudgetProposalComment.Convertible>(proposalComments));
                }
            }

            @Override
            public void onFailure(@NonNull Call<BudgetApiProposalAnswer> call, @NonNull Throwable t) {
                Toast.makeText(ProposalCommentsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayBasicInfo(BudgetProposal budgetProposal) {
        titleView.setText(String.valueOf(budgetProposal.title));
        yesVotesRatioView.setProgress(budgetProposal.getRatioYes());
        yesVotesRatioValueView.setText(getString(R.string.simple_percentage_value, budgetProposal.getRatioYes()));
        ownerView.setText(getString(R.string.owner_format, budgetProposal.owner));
    }

    private void displayComments(ArrayList<BudgetProposalComment.Convertible> commentList) {
        commentsContainerView.removeAllViews();
        BudgetProposalComment previousComment = null;
        for (BudgetProposalComment.Convertible rawComment : commentList) {
            BudgetProposalComment comment = rawComment.convert();
            ProposalCommentView proposalCommentView = new ProposalCommentView(this);
            proposalCommentView.setAuthor(comment.username, comment.postedByOwner);
            proposalCommentView.setPoints(0, comment.dateHuman);
            proposalCommentView.setComment(comment.content);

            if (!comment.level.equals("0") && (previousComment != null)) {
                proposalCommentView.setInReplayTo(previousComment.username, previousComment.postedByOwner);
            }
            previousComment = comment;

            commentsContainerView.addView(proposalCommentView);
        }
    }
}
