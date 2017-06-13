package com.example.chris.konferenz_app;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chris on 27.05.2017.
 */

public class Config {

    public static final String webserviceUrl = "https://new.myconf.akademie-herkert.de/";
    public static final String sendMessageShortError = "Das Senden leerer Nachrichten ist nicht möglich.";

    //cuts off the date and only leaves the time without seconds
    public static final String formatDates(String date){
        //Log.e("formatDates", date);
        return date.substring(11, 16);
    }

    public static final void error_message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static final void popupMessage(String title, String message, Context context) {

        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(true).show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setLineSpacing(1, 1.3f);
        //builder.show();
    }


}
