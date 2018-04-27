package com.dash.dashapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dash.dashapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterToggleView extends RelativeLayout implements Checkable {

    @BindView(R.id.filter_title)
    TextView titleView;

    @BindView(R.id.filter_value)
    TextView valueView;

    private OnCheckedChangeListener onCheckedChangeListener;

    boolean checked;

    boolean radioButton = true;

    public FilterToggleView(@NonNull Context context) {
        super(context);
        init();
    }

    public FilterToggleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterToggleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FilterToggleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_filter_toggle, this);
        ButterKnife.bind(this);
        setBackgroundResource(R.drawable.filter_toggle_selector);
        setClickable(true);
        setFocusable(true);
    }

    private static final int[] checkedStateSet = {
            android.R.attr.state_checked,
    };

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, checkedStateSet);
        }
        return drawableState;
    }

    @Override
    public boolean performClick() {
        if (radioButton && checked) {
            return false;
        }
        toggle();
        return super.performClick();
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        refreshDrawableState();
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    public void setText(CharSequence text) {
        titleView.setText(text);
    }

    public void setText(@StringRes int resid) {
        titleView.setText(resid);
    }

    public void setValue(CharSequence value) {
        titleView.setText(value);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(FilterToggleView toggleView, boolean isChecked);
    }
}
