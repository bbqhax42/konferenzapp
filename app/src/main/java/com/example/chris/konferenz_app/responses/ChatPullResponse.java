package com.example.chris.konferenz_app.responses;

import com.example.chris.konferenz_app.data.ChatChannel;

import java.util.ArrayList;

/**
 * Created by Chris on 06.06.2017.
 */

public class ChatPullResponse {
    String success;
    String timestamp;
    ArrayList<ChatChannel> channels;

    public ChatPullResponse() {
    }


    public String getTimestamp() {
        return success == null ? "" : timestamp;
    }


    public int channelAmount() {
        if (channels != null)
            return channels.size();
        else return 0;
    }

    public String getSuccess() {
        return success == null ? "false" : success;
    }

    public ChatChannel getChatChannel(int i) {
        return channels == null ? null : channels.get(i);
    }
}
