package com.example.chris.konferenz_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    public static final String table1 = "CREATE TABLE userinformation (name varchar(255), phonenumber varchar(255), email varchar(255), company varchar(255), loginemail varchar(255), loginkey varchar(255), stayloggedin BOOLEAN, lastlogin varchar(255));";
    public static final String table2 = "create table events (event_id INTEGER, id varchar(255), title varchar(255), description varchar(255), author varchar(255), start varchar(255), end varchar(255), street varchar(255), zip varchar(6), city varchar(255), location varchar(255), url varchar(255));";
    public static final String table3 = "create table documents (id INTEGER, title varchar(255), event_id integer);";
    public static final String table4 = "create table interests (name varchar(255));";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table1);
        db.execSQL(table2);
        db.execSQL(table3);
        db.execSQL(table4);
        db.execSQL("INSERT INTO userinformation (name, phonenumber, email, company, loginemail, loginkey, stayloggedin, lastlogin) VALUES ('empty', 'empty', 'empty', 'empty', 'empty', 'empty', \"FALSE\", 'empty');");
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
        onCreate(db);
    }

    public void deleteAllUselessTablesLUL(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM events");
        db.execSQL("DELETE FROM documents");
    }

    public SQLiteDatabase getConnection() {
        return this.getWritableDatabase();
    }

}
