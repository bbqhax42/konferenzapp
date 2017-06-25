package com.example.chris.konferenz_app;

/**
 * Created by Chris on 20.06.2017.
 */

public class ChatSendResponse {

    String success;

    public ChatSendResponse(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success == null ? "false" : success;
    }
}
