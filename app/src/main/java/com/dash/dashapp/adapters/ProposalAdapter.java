package com.dash.dashapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.dash.dashapp.R;
import com.dash.dashapp.models.BudgetProposal;

import java.util.ArrayList;
import java.util.List;

public class ProposalAdapter extends RecyclerView.Adapter<ProposalHolder> implements Filterable {

    private List<BudgetProposal> budgetProposalList;
    private List<BudgetProposal> referenceBudgetProposalList;

    public ProposalAdapter() {
        this.budgetProposalList = new ArrayList<>();
        this.referenceBudgetProposalList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProposalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.proposal_view, parent, false);
        return new ProposalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProposalHolder holder, int position) {
        BudgetProposal budgetProposal = budgetProposalList.get(position);
        holder.bind(budgetProposal);
    }

    @Override
    public int getItemCount() {
        return budgetProposalList.size();
    }

    public void clear() {
        budgetProposalList.clear();
        referenceBudgetProposalList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<BudgetProposal> budgetProposalList) {
        this.budgetProposalList.addAll(budgetProposalList);
        referenceBudgetProposalList.addAll(budgetProposalList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new ProposalFilter(referenceBudgetProposalList) {
            @Override
            protected void publishResults(List<BudgetProposal> budgetProposalFilteredList) {
                budgetProposalList = budgetProposalFilteredList;
                notifyDataSetChanged();
            }
        };
    }
}