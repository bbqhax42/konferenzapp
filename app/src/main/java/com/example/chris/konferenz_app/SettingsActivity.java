package com.example.chris.konferenz_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 31.05.2017.
 */

public class SettingsActivity extends AppCompatActivity {

    EditText name, email, phone, company;
    TextView tv;
    Button chatButton, settingsButton, homeButton, updateSettings, logout;
    String token;
    RecyclerView recyclerView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phonenumber);
        company = (EditText) findViewById(R.id.company);
        settingsButton = (Button) findViewById(R.id.settingsbutton);
        chatButton = (Button) findViewById(R.id.chatbutton);
        homeButton = (Button) findViewById(R.id.homebutton);
        updateSettings = (Button) findViewById(R.id.button);
        logout = (Button) findViewById(R.id.logoutbutton);
        name.clearFocus();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        settingsButton.setBackgroundResource(R.drawable.toolbar_button_selected);
        TextView tv = (TextView) findViewById(R.id.title);
        tv.setText("Einstellungen");


        final DatabaseHelper myDb = new DatabaseHelper(this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();

        loadInitialSettings(connection);

        List<Interestgroup> interestList = queryInterests(connection);
        final SettingsActivityRecyclerAdapter adapter = new SettingsActivityRecyclerAdapter(SettingsActivity.this, interestList);
        recyclerView.setAdapter(adapter);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


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
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection.execSQL("Delete from userinformation;");
                connection.execSQL("INSERT INTO userinformation (name, phonenumber, email, company, loginemail, loginkey, stayloggedin, lastlogin, sessionkey, sessioncid, firstlogin) VALUES (" + null + ", " + null + ", " + null + ", " + null + ", " + null + ", " + null + ", \"false\", '1970-01-01',  " + null + ",  " + null + ", \"true\");");
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                Config.error_message(SettingsActivity.this, "Erfolgreich ausgeloggt.");
            }
        });

        updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override

            //volley request for documents here
            public void onClick(View v) {
                //too long names bug the chatview

                ArrayList<Interestgroup> selectedFiles = new ArrayList<Interestgroup>();
                for (int i = 0; i < adapter.getItemCount(); ++i) {
                    SettingsActivityRecyclerAdapter.Holder holder = (SettingsActivityRecyclerAdapter.Holder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (holder.checkBox.isChecked()) {
                        holder.interestgroup.setVisible(true);
                        selectedFiles.add(holder.interestgroup);
                        Log.e("Checked", holder.interestgroup.getName());
                    } else {
                        holder.interestgroup.setVisible(false);
                        selectedFiles.add(holder.interestgroup);
                        Log.e("Not Checked", holder.interestgroup.getName());
                    }
                }

                updateInterests(connection, selectedFiles);


                final String nameString = name.getText().toString().length() > 20 ? name.getText().toString().substring(0, 19).trim().replace("\\n", "") : name.getText().toString().trim().replace("\\n", "");
                connection.execSQL("UPDATE userinformation SET name='" + nameString + "', phonenumber='" + phone.getText().toString().replace("\\n", "").trim() + "', email='" + email.getText().toString().replace("\\n", "").trim() + "', company='" + company.getText().toString().replace("\\n", "").trim() + "';");
                Log.e("Setting SQL UPDATE", "UPDATE userinformation SET name='" + nameString + "', phonenumber='" + phone.getText().toString().replace("\\n", "").trim() + "', email='" + email.getText().toString().replace("\\n", "").trim() + "', company='" + company.getText().toString().replace("\\n", "").trim() + "';");


                RequestQueue queue = Volley.newRequestQueue(SettingsActivity.this);

                String url = Config.webserviceUrl + "USER.SETTINGS?token=" + token + "&visible=" + generateInterestsJsonString(selectedFiles) + "&profile=" + name.getText().toString().replace("\\n", "").trim() + "&phone=" + phone.getText().toString().replace("\\n", "").trim() + "&email=" + email.getText().toString().replace("\\n", "").trim() + "&company=" + company.getText().toString().replace("\\n", "").trim();
                Log.e("Event Daily URL", url);
                final JsonObjectRequest settingRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                String s = jsonObject.toString();
                                Gson gson = new Gson();

                                SettingResponse settingResponse = gson.fromJson(jsonObject.toString(), SettingResponse.class);

                                StringBuffer settingResponseString = new StringBuffer("Status: " + settingResponse.getStatus() + "\n"
                                        + "Name: " + settingResponse.getProfile_name() + "\n"
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
                                            + phone.getText().toString().replace("\\n", "").trim() + "', '"
                                            + email.getText().toString().replace("\\n", "").trim() + "', '"
                                            + company.getText().toString().replace("\\n", "").trim() + "');");
                                } catch (SQLiteConstraintException e) {
                                    connection.execSQL("Update users SET " +
                                            "profile_name='" + nameString + "', " +
                                            "profile_phone='" + phone.getText().toString().replace("\\n", "").trim() + "', " +
                                            "profile_email='" + email.getText().toString().replace("\\n", "").trim() + "', " +
                                            "profile_company='" + company.getText().toString().replace("\\n", "").trim() + "' " +
                                            "Where cid='" + myDb.getCid(connection).toString().replace("\\n", "").trim() + "';");
                                }


                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Config.error_message(SettingsActivity.this, "Bitte überprüfen sie Ihre Internetverbindung");
                            }
                        });
                queue.add(settingRequest);
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

    }

    private void updateInterests(SQLiteDatabase connection, ArrayList<Interestgroup> selectedFiles) {
        for (int i = 0; i < selectedFiles.size(); i++) {
            connection.execSQL("Update interests SET " +
                    "isvisible=\"" + selectedFiles.get(i).isVisible() + "\" Where name='" + selectedFiles.get(i).getName() + "';");
            Log.e("updateInteestdasdsas", "Update interests SET " +
                    "isvisible=\"" + selectedFiles.get(i).isVisible() + "\" Where name='" + selectedFiles.get(i).getName() + "';");
        }
    }


    private String generateInterestsJsonString(ArrayList<Interestgroup> selectedFiles) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < selectedFiles.size(); i++) {
            if (selectedFiles.get(i).isVisible()) {
                buffer.append(selectedFiles.get(i).getName());
                buffer.append(",");
            }
        }
        //removes ',' from last loop
        String tmpInterest = buffer.toString();
        if (tmpInterest.length() != 0)
            return tmpInterest.substring(0, tmpInterest.length() - 1);
        else return "";
    }


    private List<Interestgroup> queryInterests(SQLiteDatabase connection) {

        List<Interestgroup> listofInterestgroups = new ArrayList<Interestgroup>();
        Cursor interestgroups = connection.rawQuery("Select * from interests;", null);
        while (interestgroups.moveToNext()) {
            Interestgroup interestgroup = new Interestgroup(interestgroups.getString(0), interestgroups.getString(1).equalsIgnoreCase("true"));
            listofInterestgroups.add(interestgroup);
        }
        return listofInterestgroups;
    }
}
