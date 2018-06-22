package com.dash.dashapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.models.PortfolioEntry;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;

public class AddPortfolioEntryActivity extends BaseActivity {

    private static String EXTRA_PORTFOLIO_ENTRY_TYPE = "extra_portfolio_entry_type";
    private static String EXTRA_PORTFOLIO_ENTRY_ID = "extra_portfolio_entry_id";

    @BindView(R.id.input_label)
    EditText labelView;

    @BindView(R.id.input_public_key)
    EditText addressView;

    @BindView(R.id.input_voting_key)
    EditText votingKeyView;

    @BindView(R.id.include_masternode_earnings)
    Switch includeEarningsView;

    @BindView(R.id.payment_notification)
    Switch paymentNotificationView;

    private boolean masternodeMode;

    private PortfolioEntry editedPortfolioEntry;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_portfolio_entry;
    }

    public static Intent createIntent(Context context, PortfolioEntry.Type type) {
        Intent intent = new Intent(context, AddPortfolioEntryActivity.class);
        intent.putExtra(EXTRA_PORTFOLIO_ENTRY_TYPE, type.name());
        return intent;
    }

    public static Intent createIntent(Context context, PortfolioEntry portfolioEntry) {
        Intent intent = createIntent(context, portfolioEntry.getType());
        intent.putExtra(EXTRA_PORTFOLIO_ENTRY_ID, portfolioEntry.id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackAction();
        init();
    }

    private void init() {
        Bundle extras = Objects.requireNonNull(getIntent().getExtras());

        String mode = extras.getString(EXTRA_PORTFOLIO_ENTRY_TYPE);
        masternodeMode = PortfolioEntry.Type.MASTERNODE.name().equals(mode);
        includeEarningsView.setVisibility(masternodeMode ? View.VISIBLE : View.GONE);
        votingKeyView.setVisibility(masternodeMode ? View.VISIBLE : View.GONE);

        if (extras.containsKey(EXTRA_PORTFOLIO_ENTRY_ID)) {
            setTitle(masternodeMode ? R.string.add_portfolio_entry_edit_masternode : R.string.add_portfolio_entry_edit_wallet);
            String editedPortfolioEntryId = extras.getString(EXTRA_PORTFOLIO_ENTRY_ID);
            editedPortfolioEntry = findEntryById(editedPortfolioEntryId);
            if (editedPortfolioEntry == null) {
                finish();
                return;
            }
            labelView.setText(editedPortfolioEntry.label);
            addressView.setText(editedPortfolioEntry.pubKey);
            votingKeyView.setText(editedPortfolioEntry.votingKey);
            includeEarningsView.setChecked(editedPortfolioEntry.includeEarnings);
        } else {
            labelView.setText(entryDefaultName());
        }
    }

    private String entryDefaultName() {
        try (Realm realm = Realm.getDefaultInstance()) {
            PortfolioEntry.Type entryType;
            int formatResId;
            if (masternodeMode) {
                entryType = PortfolioEntry.Type.MASTERNODE;
                formatResId = R.string.add_portfolio_entry_masternode_label_format;
            } else {
                entryType = PortfolioEntry.Type.WALLET;
                formatResId = R.string.add_portfolio_entry_wallet_label_format;
            }
            long walletsCount = realm.where(PortfolioEntry.class)
                    .equalTo(PortfolioEntry.Field.TYPE, entryType.name())
                    .count();
            return getString(formatResId, walletsCount + 1);
        }
    }

    @OnCheckedChanged(R.id.payment_notification)
    public void onPaymentNotificationCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Toast.makeText(this, R.string.not_yet_supported, Toast.LENGTH_LONG).show();
            buttonView.setChecked(false);
        }
    }

    public void onSaveActionSelected() {
        String address = addressView.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, R.string.add_portfolio_entry_empty_address, Toast.LENGTH_LONG).show();
            return;
        }
        String label = labelView.getText().toString().trim();
        if (TextUtils.isEmpty(label)) {
            label = entryDefaultName();
        }
        String votingKey = votingKeyView.getText().toString().trim();
        boolean includeEarnings = includeEarningsView.isChecked();
        boolean saved = saveWalletIfNotExist(address, label, votingKey, includeEarnings);
        if (saved) {
            setResult(RESULT_OK);
            finish();
        }
    }

    public void onDeleteActionSelected() {
        if (editedPortfolioEntry == null) {
            return;
        }
        deleteEntryById(editedPortfolioEntry.id);
        setResult(RESULT_OK);
        finish();
    }

    private boolean saveWalletIfNotExist(final String publicKey, final String label,
                                         final String votingKey, final boolean includeEarnings) {
        PortfolioEntry wallet = findEntryByAddress(publicKey);
        if (wallet != null) {
            if (editedPortfolioEntry == null || !wallet.id.equals(editedPortfolioEntry.id)) {
                Toast.makeText(this, getString(R.string.add_portfolio_entry_public_key_already_added, wallet.label),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    if (editedPortfolioEntry == null) {
                        PortfolioEntry.Type entryType = masternodeMode
                                ? PortfolioEntry.Type.MASTERNODE : PortfolioEntry.Type.WALLET;
                        editedPortfolioEntry = new PortfolioEntry(entryType);
                    }
                    editedPortfolioEntry.pubKey = publicKey;
                    editedPortfolioEntry.label = label;
                    editedPortfolioEntry.votingKey = votingKey;
                    editedPortfolioEntry.includeEarnings = includeEarnings;
                    editedPortfolioEntry.balance = 0;
                    realm.insertOrUpdate(editedPortfolioEntry);
                }
            });
        }
        return true;
    }

    private PortfolioEntry findEntryByAddress(String address) {
        try (Realm realm = Realm.getDefaultInstance()) {
            PortfolioEntry entry = realm.where(PortfolioEntry.class)
                    .equalTo(PortfolioEntry.Field.PUB_KEY, address)
                    .findFirst();
            return entry != null ? realm.copyFromRealm(entry) : null;
        }
    }

    private void deleteEntryById(final String id) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.where(PortfolioEntry.class)
                            .equalTo(PortfolioEntry.Field.ID, id)
                            .findAll()
                            .deleteAllFromRealm();
                }
            });
        }
    }

    private PortfolioEntry findEntryById(String id) {
        try (Realm realm = Realm.getDefaultInstance()) {
            PortfolioEntry entry = realm.where(PortfolioEntry.class)
                    .equalTo(PortfolioEntry.Field.ID, id)
                    .findFirst();
            return entry != null ? realm.copyFromRealm(entry) : null;
        }
    }

    @OnClick(R.id.scan_qr_code)
    public void onScanQrCodeClick() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setPrompt(getString(R.string.add_portfolio_entry_scan_qr_code));
        scanIntegrator.setBeepEnabled(false);
        scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        scanIntegrator.setCameraId(0);
        scanIntegrator.setBarcodeImageEnabled(false);
        scanIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null && !TextUtils.isEmpty(scanningResult.getContents())) {
            String scanContent = scanningResult.getContents();
            addressView.setText(scanContent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_portfolio_entry, menu);
        MenuItem deleteActionItem = menu.findItem(R.id.action_delete);
        deleteActionItem.setVisible(editedPortfolioEntry != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                onSaveActionSelected();
                return true;
            }
            case R.id.action_delete: {
                onDeleteActionSelected();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
