package com.example.chris.konferenz_app;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ChatChannelRecyclerAdapter extends RecyclerView.Adapter<ChatChannelRecyclerAdapter.Holder> {
    Context context;
    List<ChatMessage> chatMessages;
    String channelNameString;

    public ChatChannelRecyclerAdapter(Context context, List<ChatMessage> chatMessages, String channelNameString) {
        this.chatMessages = chatMessages;
        this.context = context;
        this.channelNameString = channelNameString;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the xml/ view shell and return it

        View item_list_view = LayoutInflater.from(context).inflate(R.layout.chatchannelactivity_chatmessage_list, parent, false);

        return new Holder(item_list_view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        //populates the shell
        final ChatMessage chatMessage = chatMessages.get(position);
        final DatabaseHelper myDb = new DatabaseHelper(context);
        final SQLiteDatabase connection = myDb.getWritableDatabase();
        Cursor res = connection.rawQuery("Select * from users where cid='" + chatMessage.getCid() + "';", null);
        //Log.e("chatchannelrecycler", "Select * from users where cid='" + chatMessage.getCid() + "';");
        holder.time.setText(chatMessage.getTimestamp());
        holder.message.setText(chatMessage.getContent());
        if (res.moveToNext())
            holder.user.setText(res.getString(1));
        else
            holder.user.setText("Unbekannt");
        holder.sendenstate.setVisibility(View.INVISIBLE);
        if (chatMessage.getCid().equals(myDb.getCid(connection))) {
            if (chatMessage.isSendState() == true) {
                holder.sendenstate.setVisibility(View.INVISIBLE);
            } else {
                holder.sendenstate.setVisibility(View.VISIBLE);
                holder.sendenstate.setText("Senden fehlgeschlagen");

                holder.sendenstate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("SQL ON CLICK", "Update chatmessages SET issent=\"true\" WHERE cid='" + chatMessage.getCid() + "' AND timestamp='" + chatMessage.getTimestamp() + "' AND content='" + chatMessage.getContent() + "';");


                        connection.execSQL("Update chatmessages SET issent=\"true\" WHERE cid='" + chatMessage.getCid() + "' AND timestamp='" + chatMessage.getTimestamp() + "' AND content='" + chatMessage.getContent() + "';");
                        holder.sendenstate.setVisibility(View.INVISIBLE);
                        onClickSendMessageButton(myDb.getToken(connection), chatMessage.getContent(), connection, myDb.getCid(connection), chatMessage.getTimestamp());
                    }
                });

            }

        }

        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("das habe ich", "nicht geklickt");
                if (!(chatMessage.getCid().equals(myDb.getCid(connection)))) {
                    Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra("Cid", chatMessage.getCid());
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView user, message, time, sendenstate;
        CardView cw;

        public Holder(View itemView) {
            super(itemView);
            sendenstate = (TextView) itemView.findViewById(R.id.sendenstate);
            time = (TextView) itemView.findViewById(R.id.time);
            user = (TextView) itemView.findViewById(R.id.username);
            message = (TextView) itemView.findViewById(R.id.chatmessage);
            cw = (CardView) itemView.findViewById(R.id.card_view);


        }
    }


    private void onClickSendMessageButton(String token, final String messageToSend, final SQLiteDatabase connection, final String cid, final String timestamp) {
        RequestQueue queue = Volley.newRequestQueue(context);


        String url = Config.webserviceUrl + "CHAT.SEND?token=" + token + "&channel=" + channelNameString + "&content=" + messageToSend.trim();

        Log.e("Chat Message URL", url);

        if (messageToSend.trim().length() != 0) {

            JsonObjectRequest documentRequestRequest =
                    new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Config.error_message(context, "Nachricht erfolgreich gesendet");
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Config.error_message(context, "Senden fehlgeschlagen");
                            connection.execSQL("Update chatmessages SET issent=\"false\" WHERE cid='" + cid + "' AND  timestamp='" + timestamp + "'  AND content='" + messageToSend + "';");
                            Log.e("ChatActivity SQL", "Update chatmessages SET issent=\"false\" WHERE cid='" + cid + "' AND  timestamp='" + timestamp + "'  AND content='" + messageToSend + "';");
                        }
                    });

            queue.add(documentRequestRequest);
        } else Config.error_message(context, "Leere Nachrichten sind verboten");
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(c.getTime());
    }
}