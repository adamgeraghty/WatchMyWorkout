package com.n00120191.adam.watchmyworkout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//database access object. This class is used to access the database and retrieve the objects within
// it. when the database is queried for data, the data retrieved is saved in a UserActivity object,
// which is then saved in an array.
public class UserActivityDataSource {

    //creates a database object that can later be referenced
    private SQLiteDatabase database;
    //creates a helper class that contains the sql statements for each query
    private MySQLiteHelper dbHelper;
    //Creates a string array that contains the names of each column in the database
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ACTIVITY, MySQLiteHelper.COLUMN_DATA};
    private String selection = "Select * from " + MySQLiteHelper.TABLE_ACTIVITIES + " ASC limit 7;";

    //constructor for this class, also creates the sql helper class with the sql queries
    public UserActivityDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    //this opens and retrieves the database so that the information within it can be accessed
    public void open()throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    //Closes the connection to the database
    public void close(){
        dbHelper.close();
    }

    //this function creates and adds a user activity object to the database.
    public UserActivity createActivity(String activity, int data){
        //this content values object is used when inserting into a column of data for the sql database
        ContentValues values = new ContentValues();
        //the values that will be put into its respective column
        values.put(MySQLiteHelper.COLUMN_ACTIVITY, activity);
        values.put(MySQLiteHelper.COLUMN_DATA, data);
        //creates an id for the new object to use in the databse
        long insertId = database.insert(MySQLiteHelper.TABLE_ACTIVITIES, null, values);
        //the cursor object is a reference to a row in the database. in this case it is a row
        // that will be created
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ACTIVITIES, allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        //this will get the cursor objects data and set it to that of the user activity objects data
        UserActivity newActivity = cursorToActivity(cursor);
        //close the cursor as it is no longer needed
        cursor.close();
        //the new activity is then returned
        return newActivity;
    }

    public void deleteActivity(UserActivity activity){
        long id = activity.getId();
        Log.d("DELETED", "Activity deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ACTIVITIES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);

    }
    public void deleteActivity(int id){

        Log.d("DELETED", "Activity deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ACTIVITIES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    //this function will loop through and return all activities from the database in an
    // array of user activity objects
    public List<UserActivity> getAllActivities(){
        List<UserActivity> activities = new ArrayList<UserActivity>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ACTIVITIES, allColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            UserActivity uActivity = cursorToActivity(cursor);
            activities.add(uActivity);
            cursor.moveToNext();
        }

        cursor.close();
        return activities;
    }

    //uses the cursor object and the data related to the row it is pointing to in order to save
    // each one to an activity object
    private UserActivity cursorToActivity(Cursor cursor){
        UserActivity activity = new UserActivity();
        activity.setId(cursor.getLong(0));
        activity.setActivity(cursor.getString(1));
        activity.setData(cursor.getInt(2));
        return activity;
    }
}
