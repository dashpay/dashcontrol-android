package com.dash.dashapp.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dash.dashapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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
}
