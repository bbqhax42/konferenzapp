package com.example.chris.konferenz_app;

import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by Chris on 16.06.2017.
 */

public class ChatPullThread {

    public ChatPullThread() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                final DatabaseHelper myDb = new DatabaseHelper(null);
                final SQLiteDatabase connection = myDb.getWritableDatabase();
                String token = myDb.getToken(connection);


                RequestQueue queue = Volley.newRequestQueue(null);
                String url = Config.webserviceUrl + "CHAT.PULL?token=" + token;
                //Log.e("Chat.Pull URL", url);

                //pull chat data
                final JsonObjectRequest chatPullRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                String s = jsonObject.toString();
                                Gson gson = new Gson();

                                ChatPullResponse chatPullResponse = gson.fromJson(jsonObject.toString(), ChatPullResponse.class);
                                //Log.e("Seminar DB EventAmnt", seminar.getEventAmount() + "");

                                saveChatPullResponseToDatabase(chatPullResponse, connection);


                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                queue.add(chatPullRequest);
            }

        });
        thread.start();
    }


    private void saveChatPullResponseToDatabase(ChatPullResponse chatPullResponse, SQLiteDatabase connection) {
        for (int i = 0; i < chatPullResponse.channelAmount(); i++) {
            for (int j = 0; j < chatPullResponse.getChatChannel(i).getChatMessageAmount(); j++) {
                connection.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('"
                        + chatPullResponse.getChatChannel(i).getChannel() == null ? chatPullResponse.getChatChannel(i).getCid() : chatPullResponse.getChatChannel(i).getChannel() + "' , '"
                        + Config.formatDates(chatPullResponse.getChatChannel(i).getChatMessage(j).getTimestamp()) + "' , '"
                        + chatPullResponse.getChatChannel(i).getChatMessage(j).getCid() + "' , '"
                        + chatPullResponse.getChatChannel(i).getChatMessage(j).getContent() + "' , \"" +
                        "TRUE\");");
                Log.e("Chat.Pull Save DB", "INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('"
                        + chatPullResponse.getChatChannel(i).getChannel() == null ? chatPullResponse.getChatChannel(i).getCid() : chatPullResponse.getChatChannel(i).getChannel() + "' , '"
                        + Config.formatDates(chatPullResponse.getChatChannel(i).getChatMessage(j).getTimestamp()) + "' , '"
                        + chatPullResponse.getChatChannel(i).getChatMessage(j).getCid() + "' , '"
                        + chatPullResponse.getChatChannel(i).getChatMessage(j).getContent() + "' , \"" +
                        "TRUE\");");
            }


        }


    }
}
