package com.n00120191.adam.watchmyworkout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Line chart object which will be given a reference to the linechart view object
    private LineChart mChart;
    //Username object that will be given a reference to the user name text field
    private TextView username;
    //This is given a reference to the last updated text field object and will be used to
    //display the last date that the data was updated
    private TextView lastUpdated;

    private TextView avgMiles;
    private TextView avgReps;

    //This is the datasource object, it contains all the necessary functions to read, update,
    //create and delete items from the database.
    private UserActivityDataSource datasource;
    //This is a list that will contain all the objects that are retrieved from the database and
    //store them to be accessed later
    List<UserActivity> values;
    //this is an array that contains numbers 0 to 6 in reverse order. This is needed to ensure that
    //the data displayued on the linechart is in the proper order.
    private int[] order;
    Calendar c;

    //The on create function is called when the activity is created. it is the first function
    //that should be called, all others should be called from within it, and all views should be
    // created inside it.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //calls the super classes on create function. default android function
        super.onCreate(savedInstanceState);
        //sets the layout to the main activity layout.
        setContentView(R.layout.activity_main);
        //constructors for the various view objects
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        username = (TextView) findViewById(R.id.username);
        lastUpdated = (TextView) findViewById(R.id.lastUpdated);
        avgMiles = (TextView) findViewById(R.id.avgMilesData);
        avgReps = (TextView) findViewById(R.id.avgRepsData);

        c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);

        int repCount = 0;
        int milesCount = 0;
        double repTotal = 0;
        double mileTotal = 0;

        //initialisation for the classes fields
        username.setText("John Doe");
        lastUpdated.setText("Last updated, " + new StringBuilder()
                // Month is 0 based, add 1 to get accurate month
                .append(dd).append("-").append(mm + 1).append("-")
                .append(yy));
        order = new int[]{6, 5, 4, 3, 2, 1, 0};

        //creates a new data source object that will be used to retrieve all the items
        // in the database
        datasource = new UserActivityDataSource(this);

        //try and catch statement attempts to create a connection to the database. if an
        // error occurs the error is printed to the screen.
        try{
            datasource.open();
        } catch (SQLException e){
            e.printStackTrace();
        }

        //This retrieves all the activities from the database, stores them in an activity
        // object, then adds them to the values list. These objects and their attributes
        // can then be accessed and displayed
        values = datasource.getAllActivities();

        for(int i=0; i<values.size(); i++){
            if(values.get(i).getActivity().equals("rep")){
                repCount++;
                repTotal += values.get(i).getData();
            } else if(values.get(i).getActivity().equals("run")){
                milesCount++;
                mileTotal  += values.get(i).getData();
            }
            avgReps.setText(String.valueOf((int) (repTotal / repCount)));
            avgMiles.setText(String.valueOf((int) (mileTotal/milesCount)));
        }


        //The function called to create the line chart and display the data onto it.
        createDataChart();

    }

    private void createDataChart(){
        //initialises the line chart view object and stores a reference to it in the mChart
        //variable
        mChart = (LineChart) findViewById(R.id.dataVisChart);


        //create an entry to the database
//        datasource.createActivity("run", 12);
//        datasource.createActivity("rep", 11);
//        datasource.createActivity("run", 10);
//        datasource.createActivity("rep", 9);
//        datasource.createActivity("run", 8);
//        datasource.createActivity("rep", 7);
//        datasource.createActivity("rep", 6);
//        datasource.deleteActivity(6);
//        datasource.createActivity("rep", 14);
//        datasource.createActivity("rep", 20);
//        datasource.createActivity("rep", 22);
//        datasource.createActivity("rep", 2);

        //this function is mainly used to add the default data to the database. It adds 7 of
        // each type of data
        if(values.size() < 1){
            for(int count = 0; count <= 7; count++) {
                datasource.createActivity("run", 0);
                datasource.createActivity("rep", 0);
            }
        }

        //The entries arraylist is used to store the values of each point in the linechart. This
        // can be as many or as few points as needed
        ArrayList<Entry> entries = new ArrayList<>();
        Log.d("ENTERING", String.valueOf(values.get(1).getData()));
        //retrieve data from database

        //This algorithm is used to add data from the database to the line chart. It will add
        // 7 objects if possible, if not it will just add as many as possible. These objects
        // are added from the database in reverse order so that the newest ones are at the right
        // side of the line chart, as opposed to the default left side.
            for (int count = 0; count < 7; count++) {
                //if there are only 3 of the required 7 entries this funcition will trigger.
                if(count>values.size()){
                    entries.add(new Entry(0f,count));
                } else{ //if not this function will add the correct data.
                    entries.add(new Entry((float) values.get(values.size() - (count + 1)).getData(), order[count]));
                }
            }

        //This adds the entries array and the title of the chart to the dataset object
        LineDataSet dataset = new LineDataSet(entries, "user activity");

        //Draw lines curved instead of point to point
        //dataset.setDrawCubic(true);

        //an array of labels is created which will be displayed above the chart
        ArrayList<String> labels = new ArrayList<String>();

        //replace with date of activity(first and last date)
        labels.add("7");
        labels.add("6");
        labels.add("5");
        labels.add("4");
        labels.add("3");
        labels.add("2");
        labels.add("1");

        //formatting for the chart. First the data is applied to the chart from the data object
        //then the description is added as a label, Then each of the numbers on the right and left
        // side are disabled to make the chart look cleaner
        LineData data = new LineData(labels, dataset);
        mChart.setData(data);

        mChart.setDescription("Exercise Data");
        mChart.getLegend().setEnabled(false);

        mChart.getAxisLeft().setDrawLabels(false);
        mChart.getAxisRight().setDrawLabels(false);
        //mChart.getXAxis().setDrawLabels(false);
    }

    public void setSchedBtn(View v){
        //Toast.makeText(MainActivity.this, "DOOOOONE", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //when the system resumes from a sleep the database will again try to open
    @Override
    protected void onResume() {
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    //when the phone is put to sleep(exited) the database is vlosed
    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}
