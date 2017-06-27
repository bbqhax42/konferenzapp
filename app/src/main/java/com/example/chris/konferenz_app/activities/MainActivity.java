package com.example.chris.konferenz_app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chris.konferenz_app.DatabaseHelper;
import com.example.chris.konferenz_app.data.Event;
import com.example.chris.konferenz_app.adapters.MainActivityRecyclerAdapter;
import com.example.chris.konferenz_app.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper myDb = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button chatButton, settingsButton, homeButton;
        chatButton = (Button) findViewById(R.id.chatbutton);
        settingsButton = (Button) findViewById(R.id.settingsbutton);
        homeButton = (Button) findViewById(R.id.homebutton);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //changes the button background to show which part of the program the user is accessing
        homeButton.setBackgroundResource(R.drawable.toolbar_button_selected);

        TextView tv = (TextView) findViewById(R.id.title);
        tv.setText("Start");

        final SQLiteDatabase connection = myDb.getWritableDatabase();

        populateView(connection);


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


    }

    private void populateView(SQLiteDatabase connection) {
        List<Event> eventsList = queryEvents(connection);

        MainActivityRecyclerAdapter adapter = new MainActivityRecyclerAdapter(MainActivity.this, eventsList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
    }

    private List<Event> queryEvents(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from events", null);

        List<Event> listOfEvents = new ArrayList<>();
        while (res.moveToNext()) {
            Event event = new Event();
            event.setEvent_id(Integer.parseInt(res.getString(0)));
            event.setId(res.getString(1));
            event.setTitle(res.getString(2));
            event.setDescription(res.getString(3));
            event.setAuthor(res.getString(4));
            event.setStart(res.getString(5));
            event.setEnd(res.getString(6));
            event.setStreet(res.getString(7));
            event.setZip(res.getString(8));
            event.setCity(res.getString(9));
            event.setLocation(res.getString(10));
            event.setUrl(res.getString(11));
            //Log.e("List Event ID", event.getEventId() + "");

            listOfEvents.add(event);

        }
        //Log.e("List of Events Size", String.valueOf(listOfEvents.size()));
        return listOfEvents;
    }

}
