package com.dash.dashapp.adapters;

import android.text.TextUtils;
import android.widget.Filter;

import com.dash.dashapp.models.BudgetProposal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ProposalFilter extends Filter {

    private List<BudgetProposal> referenceBudgetProposalList;

    private boolean showOnlyOngoing;
    private boolean showOnlyPast;

    ProposalFilter(List<BudgetProposal> referenceBudgetProposalList, boolean showOnlyOngoing, boolean showOnlyPast) {
        this.referenceBudgetProposalList = referenceBudgetProposalList;
        this.showOnlyOngoing = showOnlyOngoing;
        this.showOnlyPast = showOnlyPast;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        List<BudgetProposal> filteredBlogNews = new ArrayList<>();
        if (!TextUtils.isEmpty(constraint)) {
            constraint = constraint.toString().toUpperCase();
            for (BudgetProposal item : referenceBudgetProposalList) {
                if (item.title.toUpperCase().contains(constraint)) {
                    filteredBlogNews.add(item);
                }
            }
        } else {
            filteredBlogNews.addAll(referenceBudgetProposalList);
        }

        for (Iterator<BudgetProposal> iterator = filteredBlogNews.iterator(); iterator.hasNext(); ) {
            BudgetProposal proposal = iterator.next();
            boolean toBeRemoved = (showOnlyOngoing && !proposal.isOngoing());
            toBeRemoved |= (showOnlyPast && !proposal.isPast());
            if (toBeRemoved) {
                iterator.remove();
            }
        }

        results.count = filteredBlogNews.size();
        results.values = filteredBlogNews;

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //noinspection unchecked
        publishResults(constraint, (List<BudgetProposal>) results.values);
    }

    protected abstract void publishResults(CharSequence constraint, List<BudgetProposal> blogNewsFilteredList);
}