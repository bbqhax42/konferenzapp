package com.example.chris.konferenz_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper myDb = new DatabaseHelper(this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();

        //did we cache data today already?
        String s = getIntent().getStringExtra("EventUpdate");
        Log.e("Intent LoginActivity", s);


        s = "tRuE";
        //we did not login today yet? GREAT!!!!111
        if (s.equalsIgnoreCase("true")) {
            Log.e("first login?", s.equalsIgnoreCase("true") + "");
            myDb.deleteAllUselessTablesLUL();

            Cursor res = connection.rawQuery("Select * from userinformation;", null);
            res.moveToFirst();

            String token = res.getString(8);
            String date = "2017-06-14"; //wow time flew by fast


            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

            //load event data
            String url = Config.webserviceUrl + "EVENT.DAILY?token=" + token + "&date=" + date;
            Log.e("url", url);
            final JsonObjectRequest seminarRequest =
                    new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            String s = jsonObject.toString();
                            Gson gson = new Gson();

                            Seminar seminar = gson.fromJson(jsonObject.toString(), Seminar.class);
                            Log.e("Seminar EventAmnt", seminar.getEventAmount() + "");


                            for (int i = 0; i < seminar.getEventAmount(); i++) {
                                Event event = seminar.getEvent(i);
                                connection.execSQL("Insert into events (event_id, id, title, description, author, start, end, street, zip, city, location, url) VALUES ('"
                                        + event.getEventId() + "' , '"
                                        + event.getId() + "' , '"
                                        + event.getTitle() + "' , '"
                                        + event.getDescription() + "' , '"
                                        + event.getAuthor() + "' , '"
                                        + event.getStart() + "' , '"
                                        + event.getEnd() + "' , '"
                                        + event.getStreet() + "' , '"
                                        + event.getZip() + "' , '"
                                        + event.getCity() + "' , '"
                                        + event.getLocation() + "' , '"
                                        + event.getUrl() + "');");

                                for (int j = 0; j < event.getDocumentAmount(); j++) {
                                    Document doc = event.getDocument(j);
                                    connection.execSQL("Insert into documents (id, title, event_id) VALUES ('"
                                            + doc.getId() + "' , '"
                                            + doc.getTitle() + "' , '"
                                            + event.getEventId() + "');");
                                }


                                Log.e("Event DocumentAmnt", seminar.getEvent(i).getDocumentAmount() + "");
                            }

                            Log.e("Seminar InterestAmnt", seminar.getInterestgroupAmount() + "");
                            for (int i = 0; i < seminar.getInterestgroupAmount(); i++) {
                            connection.execSQL("Insert into interests (name) VALUES ('" + seminar.getInterestgroup(i).getName() +"');");
                                Log.e("Seminar Interest", seminar.getInterestgroup(i).getName());
                            }


                            //i can get all events here and now I need to display them

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", error.getMessage());
                        }
                    });
            queue.add(seminarRequest);
        }



    }
}
