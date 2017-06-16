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

public class ChatListThread {

    public ChatListThread() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                final DatabaseHelper myDb = new DatabaseHelper(null);
                final SQLiteDatabase connection = myDb.getWritableDatabase();
                String token = myDb.getToken(connection);


                RequestQueue queue = Volley.newRequestQueue(null);
                String url = Config.webserviceUrl + "CHAT.LIST?token=" + token;
                //Log.e("Chat.Pull URL", url);

                //pull chat data
                final JsonObjectRequest chatPullRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                String s = jsonObject.toString();
                                Gson gson = new Gson();

                                ChatListResponse chatListResponse = gson.fromJson(jsonObject.toString(), ChatListResponse.class);

                                saveChatListResponseToDatabase(chatListResponse, connection);


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


    private void saveChatListResponseToDatabase(ChatListResponse chatListResponse, SQLiteDatabase connection) {
        for (int i = 0; i < chatListResponse.channelAmount(); i++) {
            for (int j = 0; j < chatListResponse.getChatChannel(i).getUserAmount(); j++) {
                connection.execSQL("Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) VALUES ('"
                        + chatListResponse.getChatChannel(i).getUser(j).getCid() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_name() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_phone() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_email() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_company() + ");");
                Log.e("Chat.List Save DB", "Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) VALUES ('"
                        + chatListResponse.getChatChannel(i).getUser(j).getCid() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_name() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_phone() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_email() + "' , '"
                        + chatListResponse.getChatChannel(i).getUser(j).getProfile_company() + ");");
            }


        }


    }
}
