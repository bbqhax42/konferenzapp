package com.example.chris.konferenz_app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chris.konferenz_app.adapters.ChatActivityPrivateRecyclerAdapter;
import com.example.chris.konferenz_app.adapters.ChatActivityRecyclerAdapter;
import com.example.chris.konferenz_app.Config;
import com.example.chris.konferenz_app.DatabaseHelper;
import com.example.chris.konferenz_app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 06.06.2017.
 */

public class ChatActivity extends AppCompatActivity {
    Button chatButton, settingsButton, homeButton;
    RecyclerView recyclerView, recyclerViewPrivate;
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
        recyclerViewPrivate = (RecyclerView) findViewById(R.id.recycler_view2);

        //changes the button background to show which part of the program the user is accessing
        chatButton.setBackgroundResource(R.drawable.toolbar_button_selected);

        TextView headerTitle = (TextView) findViewById(R.id.title);
        headerTitle.setText("Chat");


        Cursor res = connection.rawQuery("Select * from userinformation;", null);
        res.moveToFirst();
        String userName = res.getString(0);

        if (userName == null || userName.length() == 0) {
            Config.error_message(ChatActivity.this, "Sichtbarer Name erforderlich für Chats");
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
        }


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
        List<String> privateChannelList = queryPrivateChannels(connection);

        //setupRecyclerview adapter

        ChatActivityRecyclerAdapter adapter = new ChatActivityRecyclerAdapter(ChatActivity.this, channelList);
        recyclerView.setAdapter(adapter);

        ChatActivityPrivateRecyclerAdapter adapter2 = new ChatActivityPrivateRecyclerAdapter(ChatActivity.this, privateChannelList);
        recyclerViewPrivate.setAdapter(adapter2);

        LinearLayoutManager llm = new LinearLayoutManager(ChatActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager llm2 = new LinearLayoutManager(ChatActivity.this);
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerViewPrivate.setLayoutManager(llm2);
    }

    private List<String> queryChannels(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from interests where isvisible=\"true\";", null);

        ArrayList<String> listOfChannels = new ArrayList<>();
        while (res.moveToNext()) {
            listOfChannels.add(res.getString(0));
        }
        //Log.e("List of Events Size", String.valueOf(listOfEvents.size()));
        return listOfChannels;
    }

    private List<String> queryPrivateChannels(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from privatechatlist where blocked=\"false\"", null);

        ArrayList<String> listOfChannels = new ArrayList<>();
        while (res.moveToNext()) {
            listOfChannels.add(res.getString(0));
        }
        //Log.e("List of Events Size", String.valueOf(listOfEvents.size()));
        return listOfChannels;
    }
}
