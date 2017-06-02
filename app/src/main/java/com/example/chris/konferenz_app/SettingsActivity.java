package com.example.chris.konferenz_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Chris on 31.05.2017.
 */

public class SettingsActivity extends AppCompatActivity {

    EditText name, email, phone, company;
    Button update;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        name= (EditText) findViewById(R.id.name);
        email= (EditText) findViewById(R.id.email);
        phone= (EditText) findViewById(R.id.phonenumber);
        company= (EditText) findViewById(R.id.company);
        update = (Button) findViewById(R.id.button);
    }
}
