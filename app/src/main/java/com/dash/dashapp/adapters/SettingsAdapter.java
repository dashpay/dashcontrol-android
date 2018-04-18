package com.dash.dashapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.dash.dashapp.R;
import com.dash.dashapp.activities.MainActivity;
import com.dash.dashapp.models.SettingsModel;
import com.dash.dashapp.utils.MyDBHandler;
import com.dash.dashapp.utils.SharedPreferencesManager;

import java.util.List;

/**
 * Created by Dexter Barretto on 14/2/18.
 * Github : @dbarretto
 */

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<SettingsModel>  mRows;
    private Context context;

    public SettingsAdapter(List<SettingsModel> mRows, Context context) {
        this.mRows = mRows;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_settings, parent, false);
        viewHolder = new SettingsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SettingsViewHolder settingsViewHolder =  (SettingsViewHolder) holder;
        SettingsModel settingsModel = (SettingsModel) mRows.get(position);

        if(settingsViewHolder!=null && settingsModel != null){
            CheckedTextView checkedTextView = settingsViewHolder.getmCheckedTextView();
            checkedTextView.setText(settingsModel.getTitle());
            final String url = settingsModel.getURL();

            checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (!SharedPreferencesManager.getLanguageRSS(context).equals(url)) {
//                        SharedPreferencesManager.setLanguageRSS(context, url);
//                        MyDBHandler dbHandler = new MyDBHandler(context, null);
//                        dbHandler.deleteAllNews();
//                        context.startActivity(new Intent(context, MainActivity.class));
//                    }
                }
            });

            if(url!=null && url.equals(SharedPreferencesManager.getLanguageRSS(context))){
                checkedTextView.setChecked(true);
            }else{
                checkedTextView.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mRows.size();
    }

    private class SettingsViewHolder extends RecyclerView.ViewHolder{

        private CheckedTextView mCheckedTextView;

        public SettingsViewHolder(View itemView) {
            super(itemView);
            mCheckedTextView =  (CheckedTextView) itemView.findViewById(R.id.list_item_checked_textview);
        }

        public CheckedTextView getmCheckedTextView() {
            return mCheckedTextView;
        }

        public void setmCheckedTextView(CheckedTextView mCheckedTextView) {
            this.mCheckedTextView = mCheckedTextView;
        }
    }
}
