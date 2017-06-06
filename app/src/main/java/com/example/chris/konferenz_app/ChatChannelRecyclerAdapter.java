package com.example.chris.konferenz_app;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ChatChannelRecyclerAdapter extends RecyclerView.Adapter<ChatChannelRecyclerAdapter.Holder> {
    Context context;
    List<ChatMessage> chatMessages;

    public ChatChannelRecyclerAdapter(Context context, List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the xml/ view shell and return it

        View item_list_view = LayoutInflater.from(context).inflate(R.layout.chatchannelactivity_chatmessage_list, parent, false);

        return new Holder(item_list_view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        //populates the shell
        final ChatMessage chatMessage = chatMessages.get(position);
        DatabaseHelper myDb = new DatabaseHelper(context);
        SQLiteDatabase connection = myDb.getWritableDatabase();

        Cursor res = connection.rawQuery("Select * from users where cid='" + chatMessage.getCid() + "';", null);
        holder.time.setText(chatMessage.getTimestamp());
        holder.message.setText(chatMessage.getContent());
        if (res.moveToNext())
            holder.user.setText(res.getString(1));
        else
            holder.user.setText("Unbekannt");

        /*holder.cw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //maybe we can view their profile?
                Intent intent = new Intent(context, EventActivity.class);
                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView user, message, time;
        CardView cw;

        public Holder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.time);
            user = (TextView) itemView.findViewById(R.id.username);
            message = (TextView) itemView.findViewById(R.id.chatmessage);
            cw = (CardView) itemView.findViewById(R.id.card_view);


        }
    }
}