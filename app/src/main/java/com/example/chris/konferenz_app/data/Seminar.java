package com.example.chris.konferenz_app.data;

import java.util.ArrayList;

public class Seminar {
    private String seminar_id;
    private String seminar_title;
    private String date;
    private ArrayList<Event> events;
    private ArrayList<Interestgroup> interestgroups;


    public String getSeminar_id() {
        return seminar_id == null ? "" : seminar_id;
    }


    public String getSeminar_title() {
        return seminar_title == null ? "" : seminar_title;
    }

    public Event getEvent(int i) {
        return events == null ? null : events.get(i);
    }

    public int getEventAmount() {
        return events.size();
    }

    public boolean isempty() {
        return events == null || interestgroups == null;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public int getInterestgroupAmount() {
        if (interestgroups != null)
            return interestgroups.size();
        else return 0;
    }

    public Interestgroup getInterestgroup(int i) {
        return interestgroups.get(i);
    }
}