package com.example.chris.konferenz_app;

import java.util.ArrayList;

/**
 * Created by Chris on 06.06.2017.
 */

public class ChatChannel {
    private String channel, cid;
    private ArrayList<ChatMessage> messages;

    public ChatChannel(String channel, String cid, ArrayList<ChatMessage> messages) {
        this.channel = channel;
        this.cid = cid;
        this.messages = messages;
    }

    public ChatChannel() {
    }

    public String getChannel() {
        return channel;
    }

    public String getCid() {
        return cid;
    }

    public int getChatMessageAmount() {
        if (messages != null)
            return messages.size();
        else return 0;
    }

    public ChatMessage getChatMessage(int i) {
        return messages.get(i);
    }
}
