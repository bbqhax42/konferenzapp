package com.example.chris.konferenz_app;

/**
 * Created by Chris on 20.05.2017.
 */

public class LoginResponse {

    private String success, errorMessage, cid, token;

    public String getCid() {
        return cid == null ? "" : cid;
    }

    public String getErrorMessage() {
        return errorMessage == null ? "" : errorMessage;
    }

    public String getSuccess() {
        return success == null ? "false" : success;
    }

    public String getToken() {
        return token == null ? "" : token;
    }
}