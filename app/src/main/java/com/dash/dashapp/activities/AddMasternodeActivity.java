package com.dash.dashapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dash.dashapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.OnClick;

public class AddMasternodeActivity extends BaseActivity {

    @BindView(R.id.adressEditText)
    EditText addressEditText;
    @BindView(R.id.includeMasterNodeToggle)
    ToggleButton includeMasterNodeToggle;
    @BindView(R.id.paymentNotifToggle)
    ToggleButton paymentNotifToggle;
    @BindView(R.id.qrCodeImport)
    TextView qrCodeImport;
    @BindView(R.id.btn_AddMasterNode)
    Button addMasterNode;

    private String scanContent, scanFormat;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_masternode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        ab.setTitle("Add Masternode");

        addMasterNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEditText.getText().toString().trim();
                if(address!=null && !address.isEmpty()){
                    Intent data = new Intent();
                    setResult(RESULT_OK,data);
                    finish();
                }
                else{
                    addressEditText.setError("Address cannot be empty!");
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                scanContent = scanningResult.getContents().toString();
                scanFormat = scanningResult.getFormatName().toString();
                if (scanFormat.equals("QR_CODE")) addressEditText.setText(scanContent);
            }

        } else {
            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent data = new Intent();
        setResult(RESULT_CANCELED,data);
        finish();
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
