package com.example.chris.konferenz_app.activities;

/**
 * Created by Chris on 06.06.2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.content.BroadcastReceiver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chris.konferenz_app.adapters.ChatChannelRecyclerAdapter;
import com.example.chris.konferenz_app.data.ChatMessage;
import com.example.chris.konferenz_app.responses.ChatSendResponse;
import com.example.chris.konferenz_app.Config;
import com.example.chris.konferenz_app.DatabaseHelper;
import com.example.chris.konferenz_app.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatChannelActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper myDb = new DatabaseHelper(this);
    ArrayList<ChatMessage> messages;
    String channelNameString;


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            SQLiteDatabase connection = myDb.getWritableDatabase();
            populateView(connection);
            Log.e("onReceive", "View Populated");
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button chatButton, settingsButton, homeButton, sendButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatchannel);
        this.registerReceiver(receiver, new IntentFilter("MsgSent"));

        final TextView channelName;
        final EditText messageToSend;
        chatButton = (Button) findViewById(R.id.chatbutton);
        settingsButton = (Button) findViewById(R.id.settingsbutton);
        homeButton = (Button) findViewById(R.id.homebutton);
        messageToSend = (EditText) findViewById(R.id.messagetosend);
        sendButton = (Button) findViewById(R.id.sendbutton);
        channelName = (TextView) findViewById(R.id.title);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //changes the button background to show which part of the program the user is accessing
        chatButton.setBackgroundResource(R.drawable.toolbar_button_selected);

        channelNameString = getIntent().getStringExtra("ChannelName");
        channelName.setText("Thema: " + channelNameString);

        final SQLiteDatabase connection = myDb.getWritableDatabase();


        final String token = myDb.getToken(connection);
        final String cid = myDb.getCid(connection);

        populateView(connection);


        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                                   @Override
                                                   public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                                       recyclerView.scrollToPosition(messages.size() - 1);
                                                   }
                                               }
        );

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("EventUpdate", false + ""); //true if not logged in today yet, false if already logged in today
                startActivity(intent);
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendMessageButton(token, messageToSend, connection, cid);
            }
        });

        //resizes window if keyboard pops up
       /* getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);*/
    }


    private void onClickSendMessageButton(String token, final EditText messageToSend, final SQLiteDatabase connection, final String cid) {
        RequestQueue queue = Volley.newRequestQueue(ChatChannelActivity.this);


        String url = Config.webserviceUrl + "CHAT.SEND?token=" + token + "&channel=" + channelNameString + "&content=" + messageToSend.getText().toString().trim();

        Log.e("Chat Message URL", url);

        if (messageToSend.getText().toString().trim().length() != 0) {

            JsonObjectRequest chatSendRequest =
                    new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Gson gson = new Gson();

                            ChatSendResponse chatSendResponse = gson.fromJson(jsonObject.toString(), ChatSendResponse.class);
                            StringBuilder stringBuilder = new StringBuilder();
                            if (chatSendResponse.getSuccess().equalsIgnoreCase("true")) {
                                Config.error_message(ChatChannelActivity.this, "Nachricht erfolgreich gesendet");
                                stringBuilder.append("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('");
                                stringBuilder.append(channelNameString);
                                stringBuilder.append("', '");
                                stringBuilder.append(getCurrentDate());
                                stringBuilder.append("', '");
                                stringBuilder.append(cid);
                                stringBuilder.append("', '");
                                stringBuilder.append(messageToSend.getText().toString().trim());
                                stringBuilder.append("', \"true\");");
                                connection.execSQL(stringBuilder.toString());

                                Log.e("Msg SQL", stringBuilder.toString());
                            } else {
                                Config.error_message(ChatChannelActivity.this, "Senden fehlgeschlagen");
                                stringBuilder.append("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('");
                                stringBuilder.append(channelNameString);
                                stringBuilder.append("', '");
                                stringBuilder.append(getCurrentDate());
                                stringBuilder.append("', '");
                                stringBuilder.append(cid);
                                stringBuilder.append("', '");
                                stringBuilder.append(messageToSend.getText().toString().trim());
                                stringBuilder.append("', \"false\");");
                                connection.execSQL(stringBuilder.toString());
                                Log.e("ChatActivity SQL", stringBuilder.toString());
                            }
                            messageToSend.setText("");
                            populateView(connection);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Config.error_message(ChatChannelActivity.this, "Senden fehlgeschlagen - Keine Internetverbindung");
                        }
                    });

            queue.add(chatSendRequest);
        } else Config.error_message(ChatChannelActivity.this, Config.sendMessageShortError);
    }


    public void populateView(SQLiteDatabase connection) {

        messages = queryChatMessages(connection);

        ChatChannelRecyclerAdapter adapter = new ChatChannelRecyclerAdapter(ChatChannelActivity.this, messages, channelNameString);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(ChatChannelActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        //focus on last message
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private ArrayList<ChatMessage> queryChatMessages(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from chatmessages where channel='" + channelNameString + "';", null);
        Log.e("queryChatMessages", "Select * from chatmessages where channel='" + channelNameString + "';");
        ArrayList<ChatMessage> listOfChatMessages = new ArrayList<>();
        while (res.moveToNext()) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTimestamp(res.getString(1));
            chatMessage.setCid(res.getString(2));
            chatMessage.setContent(res.getString(3));
            chatMessage.setSendState(res.getString(4).equalsIgnoreCase("true") ? true : false);
            listOfChatMessages.add(chatMessage);
        }
        //Log.e("List of Events Size", String.valueOf(listOfEvents.size()));
        return listOfChatMessages;
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(c.getTime());
    }


}
