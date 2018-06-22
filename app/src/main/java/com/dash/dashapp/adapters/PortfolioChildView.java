package com.dash.dashapp.adapters;

import android.widget.TextView;

import com.dash.dashapp.R;
import com.dash.dashapp.models.PortfolioEntry;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.ChildPosition;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;

import java.util.Locale;

@Layout(R.layout.portfolio_child_view)
public class PortfolioChildView {

    @View(R.id.title)
    TextView titleView;

    @View(R.id.balance)
    TextView balanceView;

    @ParentPosition
    int parentPosition;

    @ChildPosition
    int childPosition;

    private OnItemClickListener listener;

    private PortfolioEntry portfolioEntry;

    public PortfolioChildView(PortfolioEntry portfolioEntry, OnItemClickListener listener) {
        this.portfolioEntry = portfolioEntry;
        this.listener = listener;
    }

    @Resolve
    public void onResolved() {
        titleView.setText(portfolioEntry.label + "\n" + portfolioEntry.pubKey);
        String balanceFormat = String.format(Locale.US, "%.8f", portfolioEntry.balance / 100000000F);
        balanceView.setText(balanceFormat);
    }

    @Click(R.id.root)
    public void onClick() {
        if (listener != null) {
            listener.onItemClick(portfolioEntry);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PortfolioEntry entry);
    }
}
