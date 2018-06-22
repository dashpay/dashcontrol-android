package com.dash.dashapp.adapters;

import android.widget.ImageView;
import android.widget.TextView;

import com.dash.dashapp.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;

import java.util.Locale;

@Parent
@Layout(R.layout.portfolio_parent_view)
public class PortfolioParentView {

    @View(R.id.title)
    TextView titleView;

    @View(R.id.balance)
    TextView balanceView;

    @View(R.id.expand_arrow)
    ImageView expandArrowView;

    @ParentPosition
    int parentPosition;

    private String heading;

    private long balance;

    private boolean expanded;

    public PortfolioParentView(String heading) {
        this.heading = heading;
        this.balance = 0;
    }

    @Resolve
    public void onResolved() {
        titleView.setText(heading);
        String balanceFormat = String.format(Locale.US, "%.8f", balance / 100000000F);
        balanceView.setText(balanceFormat);
        if (expanded) {
            expandArrowView.setRotation(180);
        }
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    @Expand
    public void onExpand() {
        expanded = true;
        if (expandArrowView != null) {
            expandArrowView.animate().rotation(180).setDuration(300).start();
        }
    }

    @Collapse
    public void onCollapse() {
        expanded = false;
        if (expandArrowView != null) {
            expandArrowView.animate().rotation(0).setDuration(300).start();
        }
    }
}
