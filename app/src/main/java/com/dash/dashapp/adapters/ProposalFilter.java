package com.dash.dashapp.adapters;

import android.text.TextUtils;
import android.widget.Filter;

import com.dash.dashapp.models.BudgetProposal;

import java.util.ArrayList;
import java.util.List;

public abstract class ProposalFilter extends Filter {

    private List<BudgetProposal> referenceBudgetProposalList;

    ProposalFilter(List<BudgetProposal> referenceBudgetProposalList) {
        this.referenceBudgetProposalList = referenceBudgetProposalList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (!TextUtils.isEmpty(constraint)) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<BudgetProposal> filteredBlogNews = new ArrayList<>();
            for (BudgetProposal item : referenceBudgetProposalList) {
                if (item.title.toUpperCase().contains(constraint)) {
                    filteredBlogNews.add(item);
                }
            }
            results.count = filteredBlogNews.size();
            results.values = filteredBlogNews;
        } else {
            results.count = referenceBudgetProposalList.size();
            results.values = referenceBudgetProposalList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //noinspection unchecked
        publishResults((List<BudgetProposal>) results.values);
    }

    protected abstract void publishResults(List<BudgetProposal> blogNewsFilteredList);
}