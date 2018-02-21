package com.dash.dashapp.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.dash.dashapp.interfaces.ProposalUpdateListener;
import com.dash.dashapp.models.Comment;
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

            // Getting the comments for each proposal
            MyDBHandler dbHandler = new MyDBHandler(context, null);
            List<Proposal> proposalList = dbHandler.findAllProposals(null);

            for (Proposal proposal : proposalList ){
                new UpdateCommentDB().execute(proposal.getHash());
            }

            super.onPostExecute(aVoid);
        }
    }



    public class UpdateCommentDB extends AsyncTask<String, Void, Void> {

        public UpdateCommentDB() {
        }

        // required methods

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                String hashProposal = params[0];
                String URL_COMMENT_LIST = "https://www.dashcentral.org/api/v1/proposal?hash=" + hashProposal; //change Object to required type

                MyDBHandler dbHandler = new MyDBHandler(context, null);

                InputStream jsonContent = HttpUtil.httpRequest(URL_COMMENT_LIST);

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(jsonContent, "UTF-8"));

                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject proposalJson = new JSONObject(responseStrBuilder.toString());

                JSONObject proposalObject = proposalJson.getJSONObject("proposal");

                JSONArray array = proposalJson.getJSONArray("comments");
                for(int i = 0 ; i < array.length() ; i++){
                    Comment comment = new Comment();
                    comment.setHashProposal(proposalObject.getString("hash"));
                    comment.setId(array.getJSONObject(i).getString("id"));
                    comment.setUsername(array.getJSONObject(i).getString("username"));
                    comment.setDate(array.getJSONObject(i).getString("date"));
                    comment.setDate_human(array.getJSONObject(i).getString("date_human"));
                    comment.setOrder(array.getJSONObject(i).getInt("order"));
                    comment.setLevel(array.getJSONObject(i).getInt("level"));
                    comment.setRecently_posted(array.getJSONObject(i).getBoolean("recently_posted"));
                    comment.setPosted_by_owner(array.getJSONObject(i).getBoolean("posted_by_owner"));
                    comment.setReply_url(array.getJSONObject(i).getString("reply_url"));
                    comment.setContent(array.getJSONObject(i).getString("content"));
                    dbHandler.addComments(comment);
                }
                jsonContent.close();

            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }
}
