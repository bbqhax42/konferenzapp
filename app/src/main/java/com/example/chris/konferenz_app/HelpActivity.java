package com.example.chris.konferenz_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Chris on 20.06.2017.
 */

public class HelpActivity extends AppCompatActivity {

    Button chatButton, settingsButton, homeButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView tv = (TextView) findViewById(R.id.title);
        tv.setText("Zusätzliche Informationen");

        settingsButton = (Button) findViewById(R.id.settingsbutton);
        chatButton = (Button) findViewById(R.id.chatbutton);
        homeButton = (Button) findViewById(R.id.homebutton);


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

    }
}
