package com.example.chris.konferenz_app;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

public class SettingsActivityRecyclerAdapter extends RecyclerView.Adapter<SettingsActivityRecyclerAdapter.Holder> {
    Context context;
    List<Interestgroup> wordList;

    public SettingsActivityRecyclerAdapter(Context context, List<Interestgroup> wordList) {
        Log.e("is this happening", "yes");
        this.wordList = wordList;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the xml/ view shell and return it

        View item_list_view = LayoutInflater.from(context).inflate(R.layout.eventactivity_checkbox_list, parent, false);

        return new Holder(item_list_view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        //populates the shell
        Interestgroup s = wordList.get(position);
        holder.checkBox.setText(s.getName());
        if(s.isVisible()) holder.checkBox.setChecked(true);
        holder.interestgroup=s;

    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        Interestgroup interestgroup;

        public Holder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }
}