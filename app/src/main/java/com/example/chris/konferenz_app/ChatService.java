package com.example.chris.konferenz_app;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Chris on 20.06.2017.
 */

public class ChatService extends Service {
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
        startChatPull();
        startChatList();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }


    private void startChatList() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                final DatabaseHelper myDb = new DatabaseHelper(ChatService.this);
                final SQLiteDatabase connection = myDb.getWritableDatabase();
                String token = myDb.getToken(connection);


                RequestQueue queue = Volley.newRequestQueue(ChatService.this);
                String url = Config.webserviceUrl + "CHAT.LIST?token=" + token;
                //Log.e("Chat.Pull URL", url);


                while (true) {
                    //pull chat data
                    final JsonObjectRequest chatPullRequest =
                            new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    Gson gson = new Gson();

                                    ChatListResponse chatListResponse = gson.fromJson(jsonObject.toString(), ChatListResponse.class);

                                    if (chatListResponse.getSuccess().equalsIgnoreCase("true")) {
                                        saveChatListResponseToDatabase(chatListResponse, connection);
                                    } else {
                                        Config.error_message(null, "Fehler, Programm startet neu. Bitte melden sie sich beim Kundenservice.");
                                    }


                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    queue.add(chatPullRequest);
                    try {
                        TimeUnit.SECONDS.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }


        });
        thread.start();


    }


    private void saveChatListResponseToDatabase(ChatListResponse chatListResponse, SQLiteDatabase connection) {
        for (int i = 0; i < chatListResponse.channelAmount(); i++) {
            for (int j = 0; j < chatListResponse.getChatChannel(i).getUserAmount(); j++) {
               Log.e("Chat.List Save DB", "Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) VALUES ('"
                        + chatListResponse.getChatChannel(i).getUser(j).getCid() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_name() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_phone() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_email() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_company() + "');");

                try {
                    connection.execSQL("Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) VALUES ('"
                            + chatListResponse.getChatChannel(i).getUser(j).getCid() + "' , '"
                            + chatListResponse.getChatChannel(i).getUser(j).getProfile_name() + "' , '"
                            + chatListResponse.getChatChannel(i).getUser(j).getProfile_phone() + "' , '"
                            + chatListResponse.getChatChannel(i).getUser(j).getProfile_email() + "' , '"
                            + chatListResponse.getChatChannel(i).getUser(j).getProfile_company() + "');");
                } catch (SQLiteConstraintException e) {

                }

            }


        }


    }


    private void startChatPull() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                final DatabaseHelper myDb = new DatabaseHelper(ChatService.this);
                final SQLiteDatabase connection = myDb.getWritableDatabase();
                String token = myDb.getToken(connection);


                RequestQueue queue = Volley.newRequestQueue(ChatService.this);
                String url = Config.webserviceUrl + "CHAT.PULL?token=" + token;
                Log.e("Chat.Pull URL", url);

                while (true) {
                    //pull chat data
                    final JsonObjectRequest chatPullRequest =
                            new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    Gson gson = new Gson();

                                    ChatPullResponse chatPullResponse = gson.fromJson(jsonObject.toString(), ChatPullResponse.class);
                                    if (chatPullResponse.getSuccess().equalsIgnoreCase("true")) {
                                        saveChatPullResponseToDatabase(chatPullResponse, connection);
                                    } else {
                                        Config.error_message(null, "Fehler, Programm startet neu. Bitte melden sie sich beim Kundenservice.");

                                    }


                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    queue.add(chatPullRequest);

                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        thread.start();
    }


    private void saveChatPullResponseToDatabase(ChatPullResponse chatPullResponse, SQLiteDatabase connection) {
        for (int i = 0; i < chatPullResponse.channelAmount(); i++) {
            Log.e("Message Amount: ", chatPullResponse.getChatChannel(i).getChatMessageAmount() + "");
            for (int j = 0; j < chatPullResponse.getChatChannel(i).getChatMessageAmount(); j++) {
                connection.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('"
                        + chatPullResponse.getChatChannel(i).getChannel() == null || chatPullResponse.getChatChannel(i).getChannel().length() == 0 ? chatPullResponse.getChatChannel(i).getCid() : chatPullResponse.getChatChannel(i).getChannel() + "' , '"
                        + Config.formatDates(chatPullResponse.getChatChannel(i).getChatMessage(j).getTimestamp()) + "' , '"
                        + chatPullResponse.getChatChannel(i).getChatMessage(j).getCid() + "' , '"
                        + chatPullResponse.getChatChannel(i).getChatMessage(j).getContent() + "' , \"" +
                        "TRUE\");");
                Log.e("Chat.Pull Save DB", "INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('"
                        + chatPullResponse.getChatChannel(i).getChannel() == null || chatPullResponse.getChatChannel(i).getChannel().length() == 0 ? chatPullResponse.getChatChannel(i).getCid() : chatPullResponse.getChatChannel(i).getChannel() + "' , '"
                        + Config.formatDates(chatPullResponse.getChatChannel(i).getChatMessage(j).getTimestamp()) + "' , '"
                        + chatPullResponse.getChatChannel(i).getChatMessage(j).getCid() + "' , '"
                        + chatPullResponse.getChatChannel(i).getChatMessage(j).getContent() + "' , \"" +
                        "TRUE\");");

                if (chatPullResponse.getChatChannel(i).getCid() != null && chatPullResponse.getChatChannel(i).getCid().length() != 0) {
                    try {
                        connection.execSQL("INSERT INTO privatechatlist (cid, blocked) VALUES ('" + chatPullResponse.getChatChannel(i).getCid() + "', \"FALSE\");");
                    } catch (SQLiteConstraintException e) {
                    }
                }
            }


        }


    }

}
