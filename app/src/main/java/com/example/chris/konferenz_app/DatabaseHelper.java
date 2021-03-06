package com.example.chris.konferenz_app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.chris.konferenz_app.data.Event;

/**
 * Created by Chris on 01.06.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KonferenzAppDB";
    Context context;


    // Database creation sql statement
    private static final String table1 = "CREATE TABLE userinformation (name varchar(255), phonenumber varchar(255), email varchar(255), company varchar(255), loginemail varchar(255), loginkey varchar(255), stayloggedin BOOLEAN, lastlogin varchar(255), sessionkey varchar(255), sessioncid varchar(255), firstlogin BOOLEAN);";
    private static final String table2 = "create table events (event_id INTEGER, id varchar(255), title varchar(255), description varchar(255), author varchar(255), start varchar(255), end varchar(255), street varchar(255), zip varchar(6), city varchar(255), location varchar(255), url varchar(255), PRIMARY KEY (event_id));";
    private static final String table3 = "create table documents (id INTEGER, title varchar(255), event_id integer, PRIMARY KEY (id));";
    private static final String table4 = "create table interests (name varchar(255) NOT NULL, isvisible BOOLEAN DEFAULT false, PRIMARY KEY (name));";
    private static final String table5 = "create table chatmessages (channel varchar(255) NOT NULL, timestamp varchar(255), cid varchar(255), content TEXT, issent BOOLEAN);";
    private static final String table6 = "create table users (cid varchar(255) NOT NULL, profile_name varchar(255), profile_phone varchar(255), profile_email varchar(255), profile_company varchar(255), PRIMARY KEY (cid));";
    private static final String table7 = "create table privatechatlist (cid varchar(255) NOT NULL, blocked BOOLEAN, PRIMARY KEY (cid));";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, Config.appversion);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table1);
        db.execSQL(table2);
        db.execSQL(table3);
        db.execSQL(table4);
        db.execSQL(table5);
        db.execSQL(table6);
        db.execSQL(table7);
        db.execSQL("INSERT INTO userinformation (name, phonenumber, email, company, loginemail, loginkey, stayloggedin, lastlogin, sessionkey, sessioncid, firstlogin) VALUES (" + null + ", " + null + ", " + null + ", " + null + ", " + null + ", " + null + ", \"false\", '1970-01-01',  " + null + ",  " + null + ", \"true\");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Config.error_message(context,
                "Datenbank Version " + oldVersion + " ist veraltet. Wir installieren Ihnen Version "
                        + newVersion + ". Dabei werden alle Daten gelöscht.");
        deleteAllTables(db);
    }

    public void deleteAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS userinformation");
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS documents");
        db.execSQL("DROP TABLE IF EXISTS interests");
        db.execSQL("DROP TABLE IF EXISTS chatmessages");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS privatechatlist");
        onCreate(db);
    }


    public void insertInterest(SQLiteDatabase connection, String interest) throws SQLiteConstraintException {
        try {
            connection.execSQL("Insert into interests (name) VALUES ('" + interest + "');");
        } catch (SQLiteConstraintException e) {
            throw new SQLiteConstraintException();
        }
    }

    public String getToken(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from userinformation;", null);
        res.moveToFirst();
        return res.getString(8);
    }


    public String getCid(SQLiteDatabase connection) {
        Cursor res = connection.rawQuery("Select * from userinformation;", null);
        res.moveToFirst();
        return res.getString(9);
    }

    public SQLiteDatabase getConnection() {
        return this.getWritableDatabase();
    }

    public Event getData(SQLiteDatabase connection, int eventId) {
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
