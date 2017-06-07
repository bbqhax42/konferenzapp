package com.example.chris.konferenz_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Chris on 01.06.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KonferenzAppDB";

    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String table1 = "CREATE TABLE userinformation (name varchar(255), phonenumber varchar(255), email varchar(255), company varchar(255), loginemail varchar(255), loginkey varchar(255), stayloggedin BOOLEAN, lastlogin varchar(255), sessionkey varchar(255), sessioncid varchar(255));";
    private static final String table2 = "create table events (event_id INTEGER, id varchar(255), title varchar(255), description varchar(255), author varchar(255), start varchar(255), end varchar(255), street varchar(255), zip varchar(6), city varchar(255), location varchar(255), url varchar(255), PRIMARY KEY (event_id));";
    private static final String table3 = "create table documents (id INTEGER, title varchar(255), event_id integer, PRIMARY KEY (id));";
    private static final String table4 = "create table interests (name varchar(255) NOT NULL, PRIMARY KEY (name));";
    private static final String table5 = "create table chatmessages (channel varchar(255) NOT NULL, timestamp varchar(255), cid varchar(255), content varchar(255), issent BOOLEAN);";
    private static final String table6 = "create table users (cid varchar(255) NOT NULL, profile_name varchar(255), profile_phone varchar(255), profile_email varchar(255), profile_company varchar(255), PRIMARY KEY (cid));";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table1);
        db.execSQL(table2);
        db.execSQL(table3);
        db.execSQL(table4);
        db.execSQL(table5);
        db.execSQL(table6);
        db.execSQL("INSERT INTO userinformation (name, phonenumber, email, company, loginemail, loginkey, stayloggedin, lastlogin, sessionkey, sessioncid) VALUES ("+ null + ", "+ null + ", "+ null + ", "+ null + ", "+ null + ", "+ null + ", \"FALSE\", '1970-01-01',  "+ null + ",  "+ null + ");");
        db.execSQL("Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) values ('1234', 'Lorenz', '0123445678', 'lorenz@mail.de', 'KoksAG');");
        db.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('Testchannel', '08:39', '1234', 'Testnachricht von Lorenzo', \"FALSE\");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS table1");
        db.execSQL("DROP TABLE IF EXISTS table2");
        db.execSQL("DROP TABLE IF EXISTS table3");
        db.execSQL("DROP TABLE IF EXISTS table4");
        db.execSQL("DROP TABLE IF EXISTS table5");
        onCreate(db);
    }

    public void deleteAllUselessTablesLUL() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM events");
        db.execSQL("DELETE FROM documents");
        //db.execSQL("DELETE FROM users");
    }

    public void insertInterest (SQLiteDatabase connection, String interest) throws SQLiteConstraintException {
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
        Log.e("DBHELPER", res.getString(9));
        return res.getString(9);
    }

    public SQLiteDatabase getConnection() {
        return this.getWritableDatabase();
    }

}
