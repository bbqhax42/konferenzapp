package com.example.chris.konferenz_app;

/**
 * Created by Chris on 06.06.2017.
 */

public class User {
    private String cid, profile_name, profile_phone, profile_email, profile_company;

    public User(String cid, String profile_name, String profile_phone, String profile_email, String profile_company) {
        this.cid = cid;
        this.profile_name = profile_name;
        this.profile_phone = profile_phone;
        this.profile_email = profile_email;
        this.profile_company = profile_company;
    }

    public User() {
    }

    public String getCid() {
        return cid;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public String getProfile_phone() {
        return profile_phone;
    }

    public String getProfile_email() {
        return profile_email;
    }

    public String getProfile_company() {
        return profile_company;
    }
}
