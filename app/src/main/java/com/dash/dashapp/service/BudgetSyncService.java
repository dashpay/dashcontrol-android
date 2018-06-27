package com.dash.dashapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.events.BudgetSyncCompleteEvent;
import com.dash.dashapp.events.BudgetSyncFailedEvent;
import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.models.BudgetSummary;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;

public class BudgetSyncService extends Service {

    private static final String TAG = BudgetSyncService.class.getCanonicalName();

    private DashControlClient dashControlClient;

    private Call budgetCall;
    private Call budgetHistoryCall;

    @Override
    public void onCreate() {
        super.onCreate();
        dashControlClient = DashControlClient.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        syncBudget();
        return Service.START_NOT_STICKY;
    }

    private void syncBudget() {
        budgetCall = dashControlClient.getBudget(
                new DashControlClient.Callback<Pair<BudgetSummary, List<BudgetProposal>>>() {
                    @Override
                    public void onResponse(Pair<BudgetSummary, List<BudgetProposal>> budgetResult) {
                        save(budgetResult.first, budgetResult.second, false);
                        syncBudgetHistory();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        EventBus.getDefault().post(new BudgetSyncFailedEvent(t));
                        stopSelf();
                    }
                });
    }

    private void syncBudgetHistory() {
        budgetHistoryCall = dashControlClient.getBudgetHistory(
                new DashControlClient.Callback<List<BudgetProposal>>() {
                    @Override
                    public void onResponse(List<BudgetProposal> budgetProposals) {
                        save(null, budgetProposals, true);
                        Log.d(TAG, "Proposals sync complete");
                        stopSelf();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        EventBus.getDefault().post(new BudgetSyncFailedEvent(t));
                        stopSelf();
                    }
                });
    }

    private void save(final BudgetSummary budgetSummary, final List<BudgetProposal> proposalList, final boolean historical) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.where(BudgetProposal.class)
                            .equalTo(BudgetProposal.Field.HISTORICAL, historical)
                            .findAll()
                            .deleteAllFromRealm();
                    realm.insert(proposalList);

                    if (budgetSummary != null) {
                        realm.delete(BudgetSummary.class);
                        realm.insert(budgetSummary);
                    }
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        cancelRequests();
        EventBus.getDefault().post(new BudgetSyncCompleteEvent());
        super.onDestroy();
    }

    private void cancelRequests() {
        if (budgetCall != null) {
            budgetCall.cancel();
        }
        if (budgetHistoryCall != null) {
            budgetHistoryCall.cancel();
        }
    }
}
