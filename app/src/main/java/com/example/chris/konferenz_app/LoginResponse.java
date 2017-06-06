package com.example.chris.konferenz_app;

/**
 * Created by Chris on 20.05.2017.
 */

public class LoginResponse {

    private String success, errorMessage, cid, token;

    public String getCid() {
        return cid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSuccess() {return success;}

    public String getToken() {
        return token;
    }
}