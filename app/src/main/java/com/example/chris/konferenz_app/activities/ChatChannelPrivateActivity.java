package com.example.chris.konferenz_app.activities;

/**
 * Created by Chris on 06.06.2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chris.konferenz_app.adapters.ChatChannelPrivateRecyclerAdapter;
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

public class ChatChannelPrivateActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper myDb = new DatabaseHelper(this);
    ArrayList<ChatMessage> messages;
    String channelNameString = "Unbekannt";
    String partnerCid;


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
        final SQLiteDatabase connection = myDb.getWritableDatabase();
        partnerCid = getIntent().getStringExtra("ChannelName");
        this.registerReceiver(receiver, new IntentFilter("MsgSent"));

        Cursor res = connection.rawQuery("Select * from users where cid='" + partnerCid + "';", null);

        while (res.moveToNext()) {
            channelNameString = "<u>" + res.getString(1) + "</u>";
        }
        try {
            connection.execSQL("Insert into privatechatlist (cid, blocked) VALUES ('" + partnerCid + "', \"false\");");
        } catch (SQLiteConstraintException e) {
        }

        final TextView channelName;
        final EditText messageToSend;
        chatButton = (Button) findViewById(R.id.chatbutton);
        settingsButton = (Button) findViewById(R.id.settingsbutton);
        homeButton = (Button) findViewById(R.id.homebutton);
        messageToSend = (EditText) findViewById(R.id.messagetosend);
        sendButton = (Button) findViewById(R.id.sendbutton);
        channelName = (TextView) findViewById(R.id.title);
        channelName.setText(Html.fromHtml(channelNameString));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //changes the button background to show which part of the program the user is accessing
        chatButton.setBackgroundResource(R.drawable.toolbar_button_selected);

        final String token = myDb.getToken(connection);
        final String cid = myDb.getCid(connection);


        populateView(connection);


        channelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatChannelPrivateActivity.this, UserActivity.class);
                intent.putExtra("Cid", partnerCid);
                ChatChannelPrivateActivity.this.startActivity(intent);
            }
        });


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
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void onClickSendMessageButton(String token, final EditText messageToSend, final SQLiteDatabase connection, final String cid) {
        RequestQueue queue = Volley.newRequestQueue(ChatChannelPrivateActivity.this);


        String url = Config.webserviceUrl + "CHAT.SEND?token=" + token + "&cid=" + getIntent().getStringExtra("ChannelName") + "&content=" + messageToSend.getText();
        Log.e("Priv Chat Message URL", url);

        if (messageToSend.getText().toString().trim().length() != 0) {

            JsonObjectRequest documentRequestRequest =
                    new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Gson gson = new Gson();
                            ChatSendResponse chatSendResponse = gson.fromJson(jsonObject.toString(), ChatSendResponse.class);
                            StringBuilder stringBuilder = new StringBuilder();
                            if (chatSendResponse.getSuccess().equalsIgnoreCase("true")) {
                                stringBuilder.append("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('");
                                stringBuilder.append(partnerCid);
                                stringBuilder.append("', '");
                                stringBuilder.append(getCurrentDate());
                                stringBuilder.append("', '");
                                stringBuilder.append(cid);
                                stringBuilder.append("', '");
                                stringBuilder.append(messageToSend.getText().toString().trim());
                                stringBuilder.append("', \"true\");");
                                connection.execSQL(stringBuilder.toString());
                                Config.error_message(ChatChannelPrivateActivity.this, "Nachricht erfolgreich gesendet");
                            } else {
                                stringBuilder.append("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('");
                                stringBuilder.append(partnerCid);
                                stringBuilder.append("', '");
                                stringBuilder.append(getCurrentDate());
                                stringBuilder.append("', '");
                                stringBuilder.append(cid);
                                stringBuilder.append("', '");
                                stringBuilder.append(messageToSend.getText().toString().trim());
                                stringBuilder.append("', \"false\");");
                                connection.execSQL(stringBuilder.toString());
                                Config.error_message(ChatChannelPrivateActivity.this, "Senden fehlgeschlagen");
                            }

                            messageToSend.setText("");
                            populateView(connection);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Config.error_message(ChatChannelPrivateActivity.this, "Senden fehlgeschlagen - Keine Internetverbindung");
                        }
                    });

            queue.add(documentRequestRequest);
        } else
            Config.error_message(ChatChannelPrivateActivity.this, Config.sendMessageShortError);
    }


    public void populateView(SQLiteDatabase connection) {

        messages = queryChatMessages(connection);
        ChatChannelPrivateRecyclerAdapter adapter = new ChatChannelPrivateRecyclerAdapter(ChatChannelPrivateActivity.this, messages, partnerCid);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(ChatChannelPrivateActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        //focus on last message
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private ArrayList<ChatMessage> queryChatMessages(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from chatmessages where channel='" + partnerCid + "';", null);
        Log.e("queryChatMessages", "Select * from chatmessages where channel='" + partnerCid + "';");
        ArrayList<ChatMessage> listOfChatMessages = new ArrayList<>();
        while (res.moveToNext()) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTimestamp(res.getString(1));
            chatMessage.setCid(res.getString(2));
            chatMessage.setContent(res.getString(3));
            chatMessage.setSendState(res.getString(4).equalsIgnoreCase("true") ? true : false);
            listOfChatMessages.add(chatMessage);
        }
        return listOfChatMessages;
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(c.getTime());
    }


}
