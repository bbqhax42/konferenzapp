package com.example.chris.konferenz_app.responses;

import com.example.chris.konferenz_app.data.ChatChannel;

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
        return timestamp == null ? "" : timestamp;
    }

    public String getSuccess() {
        return success == null ? "false" : success;
    }

    public int channelAmount() {
        if (channels != null)
            return channels.size();
        else return 0;
    }

    public ChatChannel getChatChannel(int i) {
        return channels == null ? null : channels.get(i);
    }
}

