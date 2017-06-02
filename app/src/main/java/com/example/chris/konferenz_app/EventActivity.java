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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Chris on 15.05.2017.
 */

public class EventActivity extends AppCompatActivity {

    RecyclerView recyclerView;
Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        DatabaseHelper myDb = new DatabaseHelper(this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();


        //retrieving the intent of the previous activity (in this case the eventid)
        int eventId = getIntent().getIntExtra("EventID",0);

       // Log.e("Int", eventId+"");


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        List<Document> idList = queryDocuments(connection);





        //WE SHOULD PASS THIS LIST OF FILES TO THE RECYCLER ADAPTER111!!



        final RecyclerAdapter adapter = new RecyclerAdapter(EventActivity.this,idList);
        recyclerView.setAdapter(adapter);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);





        downloadButton = (Button) findViewById(R.id.button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            for (int i = 0; i < adapter.getItemCount(); ++i) {
                    RecyclerAdapter.Holder holder = ( RecyclerAdapter.Holder) recyclerView.findViewHolderForAdapterPosition(i);
                    if(holder.checkBox.isChecked()){
                        Log.e("Checked", String.valueOf(i));
                    } else {
                        Log.e("Not Checked", String.valueOf(i));
                    }
                }
            }
        });



    }

    private List<Document> queryDocuments(SQLiteDatabase connection){
        Cursor res=connection.rawQuery("Select * from documents", null);

        List<Document> listofDocuments = new ArrayList<>();
        while(res.moveToNext()){
            Document document = new Document();
            document.setTitle((res.getString(1)));
            listofDocuments.add(document);

        }
        //Log.e("ErrorDocuments", String.valueOf(listofDocuments.size()));
        return listofDocuments;
    }
}
