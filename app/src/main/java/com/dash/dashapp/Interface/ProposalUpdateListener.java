package com.dash.dashapp.Interface;

import com.dash.dashapp.Model.Proposal;

import java.util.List;

/**
 * Created by sebas on 8/7/2017.
 */

public interface ProposalUpdateListener {

    void onUpdateStarted();

    void onFirstBatchProposalsCompleted(List<Proposal> newsList);

    void onDatabaseUpdateCompleted();


}
