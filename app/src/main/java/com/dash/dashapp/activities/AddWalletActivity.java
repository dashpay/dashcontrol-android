package com.dash.dashapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dash.dashapp.R;
import com.dash.dashapp.models.PortfolioEntry;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;

public class AddWalletActivity extends BaseActivity {

    private static String EXTRA_PORTFOLIO_ENTRY_ID = "extra_portfolio_entry_id";

    @BindView(R.id.input_label)
    EditText labelView;

    @BindView(R.id.input_public_key)
    EditText publicKeyView;

    @BindView(R.id.payment_notification)
    Switch paymentNotificationView;

    @BindView(R.id.scan_qr_code)
    TextView scanQrCodeView;

    private PortfolioEntry editedPortfolioEntry;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_wallet;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, AddWalletActivity.class);
    }

    public static Intent createIntent(Context context, String portfolioEntryId) {
        Intent intent = createIntent(context);
        intent.putExtra(EXTRA_PORTFOLIO_ENTRY_ID, portfolioEntryId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackAction();
        setTitle(R.string.add_wallet);
        init();
    }

    private void init() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_PORTFOLIO_ENTRY_ID)) {
            String editedPortfolioEntryId = extras.getString(EXTRA_PORTFOLIO_ENTRY_ID);
            editedPortfolioEntry = findWalletById(editedPortfolioEntryId);
            if (editedPortfolioEntry == null) {
                finish();
                return;
            }
            labelView.setText(editedPortfolioEntry.label);
            publicKeyView.setText(editedPortfolioEntry.pubKey);
        } else {
            labelView.setText(defaultWalletName());
            publicKeyView.setText(null);
        }
    }

    private String defaultWalletName() {
        try (Realm realm = Realm.getDefaultInstance()) {
            long walletsCount = realm.where(PortfolioEntry.class)
                    .equalTo(PortfolioEntry.Field.TYPE, PortfolioEntry.Type.WALLET.name())
                    .count();
            return getString(R.string.add_wallet_default_label_format, walletsCount + 1);
        }
    }

    @OnCheckedChanged(R.id.payment_notification)
    public void onPaymentNotificationCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Toast.makeText(this, R.string.not_yet_supported, Toast.LENGTH_LONG).show();
            buttonView.setChecked(false);
        }
    }

    @OnClick(R.id.add_wallet_fab)
    public void onAddWalletClick() {
        String publicKey = publicKeyView.getText().toString().trim();
        if (TextUtils.isEmpty(publicKey)) {
            Toast.makeText(this, R.string.add_wallet_empty_address, Toast.LENGTH_LONG).show();
            return;
        }
        String label = labelView.getText().toString().trim();
        if (TextUtils.isEmpty(label)) {
            label = defaultWalletName();
        }
        boolean saved = saveWalletIfNotExist(publicKey, label);
        if (saved) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean saveWalletIfNotExist(final String publicKey, final String label) {
        PortfolioEntry wallet = findWalletByPublicKey(publicKey);
        if (wallet != null) {
            if (editedPortfolioEntry == null || !wallet.id.equals(editedPortfolioEntry.id)) {
                Toast.makeText(this, getString(R.string.add_wallet_public_key_already_added, wallet.label), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    if (editedPortfolioEntry == null) {
                        editedPortfolioEntry = new PortfolioEntry(PortfolioEntry.Type.WALLET);
                    }
                    editedPortfolioEntry.pubKey = publicKey;
                    editedPortfolioEntry.label = label;
                    editedPortfolioEntry.balance = 0;
                    realm.insertOrUpdate(editedPortfolioEntry);
                }
            });
        }
        return true;
    }

    private PortfolioEntry findWalletByPublicKey(String publicKey) {
        try (Realm realm = Realm.getDefaultInstance()) {
            PortfolioEntry entry = realm.where(PortfolioEntry.class)
                    .equalTo(PortfolioEntry.Field.PUB_KEY, publicKey)
                    .findFirst();
            return entry != null ? realm.copyFromRealm(entry) : null;
        }
    }

    private PortfolioEntry findWalletById(String id) {
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
        scanIntegrator.setPrompt(getString(R.string.add_wallet_scan_qr_code));
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
            publicKeyView.setText(scanContent);
        }
    }
}
