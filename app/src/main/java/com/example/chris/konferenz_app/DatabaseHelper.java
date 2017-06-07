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
    private static final String table5 = "create table chatmessages (channel varchar(255) NOT NULL, timestamp varchar(255), cid varchar(255), content TEXT, issent BOOLEAN);";
    private static final String table6 = "create table users (cid varchar(255) NOT NULL, profile_name varchar(255), profile_phone varchar(255), profile_email varchar(255), profile_company varchar(255), PRIMARY KEY (cid));";
    private static final String table7 = "create table privatechatlist (cid varchar(255) NOT NULL, blocked BOOLEAN, PRIMARY KEY (cid));";


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
        db.execSQL(table7);
        db.execSQL("INSERT INTO userinformation (name, phonenumber, email, company, loginemail, loginkey, stayloggedin, lastlogin, sessionkey, sessioncid) VALUES ("+ null + ", "+ null + ", "+ null + ", "+ null + ", "+ null + ", "+ null + ", \"FALSE\", '1970-01-01',  "+ null + ",  "+ null + ");");
        db.execSQL("Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) values ('cid1234', 'Lorenz Vöhringer', '+49 123456789', 'lorenz@mail.de', 'Vöhringer GmbH & Co. KG');");
        db.execSQL("Insert into users (cid, profile_name, profile_phone, profile_email, profile_company) values ('cid1235', 'Alfredo Gross', '+49 987654321', 'alfredo.gross@sehrlange-emailadresse.de', 'Research and Development (R&D) / Product Development bei Alfredos Firma');");
        db.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('Hund', '08:39', 'cid1234', 'Testnachricht von Lorenz', \"FALSE\");");
        db.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('Hund', '18:59', 'cid1235', 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. \n" +
                "\n" +
                "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. ', \"FALSE\");");
        db.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('Hund', '08:39', 'cid1234', 'tl;dr?', \"FALSE\");");
        db.execSQL("INSERT INTO privatechatlist (cid, blocked) VALUES ('cid1234', \"FALSE\");");
        db.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('cid1234', '18:39', 'cid1234', 'Hey Chris, wie gehts dir heute so?', \"FALSE\");");
        db.execSQL("INSERT INTO chatmessages (channel, timestamp, cid, content, issent) VALUES ('cid1234', '18:40', 'cid1234', 'Gehen wir nachher ins McFit?', \"FALSE\");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS userinformation");
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS documents");
        db.execSQL("DROP TABLE IF EXISTS interests");
        db.execSQL("DROP TABLE IF EXISTS chatmessages");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS privatechatlist");
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
        return res.getString(9);
    }

    public SQLiteDatabase getConnection() {
        return this.getWritableDatabase();
    }

}
