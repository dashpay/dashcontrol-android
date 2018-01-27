package com.dash.dashapp.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.dash.dashapp.interfaces.ProposalUpdateListener;
import com.dash.dashapp.models.Proposal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebas on 8/26/2017.
 */

public class JsonUtil {

    private static final int NUMBER_FIRST_BATCH = 10;
    private ProposalUpdateListener dbProposalListener;
    public Context context = null;

    public JsonUtil(Context context) {
        this.context = context;
    }

    public void fetchProposalJson(ProposalUpdateListener dbProposalListener) {
        this.dbProposalListener = dbProposalListener;
        UpdateProposalDB updateDB = new UpdateProposalDB(dbProposalListener);
        updateDB.execute();
    }

    public class UpdateProposalDB extends AsyncTask<Void, Void, Void> {
        private static final String URL_PROPOSAL_LIST = "https://www.dashcentral.org/api/v1/budget"; //change Object to required type
        private ProposalUpdateListener dbProposalListener;

        public UpdateProposalDB(ProposalUpdateListener dbProposalListener) {
            this.dbProposalListener = dbProposalListener;
        }

        // required methods

        @Override
        protected void onPreExecute() {
            dbProposalListener.onUpdateStarted();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                MyDBHandler dbHandler = new MyDBHandler(context, null);

                InputStream jsonContent = HttpUtil.httpRequest(URL_PROPOSAL_LIST);

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(jsonContent, "UTF-8"));

                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject proposalJson = new JSONObject(responseStrBuilder.toString());

                List<Proposal> list = new ArrayList<>();
                JSONArray array = proposalJson.getJSONArray("proposals");
                for(int i = 0 ; i < array.length() ; i++){

                    if (i == NUMBER_FIRST_BATCH){
                        dbProposalListener.onFirstBatchProposalsCompleted(list);
                    }

                    Proposal proposal = new Proposal();
                    proposal.setHash(array.getJSONObject(i).getString("hash"));
                    proposal.setName(array.getJSONObject(i).getString("name"));
                    proposal.setUrl(array.getJSONObject(i).getString("url"));
                    proposal.setDw_url(array.getJSONObject(i).getString("dw_url"));
                    proposal.setDw_url_comments(array.getJSONObject(i).getString("dw_url_comments"));
                    proposal.setTitle(array.getJSONObject(i).getString("title"));
                    proposal.setDate_added(array.getJSONObject(i).getString("date_added"));
                    proposal.setDate_added_human(array.getJSONObject(i).getString("date_added_human"));
                    proposal.setDate_end(array.getJSONObject(i).getString("date_end"));
                    proposal.setVoting_deadline_human(array.getJSONObject(i).getString("voting_deadline_human"));
                    proposal.setWill_be_funded(array.getJSONObject(i).getBoolean("will_be_funded"));
                    proposal.setRemaining_yes_votes_until_funding(array.getJSONObject(i).getInt("remaining_yes_votes_until_funding"));
                    proposal.setIn_next_budget(array.getJSONObject(i).getBoolean("in_next_budget"));
                    proposal.setMonthly_amount(array.getJSONObject(i).getInt("monthly_amount"));
                    proposal.setTotal_payment_count(array.getJSONObject(i).getInt("total_payment_count"));
                    proposal.setRemaining_payment_count(array.getJSONObject(i).getInt("remaining_payment_count"));
                    proposal.setYes(array.getJSONObject(i).getInt("yes"));
                    proposal.setNo(array.getJSONObject(i).getInt("no"));
                    proposal.setOrder(array.getJSONObject(i).getInt("order"));
                    proposal.setComment_amount(array.getJSONObject(i).getInt("comment_amount"));
                    proposal.setOwner_username(array.getJSONObject(i).getString("owner_username"));
                    list.add(proposal);
                    dbHandler.addProposal(proposal);
                }

                jsonContent.close();

            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dbProposalListener.onDatabaseUpdateCompleted();
            super.onPostExecute(aVoid);
        }
    }
}
