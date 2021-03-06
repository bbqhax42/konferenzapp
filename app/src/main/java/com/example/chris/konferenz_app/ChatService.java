package com.example.chris.konferenz_app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chris.konferenz_app.activities.LoginActivity;
import com.example.chris.konferenz_app.responses.ChatListResponse;
import com.example.chris.konferenz_app.responses.ChatPullResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Chris on 20.06.2017.
 */

public class ChatService extends Service {
    Thread chatList, chatPull;
    volatile boolean chatListRun = false, chatPullRun = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        startChatPull();
        startChatList();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        chatListRun = false;
        chatPullRun = false;
        super.onDestroy();
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }


    private void startChatList() {
        chatListRun = true;
        chatList = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                final DatabaseHelper myDb = new DatabaseHelper(ChatService.this);
                final SQLiteDatabase connection = myDb.getWritableDatabase();
                String token = myDb.getToken(connection);


                RequestQueue queue = Volley.newRequestQueue(ChatService.this);
                String url = Config.webserviceUrl + "CHAT.LIST?token=" + token;
                Log.e("Chat.List URL", url);

                while (chatListRun) {
                    final JsonObjectRequest chatListRequest =
                            new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    Gson gson = new Gson();
                                    ChatListResponse chatListResponse = null;
                                    try {
                                        chatListResponse = gson.fromJson(jsonObject.toString(), ChatListResponse.class);
                                        Log.e("Chat.List", chatListResponse.getSuccess());
                                        if (chatListResponse.getSuccess().equalsIgnoreCase("true")) {

                                            saveChatListResponseToDatabase(chatListResponse, connection);
                                        } else {
                                            Config.error_message(ChatService.this.getApplicationContext(), "Fehler, starten Sie bitte das Programm neu und melden sich beim Kundenservice.");
                                        }
                                    } catch (IllegalStateException | JsonSyntaxException e) {
                                        Toast.makeText(ChatService.this.getApplicationContext(), "Serverseitiger Fehler beim Empfangen der Chatliste. Bitte melden Sie sich beim Kundenservice", Toast.LENGTH_LONG).show();

                                    }


                                }
                            }, new Response.ErrorListener() {


                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Chat.List", "Serversided error");
                                }
                            });
                    queue.add(chatListRequest);
                    try {
                        TimeUnit.SECONDS.sleep(Config.chatListWaitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }


        });
        chatList.start();


    }


    private void saveChatListResponseToDatabase(ChatListResponse chatListResponse, SQLiteDatabase connection) {
        for (int i = 0; i < chatListResponse.channelAmount(); i++) {
            for (int j = 0; j < chatListResponse.getChatChannel(i).getUserAmount(); j++) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) VALUES ('");
                stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getCid());
                stringBuilder.append("' , '");
                stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getProfile_name());
                stringBuilder.append("' , '");
                stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getProfile_phone());
                stringBuilder.append("' , '");
                stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getProfile_email());
                stringBuilder.append("' , '");
                stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getProfile_company());
                stringBuilder.append("');");
                Log.e("Chat.List Save DB", stringBuilder.toString());
                try {
                    connection.execSQL(stringBuilder.toString());
                } catch (SQLiteConstraintException e) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Update users set profile_name='");
                    stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getProfile_name());
                    stringBuilder.append("' , profile_phone='");
                    stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getProfile_phone());
                    stringBuilder.append("' , profile_email='");
                    stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getProfile_email());
                    stringBuilder.append("' , profile_company='");
                    stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getProfile_company());
                    stringBuilder.append("' where cid='");
                    stringBuilder.append(chatListResponse.getChatChannel(i).getUser(j).getCid());
                    stringBuilder.append("';");

                    Log.e("Chat.Pull", "User " + chatListResponse.getChatChannel(i).getUser(j).getCid() + " schon vorhanden.");
                    Log.e("Chat.Pull", stringBuilder.toString());
                    connection.execSQL(stringBuilder.toString());
                }

            }


        }


    }


    private void startChatPull() {
        chatPullRun = true;
        chatPull = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                final DatabaseHelper myDb = new DatabaseHelper(ChatService.this);
                final SQLiteDatabase connection = myDb.getWritableDatabase();
                String token = myDb.getToken(connection);


                RequestQueue queue = Volley.newRequestQueue(ChatService.this);
                final String url = Config.webserviceUrl + "CHAT.PULL?token=" + token;


                while (chatPullRun) {
                    //pull help_channeloverview data
                    final JsonObjectRequest chatPullRequest =
                            new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    Gson gson = new Gson();
                                    ChatPullResponse chatPullResponse = null;
                                    try {
                                        chatPullResponse = gson.fromJson(jsonObject.toString(), ChatPullResponse.class);

                                        if (chatPullResponse.getSuccess().equalsIgnoreCase("true")) {
                                            Log.e("Chat.Pull URL", url);
                                            saveChatPullResponseToDatabase(chatPullResponse, connection);
                                        } else {
                                            Config.error_message(ChatService.this.getApplicationContext(), "Fehler, Programm startet neu. Bitte melden sie sich beim Kundenservice.");

                                        }
                                    } catch (IllegalStateException | JsonSyntaxException e) {
                                        Toast.makeText(ChatService.this.getApplicationContext(), "Serverseitiger Fehler beim Chatnachrichten empfangen. Bitte melden Sie sich beim Kundenservice", Toast.LENGTH_LONG).show();

                                    }


                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    queue.add(chatPullRequest);

                    try {
                        TimeUnit.SECONDS.sleep(Config.chatPullWaitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        chatPull.start();
    }


    private String getChannelName(ChatPullResponse chatPullResponse, int index) {
        if (chatPullResponse.getChatChannel(index).getChannel() == null || chatPullResponse.getChatChannel(index).getChannel().length() == 0) {
            return chatPullResponse.getChatChannel(index).getCid();
        } else {
            return chatPullResponse.getChatChannel(index).getChannel();
        }
    }

    private void saveChatPullResponseToDatabase(ChatPullResponse chatPullResponse, SQLiteDatabase connection) {
        String delimiter = "' , '";
        int msgCount = 0;
        for (int i = 0; i < chatPullResponse.channelAmount(); i++) {
            Log.e("timestamp", chatPullResponse.getTimestamp() + " Channelname: " + chatPullResponse.getChatChannel(i).getChannel() + " Message Amount: " + chatPullResponse.getChatChannel(i).getChatMessageAmount());
            for (int j = 0; j < chatPullResponse.getChatChannel(i).getChatMessageAmount(); j++) {

                if (chatPullResponse.getChatChannel(i).getChannel() != null && !chatPullResponse.getChatChannel(i).getChannel().equals("empty")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('");
                    stringBuilder.append(getChannelName(chatPullResponse, i));
                    stringBuilder.append(delimiter);
                    stringBuilder.append(Config.formatDates(chatPullResponse.getChatChannel(i).getChatMessage(j).getTimestamp()));
                    stringBuilder.append(delimiter);
                    stringBuilder.append(chatPullResponse.getChatChannel(i).getChatMessage(j).getCid());
                    stringBuilder.append(delimiter);
                    stringBuilder.append(chatPullResponse.getChatChannel(i).getChatMessage(j).getContent());
                    stringBuilder.append("' , \"");
                    stringBuilder.append("true\");");

                    Log.e("Chat.Pull Save DB PUB", stringBuilder.toString());
                    connection.execSQL(stringBuilder.toString());
                }
                //if the message received is a private message we insert the userinformation here
                else if (chatPullResponse.getChatChannel(i).getCid() != null && chatPullResponse.getChatChannel(i).getCid().length() != 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('");
                    stringBuilder.append(chatPullResponse.getChatChannel(i).getChatMessage(j).getCid());
                    stringBuilder.append(delimiter);
                    stringBuilder.append(Config.formatDates(chatPullResponse.getChatChannel(i).getChatMessage(j).getTimestamp()));
                    stringBuilder.append(delimiter);
                    stringBuilder.append(chatPullResponse.getChatChannel(i).getChatMessage(j).getCid());
                    stringBuilder.append(delimiter);
                    stringBuilder.append(chatPullResponse.getChatChannel(i).getChatMessage(j).getContent());
                    stringBuilder.append("' , \"");
                    stringBuilder.append("false\");");

                    Log.e("Chat.Pull Save DB PRIV", stringBuilder.toString());
                    connection.execSQL(stringBuilder.toString());


                    try {
                        Log.e("Chat.Pull Save DB PRIV", "INSERT INTO privatechatlist (cid, blocked) VALUES ('" + chatPullResponse.getChatChannel(i).getChatMessage(j).getCid() + "', \"false\");");
                        connection.execSQL("INSERT INTO privatechatlist (cid, blocked) VALUES ('" + chatPullResponse.getChatChannel(i).getChatMessage(j).getCid() + "', \"false\");");
                    } catch (SQLiteConstraintException e) {
                    }
                }
                msgCount++;
            }

        }
        if (msgCount >= 1) {
            sendBroadcast(new Intent("MsgSent"));
            Log.e("Intent sent! Messages: ", msgCount + "");
        }
    }

}
