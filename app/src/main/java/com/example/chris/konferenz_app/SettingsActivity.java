package com.example.chris.konferenz_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Chris on 31.05.2017.
 */

public class SettingsActivity extends AppCompatActivity {

    EditText name, email, phone, company, addInterestEditText;
    TextView tv;
    Button update, deleteInterest, addInterest, chatButton, settingsButton, homeButton;
    String token;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phonenumber);
        company = (EditText) findViewById(R.id.company);
        update = (Button) findViewById(R.id.button);
        deleteInterest = (Button) findViewById(R.id.buttondel);
        addInterest = (Button) findViewById(R.id.buttonsave);
        tv = (TextView) findViewById(R.id.textview);
        addInterestEditText = (EditText) findViewById(R.id.interest);
        settingsButton = (Button) findViewById(R.id.settingsbutton);
        chatButton = (Button) findViewById(R.id.chatbutton);
        homeButton = (Button) findViewById(R.id.homebutton);
        name.clearFocus();


        final DatabaseHelper myDb = new DatabaseHelper(this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();

        loadInitialSettings(connection);


        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("EventUpdate", false + ""); //true if not logged in today yet, false if already logged in today
                startActivity(intent);
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override

            //volley request for documents here
            public void onClick(View v) {
                //too long names bug the chatview
                final String nameString = name.getText().toString().length()>20 ? name.getText().toString().substring(0, 19) : name.getText().toString();
                connection.execSQL("UPDATE userinformation SET name='" + nameString+ "', phonenumber='" + phone.getText().toString() + "', email='" + email.getText().toString() + "', company='" + company.getText().toString() + "';");
                Log.e("Setting SQL UPDATE", "UPDATE userinformation SET name='" + nameString + "', phonenumber='" + phone.getText().toString() + "', email='" + email.getText().toString() + "', company='" + company.getText().toString() + "';");

                RequestQueue queue = Volley.newRequestQueue(SettingsActivity.this);

                String url = Config.webserviceUrl + "USER.SETTINGS?token=" + token + "&visible=" + generateInterestsJsonString(connection) + "&profile=" + name.getText().toString() + "&phone=" + phone.getText().toString() + "&email=" + email.getText().toString() + "&company=" + company.getText().toString();
                Log.e("Event Daily URL", url);
                final JsonObjectRequest settingRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                String s = jsonObject.toString();
                                Gson gson = new Gson();

                                SettingResponse settingResponse = gson.fromJson(jsonObject.toString(), SettingResponse.class);

                                StringBuffer settingResponseString = new StringBuffer("Status: " + settingResponse.getStatus() + "\n"
                                        + "Status Info: " + settingResponse.getStatus_info() + "\n"
                                        + "Name:" + settingResponse.getProfile_name() + "\n"
                                        + "Tel.Nr.: " + settingResponse.getProfile_phone() + "\n"
                                        + "e-Mail: " + settingResponse.getProfile_email() + "\n"
                                        + "Firma: " + settingResponse.getProfile_company() + "\n"
                                        + "Sichtbare Interessen: ");


                                if (settingResponse.getInterestgroupAmount() == 0) {
                                    settingResponseString.append("keine");
                                }
                                for (int i = 0; i < settingResponse.getInterestgroupAmount(); i++) {
                                    settingResponseString.append(settingResponse.getInterestgroup(i).getName());
                                    if (i + 1 < settingResponse.getInterestgroupAmount())
                                        settingResponseString.append(", ");
                                }

                                Config.popupMessage("Ihre Einstellungen wurden aktualisiert", settingResponseString.toString(), SettingsActivity.this);
                                try {
                                    connection.execSQL("Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) VALUES ('"
                                            + myDb.getCid(connection) + "', '"
                                            + nameString + "', '"
                                            + phone.getText() + "', '"
                                            + email.getText() + "', '"
                                            + company.getText() + "');");
                                } catch (SQLiteConstraintException e) {
                                    connection.execSQL("Update users SET " +
                                            "profile_name='" + nameString + "', " +
                                            "profile_phone='" + phone.getText() + "', " +
                                            "profile_email='" + email.getText() + "', " +
                                            "profile_company='" + company.getText() + "' " +
                                            "Where cid='" + myDb.getCid(connection) + "';");
                                }


                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ///!!!!!!!!!!!!!!!!!!!!!!
                                try {
                                    connection.execSQL("Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) VALUES ('"
                                            + myDb.getCid(connection) + "', '"
                                            + nameString + "', '"
                                            + phone.getText() + "', '"
                                            + email.getText() + "', '"
                                            + company.getText() + "');");
                                } catch (SQLiteConstraintException e) {
                                    connection.execSQL("Update users SET " +
                                            "profile_name='" + nameString + "', " +
                                            "profile_phone='" + phone.getText() + "', " +
                                            "profile_email='" + email.getText() + "', " +
                                            "profile_company='" + company.getText() + "' " +
                                            "Where cid='" + myDb.getCid(connection) + "';");
                                }

                                //NOCH LOESCHEN WICHTIG111111!!!!


                            }
                        });
                queue.add(settingRequest);
            }


        });

        addInterest.setOnClickListener(new View.OnClickListener() {
            @Override

            //volley request for documents here
            public void onClick(View v) {
                try {
                    myDb.insertInterest(connection, addInterestEditText.getText().toString());
                    Config.popupMessage("Interessensgruppe hinzugefügt", "Sie haben sich in " + addInterestEditText.getText().toString() + " eingetragen.", SettingsActivity.this);
                } catch (SQLiteConstraintException e) {
                    Config.popupMessage("Interessensgruppe nicht hinzugefügt", "Sie sind bereits in " + addInterestEditText.getText().toString() + " eingetragen.", SettingsActivity.this);
                }

                tv.setText(generateInterests(connection));
            }
        });

        deleteInterest.setOnClickListener(new View.OnClickListener() {
            @Override

            //volley request for documents here
            public void onClick(View v) {
                connection.execSQL("DELETE FROM interests WHERE name='" + addInterestEditText.getText().toString() + "';");
                Config.popupMessage("Interessensgruppe entfernt", "Sie haben sich aus " + addInterestEditText.getText().toString() + " entfernt.", SettingsActivity.this);
                tv.setText(generateInterests(connection));
            }
        });
    }


    //loads the initial settings from DB and puts them on screen
    private void loadInitialSettings(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from userinformation;", null);
        res.moveToFirst();

        name.setText(res.getString(0));
        email.setText(res.getString(2));
        phone.setText(res.getString(1));
        company.setText(res.getString(3));
        token = res.getString(8);
        tv.setText(generateInterests(connection));

    }


    private String generateInterests(SQLiteDatabase connection) {
        Cursor res;
        res = connection.rawQuery("Select * from interests;", null);
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append(res.getString(0));
            buffer.append(", ");
        }
        //removes ', ' from last loop
        String tmpInterest = buffer.toString();
        if (tmpInterest.length() != 0)
            return tmpInterest.substring(0, tmpInterest.length() - 2);
        else return null;
    }

    private String generateInterestsJsonString(SQLiteDatabase connection) {
        Cursor res;
        res = connection.rawQuery("Select * from interests;", null);
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append(res.getString(0));
            buffer.append(",");
        }
        //removes ',' from last loop
        String tmpInterest = buffer.toString();
        if (tmpInterest.length() != 0)
            return tmpInterest.substring(0, tmpInterest.length() - 1);
        else return null;
    }
}
