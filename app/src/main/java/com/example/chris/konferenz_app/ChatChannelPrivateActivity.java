package com.example.chris.konferenz_app;

/**
 * Created by Chris on 06.06.2017.
 */

import android.content.Intent;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ChatChannelPrivateActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper myDb = new DatabaseHelper(this);
    ArrayList<ChatMessage> messages;
    String channelNameString = "Unbekannt";
    String partnerCid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button chatButton, settingsButton, homeButton, sendButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatchannel);
        final SQLiteDatabase connection = myDb.getWritableDatabase();
        partnerCid = getIntent().getStringExtra("ChannelName");
        Cursor res = connection.rawQuery("Select * from users where cid='" + partnerCid + "';", null);

        while (res.moveToNext()) {
            channelNameString ="<u>" + res.getString(1) + "</u>";
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

        final String token = myDb.getToken(connection);
        final String cid = myDb.getCid(connection);


        populateView(connection);


        messageToSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                    params.height = 900;
                    recyclerView.setLayoutParams(params);
                    recyclerView.scrollToPosition(messages.size() - 1);
                } else {
                    //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                    params.height = 1680;
                    recyclerView.setLayoutParams(params);
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            }
        });

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


    }

    private void onClickSendMessageButton(String token, final EditText messageToSend, final SQLiteDatabase connection, final String cid) {
        RequestQueue queue = Volley.newRequestQueue(ChatChannelPrivateActivity.this);


        String url = Config.webserviceUrl + "CHAT.SEND?token" + token + "&cid=" + getIntent().getStringExtra("ChannelName") + "&content=" + partnerCid;
        Log.e("Priv Chat Message URL", url);

        if (messageToSend.getText().toString().trim().length() != 0) {

            JsonObjectRequest documentRequestRequest =
                    new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            messageToSend.setText("");
                            Config.error_message(ChatChannelPrivateActivity.this, "Nachricht erfolgreich gesendet");
                            connection.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('" + partnerCid + "', '" + getCurrentDate() + "', '" + cid + "', '" + messageToSend.getText().toString().trim() + "', \"true\");");
                            messageToSend.setText("");
                            populateView(connection);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            //!!!!!!!!!!!!!!!!!!!!!!
                            Config.error_message(ChatChannelPrivateActivity.this, "Senden fehlgeschlagen");
                            connection.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('" + partnerCid + "', '" + getCurrentDate() + "', '" + cid + "', '" + messageToSend.getText().toString().trim() + "', \"false\");");
                            Log.e("ChatActivity SQL", "INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('" + partnerCid + "', '" + getCurrentDate() + "', '" + cid + "', '" + messageToSend.getText().toString().trim() + "', \"false\");");
                            messageToSend.setText("");
                            populateView(connection);
                            //wieder loeschen!!! ! ! ! ! ! ! !
                        }
                    });

            queue.add(documentRequestRequest);
        } else
            Config.error_message(ChatChannelPrivateActivity.this, Config.sendMessageShortError);
    }


    public void populateView(SQLiteDatabase connection) {
        Log.e("populateview", "success");

        messages = queryChatMessages(connection);

        ChatChannelPrivateRecyclerAdapter adapter = new ChatChannelPrivateRecyclerAdapter(ChatChannelPrivateActivity.this, messages, partnerCid);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(ChatChannelPrivateActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);
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
        //Log.e("List of Events Size", String.valueOf(listOfEvents.size()));
        return listOfChatMessages;
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(c.getTime());
    }


}
