package com.example.chris.konferenz_app;

import java.util.ArrayList;

/**
 * Created by Chris on 16.06.2017.
 */

public class ChatListResponse {
    String success;
    String timestamp;
    ArrayList<ChatChannel> channels;

    public ChatListResponse() {
    }

    public ChatListResponse(String timestamp, ArrayList<ChatChannel> channels) {
        this.timestamp = timestamp;
        this.channels = channels;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSuccess() {
        return success;
    }

    public int channelAmount() {
        if (channels != null)
            return channels.size();
        else return 0;
    }

    public ChatChannel getChatChannel(int i) {
        return channels.get(i);
    }
}

