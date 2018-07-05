package com.dash.dashapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.BudgetApiProposalAnswer;
import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.models.BudgetProposalComment;
import com.dash.dashapp.utils.MainPreferences;
import com.dash.dashapp.view.ProposalCommentView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private BudgetProposal budgetProposal;

    private Pair<BudgetProposalComment, String> replyCache;

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
        budgetProposal = (BudgetProposal) intent.getSerializableExtra(PROPOSAL_EXTRA);
        displayBasicInfo(budgetProposal);
        loadProposalComments(budgetProposal.getHash());
    }

    private void loadProposalComments(String hash) {
        Call<BudgetApiProposalAnswer> proposalDetails = DashControlClient.getInstance().getProposalDetails(hash);
        proposalDetails.enqueue(new Callback<BudgetApiProposalAnswer>() {
            @Override
            public void onResponse(@NonNull Call<BudgetApiProposalAnswer> call, @NonNull Response<BudgetApiProposalAnswer> response) {
                if (response.isSuccessful()) {
                    BudgetApiProposalAnswer proposalAnswer = Objects.requireNonNull(response.body());
                    List<BudgetProposalComment> proposalComments = proposalAnswer.convertComments();
                    displayComments(proposalComments);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BudgetApiProposalAnswer> call, @NonNull Throwable t) {
                Toast.makeText(ProposalCommentsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayBasicInfo(BudgetProposal budgetProposal) {
        titleView.setText(String.valueOf(budgetProposal.getTitle()));
        yesVotesRatioView.setProgress(budgetProposal.getRatioYes());
        yesVotesRatioValueView.setText(getString(R.string.simple_percentage_value, budgetProposal.getRatioYes()));
        ownerView.setText(getString(R.string.owner_format, budgetProposal.getOwner()));
    }

    private void displayComments(List<BudgetProposalComment> commentList) {
        commentsContainerView.removeAllViews();
        sortByOrder(commentList);

        TreeNode<BudgetProposalComment> hierarchy = new TreeNode<>(null);
        for (BudgetProposalComment comment : commentList) {
            int level = comment.getLevelAsInt();
            TreeNode<BudgetProposalComment> parent = hierarchy;
            for (int i = 0; i < level; i++) {
                parent = parent.getLastChild();
            }
            parent.addChild(comment);
        }

        List<TreeNode<BudgetProposalComment>> flattenHierarchy = hierarchy.flatten();
        flattenHierarchy.remove(0);
        for (TreeNode<BudgetProposalComment> commentNode : flattenHierarchy) {
            BudgetProposalComment comment = commentNode.data;
            ProposalCommentView proposalCommentView = new ProposalCommentView(this);

            if (comment.getLevelAsInt() == 0) {
                proposalCommentView.bind(comment, onAddReplyClickListener);
            } else {
                BudgetProposalComment parentComment = commentNode.parent.data;
                proposalCommentView.bind(parentComment, comment, onAddReplyClickListener);
            }
            commentsContainerView.addView(proposalCommentView);
        }
    }

    private ProposalCommentView.OnAddReplyClickListener onAddReplyClickListener = new ProposalCommentView.OnAddReplyClickListener() {
        @Override
        public void onAddReplyClick(BudgetProposalComment comment, String replyContent) {
            postProposalComment(comment, replyContent);
        }
    };

    private void postProposalComment(BudgetProposalComment comment, String replyContent) {
        MainPreferences preferences = new MainPreferences(ProposalCommentsActivity.this);
        String dashcentralApiKey = preferences.getDashcentralApiKey();
        if (dashcentralApiKey == null) {
            replyCache = new Pair<>(comment, replyContent);
            requestDashCentralApiKey();
        } else {
            replyCache = null;
            final String proposalHash = budgetProposal.getHash();
            String commentId = comment != null ? comment.id : null;
            DashControlClient.getInstance().postProposalComment(dashcentralApiKey, proposalHash, commentId, replyContent, new DashControlClient.Callback<String>() {
                @Override
                public void onResponse(String response) {
                    loadProposalComments(proposalHash);
                }

                @Override
                public void onFailure(Throwable t) {
                    if (t != null) {
                        Toast.makeText(ProposalCommentsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void requestDashCentralApiKey() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setPrompt(getString(R.string.login_to_dashcentral));
        scanIntegrator.setBeepEnabled(false);
        scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        scanIntegrator.setCameraId(0);
        scanIntegrator.setBarcodeImageEnabled(false);
        scanIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null && !TextUtils.isEmpty(scanningResult.getContents())) {
            String scanContent = scanningResult.getContents();
            MainPreferences preferences = new MainPreferences(ProposalCommentsActivity.this);
            preferences.setDashcentralApiKey(scanContent);
            onAddReplyClickListener.onAddReplyClick(replyCache.first, replyCache.second);
        }
    }

    private void sortByOrder(List<BudgetProposalComment> commentList) {
        Collections.sort(commentList, new Comparator<BudgetProposalComment>() {
            @Override
            public int compare(BudgetProposalComment o1, BudgetProposalComment o2) {
                return Integer.compare(o1.order, o2.order);
            }
        });
    }

    private static class TreeNode<T> {

        private T data;
        private TreeNode<T> parent;
        private List<TreeNode<T>> children;

        TreeNode(T data) {
            this.data = data;
            this.children = new ArrayList<>();
        }

        TreeNode(T data, TreeNode<T> parent) {
            this(data);
            this.parent = parent;
        }

        void addChild(T child) {
            TreeNode<T> treeNode = new TreeNode<>(child, this);
            children.add(treeNode);
        }

        TreeNode<T> getLastChild() {
            if (children.size() > 0) {
                return children.get(children.size() - 1);
            } else {
                return null;
            }
        }

        List<TreeNode<T>> flatten() {
            List<TreeNode<T>> result = new ArrayList<>();
            result.add(this);
            for (TreeNode<T> child : children) {
                result.addAll(child.flatten());
            }
            return result;
        }
    }
}
