package com.dash.dashapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dash.dashapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExpandableFiltersView extends FrameLayout {

    @BindView(R.id.filter_title)
    TextView titleView;

    @BindView(R.id.sub_filters)
    ViewGroup filtersView;

    @BindView(R.id.expand_arrow)
    ImageView expandArrowView;

    @BindView(R.id.filter_current)
    FilterToggleView filterCurrentView;

    @BindView(R.id.filter_ongoing)
    FilterToggleView filterOngoingView;

    @BindView(R.id.filter_past)
    FilterToggleView filterPastView;

    FilterToggleView checkedFilterView;

    private Filter selectedFilter = Filter.CURRENT;

    private OnFilterChangeListener onFilterChangeListener;

    public ExpandableFiltersView(@NonNull Context context) {
        super(context);
        init();
    }

    public ExpandableFiltersView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandableFiltersView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ExpandableFiltersView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_expandable_filters, this);
        ButterKnife.bind(this);
        if (!isInEditMode()) {
            filtersView.setVisibility(GONE);
        }

        filterCurrentView.setText(R.string.proposal_filter_current);
        filterOngoingView.setText(R.string.proposal_filter_ongoing);
        filterPastView.setText(R.string.proposal_filter_past);

        FilterToggleView.OnCheckedChangeListener onCheckedChangeListener = new FilterToggleView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(FilterToggleView toggleView, boolean isChecked) {
                onFilterChanged(toggleView, isChecked);
            }
        };

        filterCurrentView.setTag(Filter.CURRENT);
        filterOngoingView.setTag(Filter.ONGOING);
        filterPastView.setTag(Filter.PAST);

        filterCurrentView.setOnCheckedChangeListener(onCheckedChangeListener);
        filterOngoingView.setOnCheckedChangeListener(onCheckedChangeListener);
        filterPastView.setOnCheckedChangeListener(onCheckedChangeListener);

        resetFilters();
    }

    public void resetFilters() {
        checkedFilterView = filterCurrentView;
        filterCurrentView.setChecked(true);
        filterOngoingView.setChecked(false);
        filterPastView.setChecked(false);
    }

    private void onFilterChanged(FilterToggleView toggleView, boolean isChecked) {
        if (isChecked) {
            if (checkedFilterView != toggleView) {
                checkedFilterView.setChecked(false);
                checkedFilterView = toggleView;
            }

            Filter checkedFilter = (Filter) toggleView.getTag();
            if (onFilterChangeListener != null) {
                selectedFilter = checkedFilter;
                onFilterChangeListener.onFilterChange(this, checkedFilter);
            }
        }
    }

    public Filter getSelectedFilter() {
        return selectedFilter;
    }

    @OnClick(R.id.filter_header_top)
    public void onTitleViewClick() {
        if (filtersView.getVisibility() == GONE) {
            expand(filtersView);
        } else {
            collapse(filtersView);
        }
    }

    public void setOnFilterChangeListener(OnFilterChangeListener onFilterChangeListener) {
        this.onFilterChangeListener = onFilterChangeListener;
    }

    public void expand(final View view) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with anim height of 0.
        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 0.5dp/ms
        int duration = 2 * (int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density);
        anim.setDuration(duration);
        view.startAnimation(anim);

        expandArrowView.animate().rotation(180).setDuration(duration).start();
    }

    public void collapse(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 0.5dp/ms
        int duration = 2 * (int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density);
        anim.setDuration(duration);
        view.startAnimation(anim);

        expandArrowView.animate().rotation(0).setDuration(duration).start();
    }

    public enum Filter {
        CURRENT,
        ONGOING,
        PAST
    }

    public interface OnFilterChangeListener {
        void onFilterChange(ExpandableFiltersView filtersView, Filter filter);
    }
}
