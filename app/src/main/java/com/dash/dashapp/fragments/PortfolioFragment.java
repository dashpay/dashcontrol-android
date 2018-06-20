package com.dash.dashapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.activities.AddMasternodeActivity;
import com.dash.dashapp.activities.AddWalletActivity;
import com.dash.dashapp.adapters.PortfolioChildView;
import com.dash.dashapp.adapters.PortfolioParentView;
import com.dash.dashapp.api.DashControlClient;
import com.dash.dashapp.api.data.InsightResponse;
import com.dash.dashapp.models.PortfolioEntry;
import com.github.clans.fab.FloatingActionMenu;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PortfolioFragment extends Fragment {

    private static final int REQUEST_ADD_WALLET = 100;
    private static final int REQUEST_EDIT_WALLET = 200;
    private static final int REQUEST_ADD_MASTERNODE = 300;

    @BindView(R.id.floating_menu)
    FloatingActionMenu floatingMenu;

    @BindView(R.id.expandable_list)
    ExpandablePlaceHolderView portfolioListView;

    private PortfolioParentView walletParentView;
    private PortfolioParentView masternodeParentView;

    private Unbinder unbinder;

    public PortfolioFragment() {
        // Required empty public constructor
    }

    public static PortfolioFragment newInstance() {
        PortfolioFragment fragment = new PortfolioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        unbinder = ButterKnife.bind(this, view);

        masternodeParentView = new PortfolioParentView(getString(R.string.portfolio_my_masternodes));
        walletParentView = new PortfolioParentView(getString(R.string.portfolio_my_wallets));

        return view;
    }

    @OnClick(R.id.add_wallet)
    public void onAddWalletClick() {
        Intent intent = AddWalletActivity.createIntent(getActivity());
        startActivityForResult(intent, REQUEST_ADD_WALLET);
    }

    @OnClick(R.id.add_masternode)
    public void onAddMasternodeClick() {
        Intent intent = new Intent(getActivity(), AddMasternodeActivity.class);
        startActivityForResult(intent, REQUEST_ADD_MASTERNODE);
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadPortfolio();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        floatingMenu.close(false);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_WALLET || requestCode == REQUEST_ADD_MASTERNODE) {
            if (resultCode == Activity.RESULT_OK) {
                reloadPortfolio();
            }
        }
    }

    private void reloadPortfolio() {
        refreshViews();
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmQuery<PortfolioEntry> whereQuery = realm.where(PortfolioEntry.class);
            RealmResults<PortfolioEntry> queryResult = whereQuery.findAll();
            List<PortfolioEntry> portfolioEntries = new ArrayList<>(queryResult);
            final Call<List<InsightResponse>> utxoCall = DashControlClient.getInstance().getUtxos(portfolioEntries);
            utxoCall.enqueue(new Callback<List<InsightResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<InsightResponse>> call, @NonNull Response<List<InsightResponse>> response) {
                    if (response.isSuccessful()) {
                        final List<InsightResponse> utxoList = Objects.requireNonNull(response.body());
                        updateBalances(utxoList);
                        refreshViews();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<InsightResponse>> call, @NonNull Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateBalances(final List<InsightResponse> utxoList) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    PortfolioEntry portfolioEntry = null;
                    for (InsightResponse utxo : utxoList) {
                        if (portfolioEntry == null || !portfolioEntry.pubKey.equals(utxo.address)) {
                            portfolioEntry = realm.where(PortfolioEntry.class)
                                    .equalTo(PortfolioEntry.Field.PUB_KEY, utxo.address)
                                    .findFirst();
                            if (portfolioEntry == null) {
                                continue;
                            }
                            portfolioEntry.balance = 0;
                        }
                        portfolioEntry.balance += utxo.satoshis;
                    }
                }
            });
        }
    }

    private void refreshViews() {
        clearPortfolioListView();
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<PortfolioEntry> portfolioEntriesQueryResult = realm.where(PortfolioEntry.class)
                    .equalTo(PortfolioEntry.Field.TYPE, PortfolioEntry.Type.WALLET.name())
                    .findAll();
            List<PortfolioEntry> portfolioEntries = realm.copyFromRealm(portfolioEntriesQueryResult);
            long balance = 0;
            for (PortfolioEntry entry : portfolioEntries) {
                portfolioListView.addChildView(walletParentView, new PortfolioChildView(entry, onItemClickListener));
                balance += entry.balance;
            }
            walletParentView.setBalance(balance);
            if (portfolioEntries.size() > 0) {
                expandWalletParentView();
            }
        }
    }

    private PortfolioChildView.OnItemClickListener onItemClickListener = new PortfolioChildView.OnItemClickListener() {
        @Override
        public void onItemClick(PortfolioEntry entry) {
            Intent intent = AddWalletActivity.createIntent(getActivity(), entry.id);
            startActivityForResult(intent, REQUEST_EDIT_WALLET);
        }
    };

    private void clearPortfolioListView() {
        portfolioListView.removeAllViews();
        portfolioListView.addView(masternodeParentView);
        portfolioListView.addView(walletParentView);
    }

    private void expandWalletParentView() {
        try {
            portfolioListView.expand(walletParentView);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
