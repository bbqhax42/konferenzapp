package com.example.chris.konferenz_app;

/**
 * Created by Chris on 06.06.2017.
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ChatChannelActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper myDb = new DatabaseHelper(this);
    ArrayList<ChatMessage> messages;
    String channelNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button chatButton, settingsButton, homeButton, sendButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatchannel);
        final SQLiteDatabase connection = myDb.getWritableDatabase();
        channelNameString = getIntent().getStringExtra("ChannelName");

        final TextView channelName;
        final EditText messageToSend;
        chatButton = (Button) findViewById(R.id.chatbutton);
        settingsButton = (Button) findViewById(R.id.settingsbutton);
        homeButton = (Button) findViewById(R.id.homebutton);
        messageToSend = (EditText) findViewById(R.id.messagetosend);
        sendButton = (Button) findViewById(R.id.sendbutton);
        channelName = (TextView) findViewById(R.id.title);
        channelName.setText("Thema: "+getIntent().getStringExtra("ChannelName"));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final String token = myDb.getToken(connection);
        final String cid = myDb.getCid(connection);


        populateView(connection);


        messageToSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
                    ViewGroup.LayoutParams params=recyclerView.getLayoutParams();
                    params.height=900;
                    recyclerView.setLayoutParams(params);
                    recyclerView.scrollToPosition(messages.size()-1);
                }else {
                    //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                    ViewGroup.LayoutParams params=recyclerView.getLayoutParams();
                    params.height=1680;
                    recyclerView.setLayoutParams(params);
                    recyclerView.scrollToPosition(messages.size()-1);
                }
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
                RequestQueue queue = Volley.newRequestQueue(ChatChannelActivity.this);


                String url = Config.webserviceUrl + "CHAT.SEND?token" + token + "&channel=" + channelName.getText().toString() + "&content=" + messageToSend.getText().toString();

                Log.e("Chat Message URL", url);

                JsonObjectRequest documentRequestRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                messageToSend.setText("");
                                Config.error_message(ChatChannelActivity.this, "Nachricht erfolgreich gesendet");
                                connection.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content) VALUES ('"+ channelName.getText().toString()+ "', '"+ getCurrentDate() +"', '" + cid + "', '"+messageToSend.getText().toString().trim() +"');");
                                messageToSend.setText("");
                                populateView(connection);
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Config.error_message(ChatChannelActivity.this, "Senden fehlgeschlagen");
                                connection.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content) VALUES ('"+ channelNameString+ "', '"+ getCurrentDate() +"', '" + cid + "', '"+messageToSend.getText().toString().trim() +"');");
                                //Log.e("ChatActivity SQL", "INSERT INTO chatmessages (channel, timestamp, cid, content) VALUES ('"+ channelNameString+ "', '"+ getCurrentDate() +"', '" + cid + "', '"+messageToSend.getText().toString().trim() +"');");
                                messageToSend.setText("");
                                populateView(connection);
                                //wieder loeschen!!! ! ! ! ! ! ! !
                            }
                        });

                queue.add(documentRequestRequest);

            }
        });


    }


    private void populateView(SQLiteDatabase connection) {
        Log.e("populateview", "success");

        messages= queryChatMessages(connection);

        ChatChannelRecyclerAdapter adapter = new ChatChannelRecyclerAdapter(ChatChannelActivity.this, messages);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(ChatChannelActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);
        recyclerView.scrollToPosition(messages.size()-1);
    }

    private ArrayList<ChatMessage> queryChatMessages(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from chatmessages where channel='"+ channelNameString +"';", null);
        Log.e("queryChatMessages", "Select * from chatmessages where channel='"+ channelNameString +"';");
        ArrayList<ChatMessage> listOfChatMessages = new ArrayList<>();
        while (res.moveToNext()) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTimestamp(res.getString(1));
            chatMessage.setCid(res.getString(2));
            chatMessage.setContent(res.getString(3));
            listOfChatMessages.add(chatMessage);
            Log.e("queryChatMessages", res.getString(1));
            Log.e("queryChatMessages", res.getString(2));
            Log.e("queryChatMessages", res.getString(3));
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
