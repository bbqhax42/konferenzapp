package com.example.chris.konferenz_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 06.06.2017.
 */

public class ChatActivity extends AppCompatActivity {
    Button chatButton, settingsButton, homeButton;
    RecyclerView recyclerView;
    DatabaseHelper myDb = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final SQLiteDatabase connection = myDb.getWritableDatabase();

        settingsButton = (Button) findViewById(R.id.settingsbutton);
        chatButton = (Button) findViewById(R.id.chatbutton);
        homeButton = (Button) findViewById(R.id.homebutton);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        populateView(connection);

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
    }


    private void populateView(SQLiteDatabase connection) {
        List<String> channelList = queryChannels(connection);

        //setupRecyclerview adapter

        ChatActivityRecyclerAdapter adapter = new ChatActivityRecyclerAdapter(ChatActivity.this, channelList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(ChatActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);
    }

    private List<String> queryChannels(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from interests", null);

        ArrayList<String> listOfChannels = new ArrayList<>();
        while (res.moveToNext()) {
            listOfChannels.add(res.getString(0));

        }
        //Log.e("List of Events Size", String.valueOf(listOfEvents.size()));
        return listOfChannels;
    }
}
