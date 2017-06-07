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
    List<String> wordList;


    public ChatActivityPrivateRecyclerAdapter(Context context, List<String> wordList) {
        this.wordList = wordList;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the xml/ view shell and return it

        View item_list_view = LayoutInflater.from(context).inflate(R.layout.chatactivity_chat_list, parent, false);


        return new Holder(item_list_view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        //populates the shell
        final DatabaseHelper myDb = new DatabaseHelper(context);
        final SQLiteDatabase connection = myDb.getWritableDatabase();
        final String cid =wordList.get(position);
        Cursor res = connection.rawQuery("Select * from users where cid='" + cid + "';", null);
        String tmpName;
        if (res.moveToNext()) {
            tmpName = res.getString(1);
        }
        else
            tmpName="Unbekannt";

        final String name =tmpName;

        holder.channelname.setText(name);
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
        return wordList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView channelname;
        CardView cw;

        public Holder(View itemView) {
            super(itemView);
            channelname = (TextView) itemView.findViewById(R.id.channelname);

            cw = (CardView) itemView.findViewById(R.id.card_view);


        }
    }
}