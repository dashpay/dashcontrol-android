package com.dash.dashapp.helpers;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class AssetsHelper {

    private static final String PROPOSAL_DESCRIPTION_TEMPLATE_FILE = "ProposalDescriptionTemplate.html";

    public static String applyProposalDescriptionTemplate(Context context, String descriptionContent) {
        InputStream input = null;
        try {
            input = context.getAssets().open(PROPOSAL_DESCRIPTION_TEMPLATE_FILE);
            int size = input.available();
            byte[] buffer = new byte[size];
            int read = input.read(buffer);
            if (read > 0) {
                String proposalDescriptionTemplate = new String(buffer);
                return proposalDescriptionTemplate.replace("%@", descriptionContent);
            }
        } catch (IOException e) {
            // ignore
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        throw new IllegalStateException("Unable to load ProposalDescriptionTemplate.html from assets");
    }
}
