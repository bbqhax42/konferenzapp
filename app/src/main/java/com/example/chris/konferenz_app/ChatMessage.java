package com.example.chris.konferenz_app;

/**
 * Created by Chris on 06.06.2017.
 */

public class ChatMessage {
    private String timestamp, cid, content;
    private boolean sendState;

    public ChatMessage(String timestamp, String cid, String content) {
        this.timestamp = timestamp;
        this.cid = cid;
        this.content = content;
    }

    public ChatMessage() {
    }

    public boolean isSendState() {
        return sendState;
    }

    public void setSendState(boolean sendState) {
        this.sendState = sendState;
    }

    public String getTimestamp() {
        return timestamp == null ? "" : timestamp;
    }

    public String getCid() {
        return cid == null ? "" : cid;
    }

    public String getContent() {
        return content == null ? "" : content;
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
