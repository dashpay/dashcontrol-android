package com.dash.dashapp.Interface;

import com.dash.dashapp.Model.News;
import com.dash.dashapp.Model.Proposal;

import java.util.ArrayList;

/**
 * Created by sebas on 8/7/2017.
 */

public interface ProposalUpdateListener {

    void onUpdateStarted();
    void onFirstBatchProposalsCompleted(ArrayList<Proposal> newsList);
    void onDatabaseUpdateCompleted();


}
