package com.dash.dashapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dash.dashapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.OnClick;

public class AddWalletActivity extends BaseActivity {

    @BindView(R.id.adressWalletEditText)
    EditText adressWalletEditText;
    @BindView(R.id.paymentNotificationToggle)
    ToggleButton paymentNotificationToggle;
    @BindView(R.id.qrCodeImport)
    TextView qrCodeImport;

    private String scanContent, scanFormat;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_wallet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        ab.setTitle("Add Wallet");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                scanContent = scanningResult.getContents().toString();
                scanFormat = scanningResult.getFormatName().toString();
            }

            Toast.makeText(this, scanContent + "   type:" + scanFormat, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick({
            R.id.qrCodeImport
    })
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.qrCodeImport:
                initiateQrCodeScanner();
                break;
        }

    }

    private void initiateQrCodeScanner() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setPrompt("Scan");
        scanIntegrator.setBeepEnabled(false);
        //The following line if you want QR code
        scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        scanIntegrator.setCameraId(0);
        //scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(false);
        scanIntegrator.initiateScan();
    }
}
