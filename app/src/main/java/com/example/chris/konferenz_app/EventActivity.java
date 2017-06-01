package com.example.chris.konferenz_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Chris on 15.05.2017.
 */

public class EventActivity extends AppCompatActivity {

    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        //retrieving the intent of the previous activity (in this case the eventid)
        int eventId = getIntent().getIntExtra("eventid",0);

       // Log.e("Int", eventId+"");


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        List<String> idList = new ArrayList<>();
        //THIS IS BULLSHIT AND NEEDS TO BE REPLACED
            idList.add("1");







        //WE SHOULD PASS THIS LIST OF FILES TO THE RECYCLER ADAPTER111!!



        RecyclerAdapter adapter = new RecyclerAdapter(this,idList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);








    }
}
