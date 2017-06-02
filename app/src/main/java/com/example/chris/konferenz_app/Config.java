package com.example.chris.konferenz_app;

import android.util.Log;

/**
 * Created by Chris on 27.05.2017.
 */

public class Config {

    static final String webserviceUrl = "https://new.myconf.akademie-herkert.de/";

    //cuts off the date and only leaves the time without seconds
    static final String formatDates(String date){
        Log.e("Stringlog", date);
        return date.substring(11, 16);
    }
}
