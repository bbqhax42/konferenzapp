package com.example.chris.konferenz_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Chris on 14.05.2017.
 */

public class LoginActivity extends AppCompatActivity {


    Button login_button;
    CheckBox rememberme_checkbox;
    EditText email_textfield, password_textfield;
    boolean eingeloggt_bleiben;
    String freischaltcode;


    //Returns little bubble visible for the user with errormessage
    private void error_message(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = (Button) (findViewById(R.id.button));
        rememberme_checkbox = (CheckBox) (findViewById(R.id.checkBox));
        email_textfield = (EditText) (findViewById(R.id.email));
        password_textfield = (EditText) (findViewById(R.id.highsecuritypassword));


        DatabaseHelper myDb = new DatabaseHelper(this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();
        final Seminar seminar;

        Cursor res = connection.rawQuery("Select * from userinformation;", null);
        res.moveToFirst();

        //if you want to stay logged in this loads your credentials
        if (res.getString(6).equalsIgnoreCase("true")) {
            email_textfield.setText(res.getString(4));
            password_textfield.setText(res.getString(5));
            rememberme_checkbox.setChecked(true);
            Log.e("Login DB User", res.getString(4));
            Log.e("Login DB KEY", res.getString(5));
            Log.e("Login DB BOOL", res.getString(6));
            Log.e("Login DB LASTLOGIN", res.getString(7));
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
                    error_message("Invalide e-Mailadresse");
                }
                if (freischaltcode.length() == 0) error_message("Bitte Freischaltcode eingeben");


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
                                if (loginResponse.getCid().trim().equals("") || loginResponse.getToken().trim().equals("")) {
                                    //error message
                                    return;
                                }

                                Log.e("Login Token", loginResponse.getToken());


                                //Speichert den Token fuer die weitere Verwendung in der datenbank
                                connection.execSQL("UPDATE userinformation SET sessionkey='" + loginResponse.getToken() + "', sessioncid='" + loginResponse.getCid() + "';");

                                String strDate = getCurrentDate();
                                boolean eventUpdate = true;
                                if (strDate.equalsIgnoreCase(lastLogin)) {
                                    eventUpdate = false;
                                }

                                //saving current logindate and if user prefers to stay signed in
                                connection.execSQL("UPDATE userinformation SET stayloggedin=\"" + eingeloggt_bleiben + "\", lastlogin='" + strDate + "';");
                                Log.e("Login SQL UPDATE 1/2", "UPDATE userinformation SET stayloggedin=" + eingeloggt_bleiben + ", lastlogin='" + strDate + "';");

                                if (eingeloggt_bleiben) {
                                    //email and password saving in case user wants to
                                    connection.execSQL("UPDATE userinformation SET loginemail='" + email_textfield.getText() + "', loginkey='" + freischaltcode + "';");
                                    Log.e("Login SQL UPDATE 2/2", "UPDATE userinformation SET loginemail='" + email_textfield.getText() + "', loginkey='" + freischaltcode + "'");

                                }

                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                intent.putExtra("EventUpdate", eventUpdate + ""); //true if not logged in today yet, false if already logged in today
                                startActivity(intent);

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error", error.getMessage());
                                Log.e("Error", "error"); //parse the returned string here
                                error_message("Falsche Logindaten");
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


}
