package com.example.chris.konferenz_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Chris on 15.05.2017.
 */

public class EventActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button downloadButton, viewMoreButton, chatButton, settingsButton, homeButton;
    int eventId = -1;
    TextView title, textfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        final DatabaseHelper myDb = new DatabaseHelper(this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();


        //retrieving the intent of the previous activity (in this case the eventid)
        eventId = getIntent().getIntExtra("EventID", 0);

        final Event event = myDb.getData(connection, eventId);


        title = (TextView) findViewById(R.id.title);
        textfield = (TextView) findViewById(R.id.textfield);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        downloadButton = (Button) findViewById(R.id.button);
        viewMoreButton = (Button) findViewById(R.id.viewmorebutton);
        settingsButton = (Button) findViewById(R.id.settingsbutton);
        chatButton = (Button) findViewById(R.id.chatbutton);
        homeButton = (Button) findViewById(R.id.homebutton);

        viewMoreButton.setPaintFlags(viewMoreButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        title.setText(event.getTitle());
        textfield.setText(event.getDescription());


        List<Document> idList = queryDocuments(connection);
        final EventActivityRecyclerAdapter adapter = new EventActivityRecyclerAdapter(EventActivity.this, idList);
        recyclerView.setAdapter(adapter);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        //hides the downloadbutton if there are no files to show
        if(adapter.getItemCount()==0){
            downloadButton.setVisibility(View.GONE);
        }

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
                intent.putExtra("EventUpdate", false + ""); //true if not logged in today yet, false if already logged in today
                startActivity(intent);
            }
        });


        viewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Autor: " + event.getAuthor() + "\n"
                        + "Start: " + Config.formatDates(event.getStart()) + " Uhr\n"
                        + "Ende: " + Config.formatDates(event.getEnd()) + " Uhr\n"
                        + "Strasse: " + event.getStreet() + "\n"
                        + "Ort: " + event.getZip() + " " + event.getCity() + "\n"
                        + "Zusatzangaben: " + event.getLocation() + "\n"
                        + "Website: " + event.getUrl();

               Config.popupMessage("Mehr Informationen zu " + event.getTitle(), str, EventActivity.this);
            }
        });


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override

            //volley request for documents here
            public void onClick(View v) {

                ArrayList<String> selectedFiles = new ArrayList<String>();
                StringBuffer downloadString = new StringBuffer();

                for (int i = 0; i < adapter.getItemCount(); ++i) {
                    EventActivityRecyclerAdapter.Holder holder = (EventActivityRecyclerAdapter.Holder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (holder.checkBox.isChecked()) {
                        selectedFiles.add(holder.doc.getId() + "");
                        //Log.e("Checked", holder.doc.getTitle() + "");
                    } else {
                        //Log.e("Not Checked", holder.doc.getTitle());
                    }
                }
                for (int i = 0; i < selectedFiles.size(); i++) {
                    downloadString.append(selectedFiles.get(i));
                    if (i + 1 < selectedFiles.size()) downloadString.append(",");
                }


                String token = myDb.getToken(connection);

                RequestQueue queue = Volley.newRequestQueue(EventActivity.this);

                String url = Config.webserviceUrl + "DOC.REQUEST?token=" + token + "&doclist=" + downloadString;

                Log.e("DL String", url);

                JsonObjectRequest documentRequestRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                String s = jsonObject.toString();
                                Gson gson = new Gson();

                                //session token
                                DocumentRequest documentRequest = gson.fromJson(jsonObject.toString(), DocumentRequest.class);

                                StringBuffer documentRequestString = new StringBuffer("Status: " + documentRequest.getStatus() + "\n"
                                        + "Empfänger: " + documentRequest.getRecipient() + "\n"
                                        + "Betreff: " + documentRequest.getSubject() + "\n"
                                        + "Dokumente: ");


                                if (documentRequest.getDocumentAmount() == 0) {
                                    documentRequestString.append("keine");
                                }
                                for (int i = 0; i < documentRequest.getDocumentAmount(); i++) {
                                    documentRequestString.append(documentRequest.getDocument(i).getTitle());
                                    if (i + 1 < documentRequest.getDocumentAmount())
                                        documentRequestString.append(", ");
                                }

                                Config.popupMessage("Mehr Informationen zu ihrem Download", documentRequestString.toString(), EventActivity.this);

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Config.error_message(EventActivity.this, "Sie haben keine Dateien zum Download ausgewählt");
                            }
                        });

                queue.add(documentRequestRequest);

            }
        });


    }

    private List<Document> queryDocuments(SQLiteDatabase connection) {

        List<Document> listofDocuments = new ArrayList<Document>();
        Cursor documents = connection.rawQuery("Select * from documents where event_id=" + eventId + ";", null);
        while (documents.moveToNext()) {
            Document doc = new Document(Integer.parseInt(documents.getString(0)), documents.getString(1));
            listofDocuments.add(doc);
            //Log.e("List Document ID", doc.getId() + "");
            //Log.e("List Document Title", doc.getTitle() + "");
        }


        return listofDocuments;
    }


}
