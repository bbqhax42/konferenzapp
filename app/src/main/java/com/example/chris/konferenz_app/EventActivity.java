package com.example.chris.konferenz_app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
    Button downloadButton, viewMoreButton;
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

        final Event event = getData(connection, eventId);


        title = (TextView) findViewById(R.id.title);
        textfield = (TextView) findViewById(R.id.textfield);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        downloadButton = (Button) findViewById(R.id.button);
        viewMoreButton = (Button) findViewById(R.id.viewmorebutton);

        title.setText(event.getTitle());
        textfield.setText(event.getDescription());


        List<Document> idList = queryDocuments(connection);
        final RecyclerAdapter adapter = new RecyclerAdapter(EventActivity.this, idList);
        recyclerView.setAdapter(adapter);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        viewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Autor: " + event.getAuthor() + "\n"
                        + "Start: " + event.getStart() + "\n"
                        + "Ende:" + event.getEnd() + "\n"
                        + "Strasse: " + event.getStreet() + "\n"
                        + "Ort: " + event.getZip() + " " + event.getCity() + "\n"
                        + "Zusatzangaben: " + event.getLocation() + "\n"
                        + "Website: " + event.getUrl();

                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Mehr Informationen zu " + event.getTitle());
                builder.setMessage(str);
                builder.show();
            }
        });


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override

            //volley request for documents here
            public void onClick(View v) {

                ArrayList<String> selectedFiles = new ArrayList<String>();
                StringBuffer downloadString = new StringBuffer();

                for (int i = 0; i < adapter.getItemCount(); ++i) {
                    RecyclerAdapter.Holder holder = (RecyclerAdapter.Holder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (holder.checkBox.isChecked()) {
                        selectedFiles.add(holder.doc.getId() + "");
                        Log.e("Checked", holder.doc.getId() + "");
                    } else {
                        Log.e("Not Checked", holder.doc.getTitle());
                    }
                }
                for (int i = 0; i < selectedFiles.size(); i++) {
                    downloadString.append(selectedFiles.get(i));
                    if (i + 1 < selectedFiles.size()) downloadString.append(",");
                }

                Cursor res = connection.rawQuery("Select * from userinformation;", null);
                res.moveToFirst();

                String token = res.getString(8);

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
                                        + "Status Info: " + documentRequest.getStatus_info() + "\n"
                                        + "Empfänger:" + documentRequest.getRecipient() + "\n"
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                                builder.setCancelable(true);
                                builder.setTitle("Mehr Informationen zu ihrem Download");
                                builder.setMessage(documentRequestString.toString());
                                builder.show();

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error", error.getMessage());
                                Log.e("Error", "error"); //parse the returned string here
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
