package com.example.chris.konferenz_app;

import android.content.Intent;
import android.database.Cursor;
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
    Button update, deleteInterest, addInterest;
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

        DatabaseHelper myDb = new DatabaseHelper(this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();

        loadInitialSettings(connection);


        update.setOnClickListener(new View.OnClickListener() {
            @Override

            //volley request for documents here
            public void onClick(View v) {

                RequestQueue queue = Volley.newRequestQueue(SettingsActivity.this);

                String url = Config.webserviceUrl + "USER.SETTINGS?token=" + token + "&visible=" + generateInterestsJsonString(connection) + "&profile=" + name.getText().toString() + "&phone=" + phone.getText().toString() + "&company=" + company.getText().toString() + "&email=" + email.getText().toString();
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                                builder.setCancelable(true);
                                builder.setTitle("Ihre Einstellungen wurden aktualisiert");
                                builder.setMessage(settingResponseString.toString());
                                builder.show();


                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Back to start noob", error.getMessage());
                            }
                        });
                queue.add(settingRequest);
            }


        });

        addInterest.setOnClickListener(new View.OnClickListener() {
            @Override

            //volley request for documents here
            public void onClick(View v) {
                connection.execSQL("Insert into interests (name) VALUES ('" + addInterestEditText.getText().toString() + "');");
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Interessensgruppe entfernt");
                builder.setMessage("Sie haben sich in " + addInterestEditText.getText().toString() + " eingetragen.");
                builder.show();
                tv.setText(generateInterests(connection));
            }
        });

        deleteInterest.setOnClickListener(new View.OnClickListener() {
            @Override

            //volley request for documents here
            public void onClick(View v) {
                connection.execSQL("DELETE FROM interests WHERE name='" + addInterestEditText.getText().toString() + "';");
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Interessensgruppe entfernt");
                builder.setMessage("Sie haben sich aus " + addInterestEditText.getText().toString() + " entfernt.");
                builder.show();
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
        return tmpInterest.substring(0, tmpInterest.length() - 2);
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
        return tmpInterest.substring(0, tmpInterest.length() - 1);
    }
}
