package com.n00120191.adam.watchmyworkout;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//helper class to create the database and its tables, also handles upgrading the database version.
// When the version number changes the databse will automatically delete and the recreate its
// self, with its new changes
public class MySQLiteHelper extends SQLiteOpenHelper{

    //these constants save a reference to the names of the tables and columns in the database for
    // ease of use
    public static final String TABLE_ACTIVITIES = "activities";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ACTIVITY = "activity";
    public static final String COLUMN_DATA = "data";

//    public static final String TABLE_SCHEDULE = "schedule";
//    public static final String SCHEDULE_COLUMN_ID = "_id";
//    public static final String SCHEDULE_COLUMN_ACTIVITY = "activity";
//    public static final String SCHEDULE_COLUMN_TIME = "time";


    private static final String DATABASE_NAME = "activities.db";
    private static final int DATABASE_VERSION = 4;

    //sql statement to create database. This string is saved so it can be used to execute and
    // create the database
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ACTIVITIES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_ACTIVITY
            + " text not null, " + COLUMN_DATA
            + " integer not null);";

//    private static final String SCHEDULE_CREATE = "create table "
//            + TABLE_SCHEDULE + "(" + SCHEDULE_COLUMN_ID
//            + " integer primary key autoincrement, " + SCHEDULE_COLUMN_ACTIVITY
//            + " text not null, " + SCHEDULE_COLUMN_TIME
//            + " text not null);";

    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //this function will be called automatically when the database is not yet created or has just
    // been upgraded. The sql statement from above is used here
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        ////////////////////////
//        db.execSQL(SCHEDULE_CREATE);
        /////////////////////////
    }

    //the upgrade method which deletes then creates a new database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading dabast from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        /////////////////////////
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        //////////////////////////////////////
        onCreate(db);
    }
}
