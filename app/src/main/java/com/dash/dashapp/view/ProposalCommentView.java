package com.dash.dashapp.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dash.dashapp.R;
import com.dash.dashapp.models.BudgetProposalComment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProposalCommentView extends FrameLayout {

    @BindView(R.id.in_replay_to)
    TextView inReplayToView;

    @BindView(R.id.author)
    TextView authorView;

    @BindView(R.id.is_proposal_owner)
    View isProposalOwnerView;

    @BindView(R.id.points)
    TextView pointsView;

    @BindView(R.id.comment)
    TextView commentView;

    @BindView(R.id.reply_pane)
    View replyPaneView;

    @BindView(R.id.reply_edit)
    EditText replyEditView;

    private BudgetProposalComment comment;
    private OnAddReplyClickListener onAddReplyClickListener;

    public ProposalCommentView(@NonNull Context context) {
        super(context);
        init();
    }

    public ProposalCommentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProposalCommentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ProposalCommentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_proposal_comment, this);
        ButterKnife.bind(this);
        setBackgroundResource(R.color.lightest_grey);
        inReplayToView.setVisibility(GONE);
        replyPaneView.setVisibility(GONE);
    }

    @OnClick(R.id.reply_button)
    public void onReplyClick(TextView replyButtonView) {
        if (replyPaneView.getVisibility() == VISIBLE) {
            replyButtonView.setText(R.string.reply_button);
            replyPaneView.setVisibility(GONE);
        } else {
            replyButtonView.setText(R.string.hide_reply_button);
            replyPaneView.setVisibility(VISIBLE);
        }
    }

    @OnClick(R.id.add_reply)
    public void onAddReplyClick() {
        if (onAddReplyClickListener != null) {
            String replayContent = replyEditView.getText().toString();
            onAddReplyClickListener.onAddReplyClick(comment, replayContent);
        }
    }

    public void bind(BudgetProposalComment comment, OnAddReplyClickListener listener) {
        this.comment = comment;
        this.onAddReplyClickListener = listener;
        setAuthor(comment.username, comment.postedByOwner);
        setPoints(0, comment.dateHuman);
        setComment(comment.content);
    }

    public void bind(BudgetProposalComment parentComment, BudgetProposalComment comment, OnAddReplyClickListener listener) {
        bind(comment, listener);
        setInReplayTo(parentComment.username, parentComment.postedByOwner);
    }

    public void setInReplayTo(String author, boolean isProposalOwner) {
        int textFormatResId;
        if (isProposalOwner) {
            textFormatResId = R.string.in_replay_to_owner;
        } else {
            textFormatResId = R.string.in_replay_to;
        }
        String text = getResources().getString(textFormatResId, author);
        inReplayToView.setText(text);
        inReplayToView.setVisibility(VISIBLE);
        setIsAnswerComment(true);
    }

    public void setIsAnswerComment(boolean isAnswerComment) {
        int leftPadding = isAnswerComment ? dpToPx(16) : 0;
        setPadding(leftPadding, 0, 0, 0);
    }

    public void setAuthor(CharSequence text, boolean isProposalOwner) {
        authorView.setText(text);
        isProposalOwnerView.setVisibility(isProposalOwner ? VISIBLE : GONE);
    }

    public void setPoints(int points, String added) {
        String commentPoints = getResources().getQuantityString(R.plurals.comment_points, points, points, added);
        pointsView.setText(commentPoints);
    }

    public void setComment(String comment) {
        commentView.setText(comment);
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public interface OnAddReplyClickListener {
        void onAddReplyClick(BudgetProposalComment comment, String replay);
    }
}
