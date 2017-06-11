package com.example.chris.konferenz_app;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Chris on 06.06.2017.
 */

public class UserActivity extends AppCompatActivity {

    TextView name, email, phone, companyTV, title;
    Button privatechat, savecontact, chatButton, settingsButton, homeButton, blockButton, unblockButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        final String cid = getIntent().getStringExtra("Cid");



        settingsButton = (Button) findViewById(R.id.settingsbutton);
        chatButton = (Button) findViewById(R.id.chatbutton);
        homeButton = (Button) findViewById(R.id.homebutton);
        privatechat = (Button) findViewById(R.id.privatechatbutton);
        savecontact = (Button) findViewById(R.id.contactbutton);
        blockButton = (Button) findViewById(R.id.blockbutton);
        unblockButton = (Button) findViewById(R.id.unblockbutton);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone);
        companyTV = (TextView) findViewById(R.id.company);
        title = (TextView) findViewById(R.id.title);

        String displayName="", workNumber="", emailID="", company="";
        final String jobTitle = "Kein Jobtitel";

        DatabaseHelper myDb = new DatabaseHelper(UserActivity.this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();
        Cursor res = connection.rawQuery("Select * from users where cid='" + cid + "';", null);
        if (res.moveToNext()) {
            displayName = res.getString(1);
            workNumber = res.getString(2);
            emailID = res.getString(3);
            company = res.getString(4);
        }
        else
           displayName="Unbekannt";

        name.setText(displayName);
        email.setText(emailID);
        phone.setText(workNumber);
        companyTV.setText(company);
        title.setText("Profil von " +displayName);


        final String displayNameStr=displayName;
        final String workNumberStr=workNumber;
        final String emailIDStr=emailID;
        final String companyStr=company;

        privatechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, ChatChannelPrivateActivity.class);
                intent.putExtra("ChannelName", cid); //goes to the selected channel!!
                UserActivity.this.startActivity(intent);
            }
        });

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

        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection.execSQL("Update privatechatlist SET blocked=\"TRUE\" where cid='" + cid + "';");
            }
        });

        unblockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection.execSQL("Update privatechatlist SET blocked=\"FALSE\" where cid='" + cid + "';");
            }
        });

        savecontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList <ContentProviderOperation> ops = new ArrayList< ContentProviderOperation >();

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());


                //------------------------------------------------------ Names
                if (displayNameStr != null) {
                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    displayNameStr).build());
                }


                //------------------------------------------------------ Work Numbers
                if (workNumberStr != null) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, workNumberStr)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                            .build());
                }

                //------------------------------------------------------ Email
                if (emailIDStr != null) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailIDStr)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                            .build());
                }

                //------------------------------------------------------ Organization
                if (!companyStr.equals("") && !jobTitle.equals("")) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, companyStr)
                            .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                            .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                            .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                            .build());
                }

                // Asking the Contact provider to create a new contact
                try {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    Config.popupMessage("Kontakt erstellt", "Sie haben "+ displayNameStr+ " erfolgreich zu ihren Kontakten hinzugefügt.", UserActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", e.getMessage());
                    Config.popupMessage("Kontakt nicht erstellt", "Wir konnten "+ displayNameStr+ "nicht zu ihren Kontakten hinzugefügen. Bitte stellen Sie sicher das die App die nötigen Berechtigungen hat.", UserActivity.this);
                }
            }
        });


    }

}
