package com.example.chris.konferenz_app;

import java.util.ArrayList;

/**
 * Created by Chris on 06.06.2017.
 */

public class ChatPullResponse {
    String timestamp;
    ArrayList<ChatChannel> chatChannels;

    public ChatPullResponse() {
    }

    public ChatPullResponse(String timestamp, ArrayList<ChatChannel> chatChannels) {
        this.timestamp = timestamp;
        this.chatChannels = chatChannels;
    }

    public String getTimestamp() {
        return timestamp;
    }


    public int getChatChannelAmount() {
        if (chatChannels != null)
            return chatChannels.size();
        else return 0;
    }

    public ChatChannel getChatChannel(int i) {
        return chatChannels.get(i);
    }
}
