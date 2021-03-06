package com.example.chris.konferenz_app;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chris on 27.05.2017.
 */

public class Config {

    public static final int appversion = 12;

    public static final int chatPullWaitTime = 5;
    public static final int chatListWaitTime = 30;
    public static final int expectedServerLagInMillis = 650;

    public static final String adress = "FORUM MEDIA GROUP GMBH\n" +
            "Mandichostraße 18\n" +
            "86504 Merching\n";

    public static final String contactdata = "Kontakt:\n" +
            "\n" +
            "Telefon: +49 8233 / 381-0\n" +
            "Telefax: +49 8233 / 381-222\n" +
            "E-Mail: kontakt@forum-media.com";


    public static final String webserviceUrl = "https://myconf.akademie-herkert.de/";
    public static final String sendMessageShortError = "Das Senden leerer Nachrichten ist nicht möglich.";


    public static final void error_message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static final void popupMessage(String title, String message, Context context) {

        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(true).show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setLineSpacing(1, 1.3f);
        //builder.show();
    }

    //cuts off the date and only leaves the time without seconds "2017-05-23 11:23:03.000" -> "11:23"
    public static final String formatDates(String date) {
        //Log.e("formatDates", date.length() + "");
        if (date == null) {
            return "Error";
        } else if (!(date.length() == 23 || date.length() == 19)) {
            return date;
        } else
            return date.substring(11, 16);
    }

}
