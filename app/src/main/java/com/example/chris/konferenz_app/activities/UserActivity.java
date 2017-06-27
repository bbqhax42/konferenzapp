package com.example.chris.konferenz_app.activities;

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
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.chris.konferenz_app.Config;
import com.example.chris.konferenz_app.DatabaseHelper;
import com.example.chris.konferenz_app.R;

import java.util.ArrayList;

/**
 * Created by Chris on 06.06.2017.
 */

public class UserActivity extends AppCompatActivity {

    TextView name, email, phone, companyTV, title;
    Button privateChat, saveContact, chatButton, settingsButton, homeButton;
    CheckBox blockUserCheckBox;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        final String cid = getIntent().getStringExtra("Cid");

        settingsButton = (Button) findViewById(R.id.settingsbutton);
        chatButton = (Button) findViewById(R.id.chatbutton);
        homeButton = (Button) findViewById(R.id.homebutton);
        privateChat = (Button) findViewById(R.id.privatechatbutton);
        saveContact = (Button) findViewById(R.id.contactbutton);
        blockUserCheckBox = (CheckBox) findViewById(R.id.blockcheckBox);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone);
        companyTV = (TextView) findViewById(R.id.company);
        title = (TextView) findViewById(R.id.title);

        String displayName = "", workNumber = "", emailID = "", company = "";
        final String jobTitle = "Kein Jobtitel";

        DatabaseHelper myDb = new DatabaseHelper(UserActivity.this);
        final SQLiteDatabase connection = myDb.getWritableDatabase();
        Cursor res = connection.rawQuery("Select * from users where cid='" + cid + "';", null);
        if (res.moveToNext()) {
            displayName = res.getString(1);
            workNumber = res.getString(2);
            emailID = res.getString(3);
            company = res.getString(4);
        } else
            displayName = "Unbekannt";


        res = connection.rawQuery("Select privatechatlist.blocked from privatechatlist where cid='" + cid + "';", null);
        if (res.moveToNext()) {
            blockUserCheckBox.setChecked(res.getString(0).equalsIgnoreCase("true"));
        } else
            blockUserCheckBox.setChecked(false);

        name.setText(displayName);
        email.setText(emailID);
        phone.setText(workNumber);
        companyTV.setText(company);
        title.setText("Profil");


        final String displayNameStr = displayName;
        final String workNumberStr = workNumber;
        final String emailIDStr = emailID;
        final String companyStr = company;

        privateChat.setOnClickListener(new View.OnClickListener() {
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


        blockUserCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (blockUserCheckBox.isChecked()) {
                    Log.e("checkbox user", "checked");
                    connection.execSQL("Update privatechatlist SET blocked=\"true\" where cid='" + cid + "';");
                } else {
                    Log.e("checkbox user", "unchecked");
                    connection.execSQL("Update privatechatlist SET blocked=\"false\" where cid='" + cid + "';");
                }
            }
        });


        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

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
                    Config.popupMessage("Kontakt erstellt", "Sie haben " + displayNameStr + " erfolgreich zu ihren Kontakten hinzugefügt.", UserActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", e.getMessage());
                    Config.popupMessage("Kontakt nicht erstellt", "Wir konnten " + displayNameStr + " nicht zu ihren Kontakten hinzugefügen. Bitte stellen Sie sicher das die App die nötigen Berechtigungen hat.", UserActivity.this);
                }
            }
        });


    }

}
