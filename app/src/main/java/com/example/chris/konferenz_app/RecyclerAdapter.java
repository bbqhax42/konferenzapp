package com.example.chris.konferenz_app;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    Context context;
    List<String> wordList;

    public RecyclerAdapter(Context context, List<String> wordList) {
        this.wordList = wordList;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the xml/ view shell and return it

        View item_list_view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);

        return new Holder(item_list_view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        //populates the shell
        String s = wordList.get(position);
        holder.checkBox.setText(s);
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        CheckBox checkBox;

        public Holder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.text);
        }
    }
}