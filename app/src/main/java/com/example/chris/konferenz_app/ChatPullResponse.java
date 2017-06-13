package com.example.chris.konferenz_app;

import java.util.ArrayList;

/**
 * Created by Chris on 06.06.2017.
 */

public class ChatPullResponse {
    String timestamp;
    ArrayList<ChatChannel> channels;

    public ChatPullResponse() {
    }

    public ChatPullResponse(String timestamp, ArrayList<ChatChannel> channels) {
        this.timestamp = timestamp;
        this.channels = channels;
    }

    public String getTimestamp() {
        return timestamp;
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
