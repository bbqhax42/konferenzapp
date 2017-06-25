package com.example.chris.konferenz_app;

/**
 * Created by Chris on 06.06.2017.
 */


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ChatActivityPrivateRecyclerAdapter extends RecyclerView.Adapter<ChatActivityPrivateRecyclerAdapter.Holder> {
    Context context;
    List<String> cidList;


    public ChatActivityPrivateRecyclerAdapter(Context context, List<String> cidList) {
        this.cidList = cidList;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the xml/ view shell and return it
        View chatactivity_chat_list = LayoutInflater.from(context).inflate(R.layout.chatactivity_chat_list, parent, false);
        return new Holder(chatactivity_chat_list);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        //populates the shell
        final DatabaseHelper myDb = new DatabaseHelper(context);
        final SQLiteDatabase connection = myDb.getWritableDatabase();
        final String cid = cidList.get(position);


        Cursor res = connection.rawQuery("Select * from users where cid='" + cid + "';", null);
        String userName;
        if (res.moveToNext()) {
            userName = res.getString(1);
        } else
            userName = "Unbekannt";


        holder.channelName.setText(userName);
        holder.cw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatChannelPrivateActivity.class);
                intent.putExtra("ChannelName", cid); //goes to the selected channel!!
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cidList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView channelName;
        CardView cw;

        public Holder(View itemView) {
            super(itemView);
            channelName = (TextView) itemView.findViewById(R.id.channelname);
            cw = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}