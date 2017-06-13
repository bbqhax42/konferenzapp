package com.example.chris.konferenz_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


/**
 * Created by Chris on 14.05.2017.
 */

public class LoginActivity extends AppCompatActivity {


    Button login_button;
    CheckBox rememberme_checkbox;
    EditText email_textfield, password_textfield;
    boolean eingeloggt_bleiben;
    String freischaltcode;
    boolean firstlogin;
    final DatabaseHelper myDb = new DatabaseHelper(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = (Button) (findViewById(R.id.button));
        rememberme_checkbox = (CheckBox) (findViewById(R.id.checkBox));
        email_textfield = (EditText) (findViewById(R.id.email));
        password_textfield = (EditText) (findViewById(R.id.highsecuritypassword));


        final SQLiteDatabase connection = myDb.getWritableDatabase();

        Cursor res = connection.rawQuery("Select * from userinformation;", null);
        res.moveToFirst();

        firstlogin = res.getString(10).equalsIgnoreCase("true");

        //if you want to stay logged in this loads your credentials
        if (res.getString(6).equalsIgnoreCase("true")) {
            email_textfield.setText(res.getString(4));
            password_textfield.setText(res.getString(5));
            rememberme_checkbox.setChecked(true);
            //Log.e("Login DB User", res.getString(4));
            //Log.e("Login DB KEY", res.getString(5));
            //Log.e("Login DB BOOL", res.getString(6));
            //Log.e("Login DB LASTLOGIN", res.getString(7));
        }

        final String lastLogin = res.getString(7);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Daten von den Textfields und Checkboxes laden
                String email = email_textfield.getText().toString();
                freischaltcode = password_textfield.getText().toString();
                eingeloggt_bleiben = rememberme_checkbox.isChecked();


                //Test auf Fehler der Usereingabe email format a@b.c und freischaltcode nicht leer
                if (!email.matches(".+@.+\\..+")) {
                    Config.error_message(LoginActivity.this, "Invalide e-Mailadresse");
                }
                if (freischaltcode.length() == 0)
                    Config.error_message(LoginActivity.this, "Bitte Freischaltcode eingeben");


                //Erstellt eine Volley Request Queue
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                /*for testing
                email = "juergen.vonhirschheydt@forum-media.com";
                freischaltcode = "JVH_UNLOCK";
                */
                String url = Config.webserviceUrl + "ACC.UNLOCK?email=" + email + "&code=" + freischaltcode;

                JsonObjectRequest ipAddressJsonRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                String s = jsonObject.toString();
                                Gson gson = new Gson();

                                //session token
                                LoginResponse loginResponse = gson.fromJson(jsonObject.toString(), LoginResponse.class);

                                if (loginResponse.getSuccess().equalsIgnoreCase("false")) {

                                    switch (loginResponse.getErrorMessage()) {
                                        case "invalid code":
                                            Config.error_message(LoginActivity.this, "Invalider Entsperrcode");
                                            break;
                                        case "user not found":
                                            Config.error_message(LoginActivity.this, "Nutzer nicht gefunden");
                                            break;
                                        default:
                                            Config.error_message(LoginActivity.this, "Fehler");
                                            break;
                                    }
                                } else {
                                    //Log.e("Login Token", loginResponse.getToken());


                                    //Speichert den Token fuer die weitere Verwendung in der datenbank
                                    connection.execSQL("UPDATE userinformation SET sessionkey='" + loginResponse.getToken() + "', sessioncid='" + loginResponse.getCid() + "';");

                                    String strDate = getCurrentDate();

                                    //saving current logindate and if user prefers to stay signed in
                                    connection.execSQL("UPDATE userinformation SET stayloggedin=\"" + eingeloggt_bleiben + "\", lastlogin='" + strDate + "';");
                                    //Log.e("Login SQL UPDATE 1/2", "UPDATE userinformation SET stayloggedin=" + eingeloggt_bleiben + ", lastlogin='" + strDate + "';");


                                    boolean loggedInToday = strDate.equalsIgnoreCase(lastLogin);
                                    loggedInToday = false;

                                    if (!loggedInToday) {
                                        myDb.deleteAllUselessTablesLUL();

                                        String token = myDb.getToken(connection);

                                        String date = "2017-06-14"; //wow time flew by fast


                                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                                        //load event data
                                        String url = Config.webserviceUrl + "EVENT.DAILY?token=" + token + "&date=" + date;
                                        //Log.e("Event Daily URL", url);
                                        final JsonObjectRequest seminarRequest =
                                                new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                                    @Override
                                                    public void onResponse(JSONObject jsonObject) {
                                                        String s = jsonObject.toString();
                                                        Gson gson = new Gson();

                                                        Seminar seminar = gson.fromJson(jsonObject.toString(), Seminar.class);
                                                        //Log.e("Seminar DB EventAmnt", seminar.getEventAmount() + "");

                                                            saveEventToDatabase(seminar, connection);



                                                    }
                                                }, new Response.ErrorListener() {

                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                    }
                                                });
                                        queue.add(seminarRequest);
                                    }


                                    if (firstlogin && eingeloggt_bleiben) {

                                        try {
                                            TimeUnit.MILLISECONDS.sleep(333);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        //email and password saving in case user wants to
                                        connection.execSQL("UPDATE userinformation SET loginemail='" + email_textfield.getText() + "', firstlogin=\"false\", loginkey='" + freischaltcode + "';");

                                        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                                        startActivity(intent);
                                        //Log.e("Login SQL UPDATE 2/2", "UPDATE userinformation SET loginemail='" + email_textfield.getText() + "', loginkey='" + freischaltcode + "'");

                                    } else if (firstlogin && !eingeloggt_bleiben) {
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(333);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        connection.execSQL("UPDATE userinformation SET firstlogin=\"false\";");
                                        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                                        startActivity(intent);

                                    } else if (eingeloggt_bleiben) {
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(333);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        //email and password saving in case user wants to
                                        connection.execSQL("UPDATE userinformation SET loginemail='" + email_textfield.getText() + "', loginkey='" + freischaltcode + "';");

                                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                        startActivity(intent);
                                        //Log.e("Login SQL UPDATE 2/2", "UPDATE userinformation SET loginemail='" + email_textfield.getText() + "', loginkey='" + freischaltcode + "'");

                                    } else {
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(333);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }, new Response.ErrorListener()

                        {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Config.error_message(LoginActivity.this, "Keine Internetverbindung");
                            }
                        });

                queue.add(ipAddressJsonRequest);


            }
        });


    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(c.getTime());
    }


    private void saveEventToDatabase(Seminar seminar, SQLiteDatabase connection) {
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

            Log.e("SQL EVENT", ("Insert into events (event_id, id, title, description, author, start, end, street, zip, city, location, url) VALUES ('"
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
                    + event.getUrl() + "');"));

            for (int j = 0; j < event.getDocumentAmount(); j++) {
                Document doc = event.getDocument(j);
                connection.execSQL("Insert into documents (id, title, event_id) VALUES ('"
                        + doc.getId() + "' , '"
                        + doc.getTitle() + "' , '"
                        + event.getEventId() + "');");
            }


            Log.e("SaveDB DocumentAmnt", seminar.getEvent(i).getDocumentAmount() + "");
        }

        Log.e("SaveDB InterestAmnt", seminar.getInterestgroupAmount() + "");
        for (int i = 0; i < seminar.getInterestgroupAmount(); i++) {
            try {
                myDb.insertInterest(connection, seminar.getInterestgroup(i).getName());
            } catch (SQLiteConstraintException e) {
            }
            Log.e("Seminar Interest", seminar.getInterestgroup(i).getName());
        }
    }


}
