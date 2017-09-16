package com.dash.dashapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.dash.dashapp.Model.Proposal;
import com.dash.dashapp.R;

public class ProposalDetailActivity extends AppCompatActivity {

    private static final String CONTENT_PROPOSAL = "proposal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_proposal);

        Intent intent = getIntent();
        Proposal proposal = (Proposal) intent.getSerializableExtra("CONTENT_PROPOSAL");



    }
}
