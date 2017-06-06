package com.example.chris.konferenz_app;

/**
 * Created by Chris on 06.06.2017.
 */

public class ChatMessage {
    private String timestamp, cid, content;

    public ChatMessage(String timestamp, String cid, String content) {
        this.timestamp = timestamp;
        this.cid = cid;
        this.content = content;
    }

    public ChatMessage() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCid() {
        return cid;
    }

    public String getContent() {
        return content;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
