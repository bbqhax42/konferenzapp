package com.example.chris.konferenz_app;

import java.util.ArrayList;

/**
 * Created by Chris on 05.06.2017.
 */

public class SettingResponse {
    private String status, status_info, profile_name, profile_phone, profile_email, profile_company;
    private ArrayList<Interestgroup> visibility;

    public SettingResponse() {
    }

    public SettingResponse(String status, String status_info, String profile_name, String profile_phone, String profile_email, String profile_company, ArrayList<Interestgroup> visibility) {
        this.status = status;
        this.status_info = status_info;
        this.profile_name = profile_name;
        this.profile_phone = profile_phone;
        this.profile_email = profile_email;
        this.profile_company = profile_company;
        this.visibility = visibility;
    }

    public String getStatus() {
        return status;
    }

    public String getStatus_info() {
        return status_info;
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

    public int getInterestgroupAmount() {
        if (visibility != null)
            return visibility.size();
        else return 0;
    }

    public Interestgroup getInterestgroup(int i) {
        return visibility.get(i);
    }
}
