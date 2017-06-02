package com.example.chris.konferenz_app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Chris on 15.05.2017.
 */

public class EventActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button downloadButton;
    int eventId = -1;
    TextView title, textfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        DatabaseHelper myDb = new DatabaseHelper(this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();


        //retrieving the intent of the previous activity (in this case the eventid)
        eventId = getIntent().getIntExtra("EventID", 0);

        Event event = getData(connection, eventId);


        title = (TextView) findViewById(R.id.title);
        textfield = (TextView) findViewById(R.id.textfield);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        downloadButton = (Button) findViewById(R.id.button);

        title.setText(event.getTitle());
        textfield.setText(event.getDescription());


        List<Document> idList = queryDocuments(connection);
        final RecyclerAdapter adapter = new RecyclerAdapter(EventActivity.this, idList);
        recyclerView.setAdapter(adapter);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < adapter.getItemCount(); ++i) {
                    RecyclerAdapter.Holder holder = (RecyclerAdapter.Holder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (holder.checkBox.isChecked()) {
                        Log.e("Checked", String.valueOf(i));
                    } else {
                        Log.e("Not Checked", String.valueOf(i));
                    }
                }
            }
        });


    }

    private List<Document> queryDocuments(SQLiteDatabase connection) {

        List<Document> listofDocuments = new ArrayList<Document>();
        Cursor documents = connection.rawQuery("Select * from documents where event_id=" + eventId + ";", null);
        while (documents.moveToNext()) {
            Document doc = new Document(Integer.parseInt(documents.getString(0)), documents.getString(1));
            listofDocuments.add(doc);
            Log.e("List Document ID", doc.getId() + "");
            Log.e("List Document Title", doc.getTitle() + "");
        }


        return listofDocuments;
    }

    private Event getData(SQLiteDatabase connection, int eventId) {
        Log.e("eventid", "Select * from events where event_id=" + eventId + ";");
        Cursor result = connection.rawQuery("Select * from events where event_id=" + eventId + ";", null);
        result.moveToFirst();
        Event event = new Event();
        event.setEvent_id(Integer.parseInt(result.getString(0)));
        event.setId(result.getString(1));
        event.setTitle(result.getString(2));
        event.setDescription(result.getString(3));
        event.setAuthor(result.getString(4));
        event.setStart(result.getString(5));
        event.setEnd(result.getString(6));
        event.setStreet(result.getString(7));
        event.setZip(result.getString(8));
        event.setCity(result.getString(9));
        event.setLocation(result.getString(10));
        event.setUrl(result.getString(11));
        return event;
    }
}
