package com.example.chris.konferenz_app;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

public class EventActivityRecyclerAdapter extends RecyclerView.Adapter<EventActivityRecyclerAdapter.Holder> {
    Context context;
    List<Document> documentList;

    public EventActivityRecyclerAdapter(Context context, List<Document> documentList) {
        this.documentList = documentList;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the xml/ view shell and return it
        View eventActivity_CheckBox_List = LayoutInflater.from(context).inflate(R.layout.eventactivity_checkbox_list, parent, false);
        return new Holder(eventActivity_CheckBox_List);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        //populates the shell
        Document s = documentList.get(position);
        holder.checkBox.setText(s.getTitle());
        holder.doc = s;

    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        Document doc;

        public Holder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }
}