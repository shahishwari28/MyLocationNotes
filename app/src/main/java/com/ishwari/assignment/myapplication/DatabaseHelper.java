package com.ishwari.assignment.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ishwari.sqlite.helper.MarkerPlace;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_LOCATION = "LOCATION";
    public static final String TABLE_LIST = "LIST";
    public static final String TABLE_ITEMS = "ITEM";
    public static final String TABLE_MYNAME = "BOOK";

    // Table columns
    public static final String LOC_ID = "loc_id";
public static final  String LOC_NAME = "name";
    public static final String LOC_LAT = "latitude";
    public static final String LOC_LONG = "longitude";

    public static final String LIST_ID = "list_id";
    public static final  String LIST_NAME = "name";
    public static final String LIST_NOTE = "note";
    public static final String LIST_LOC_ID = "loc_id_fk";

    public static final String ITEM_ID = "item_id";
    public static final  String ITEM_NAME = "name";
    public static final String ITEM_QTY = "qty";
    public static final String ITEM_PLACE_ID = "place_id_fk";
    //public static final String ITEM_LIST_ID = "list_id_fk";


    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String _DESC = "description";


    // Database Information
    static final String DB_NAME = "MYLOCATION.DB";

    // database version
    static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_LOCATION = "create table " + TABLE_LOCATION + "(" + LOC_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LOC_NAME + " TEXT NOT NULL, " + LOC_LAT + " TEXT, " +LOC_LONG +" TEXT);";

    private static final String CREATE_LIST = "create table " + TABLE_LIST + " (" + LIST_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LIST_NAME + " TEXT NOT NULL, " + LIST_NOTE + " TEXT, "
            + LIST_LOC_ID + " INTEGER NOT NULL ,FOREIGN KEY ("+ LIST_LOC_ID +") REFERENCES "
            +TABLE_LOCATION+" ("+LOC_ID+"));";

    private static final String CREATE_ITEM = "create table " + TABLE_ITEMS + " (" + ITEM_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_NAME + " TEXT NOT NULL, " + ITEM_QTY + " TEXT, "
            + ITEM_PLACE_ID + " INTEGER NOT NULL ,FOREIGN KEY ("+ ITEM_PLACE_ID +") REFERENCES "
            +TABLE_LOCATION+" ("+LOC_ID+"));";

    private static final String CREATE_MYTABLE  = "create table "+ TABLE_MYNAME + "("+ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL, " + _DESC + " TEXT);";



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATION);
        db.execSQL(CREATE_MYTABLE);
        db.execSQL(CREATE_LIST);
        db.execSQL(CREATE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MYNAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }
}