package com.n00120191.adam.watchmyworkout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

//This class is used to display the activity for changing the time that the app will
// send out a notification
public class SettingActivity extends AppCompatActivity {

    //this pending intent is used in creating an alarm manager object. It specifies the intent of
    // the service that will be used for the reminder
    private PendingIntent pendingIntent;

    //This data object will be used to format the time retrieved from the data picker into the
    // usual standard of hours:minutes
    private DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
    private TextView dateAndTimeLabel;
    //This calender object is used to get the current time which then sets the time picker
    // view object to the current time
    private Calendar dateAndTime=Calendar.getInstance();

    //This time picker object will be shown to the user when they click on the change time button.
    // This time picker shows an hour and minutes field that the user will change to change the
    // time of the reminder
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        //When the user finishes selecting the time and clicks Done, thic function will be run.
        // This saves the hours and minutes the user selected, then runs the update alarm function
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            updateAlarm();
        }
    };

    //This function will be run automatically when the activity is created
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_setting);

        //Creates a reference to the change time button
        Button btn = (Button)findViewById(R.id.timeBtn);

        //This function will run when that button is clicked and will display the time picker to
        // the user at the current time
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(SettingActivity.this,
                        t,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        true).show();
            }
        });

        dateAndTimeLabel=(TextView)findViewById(R.id.dateAndTime);
    }

    //This function is used to update the alarm when the user has changed the time.
    private void updateAlarm() {
        //creates an intent for the notify service class
        Intent myIntent = new Intent(this , NotifyService.class);
        //creates the manager for the alarm object
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //creates a pending intent that uses the previous intent object to open the notify service
        pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

        //1000ms * 60 minutes per hour * 60 seconds per hour * 24 hour per day = seconds per
        // day or, 1 day. The alarm manager will use permissions to wake the device if it is sleeping, a time to delay the repeating notification in milliseconds, and the pending intent object
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dateAndTime.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);

        Toast.makeText(SettingActivity.this, "Alarm Set", Toast.LENGTH_SHORT).show();
        dateAndTimeLabel.setText(fmtDateAndTime.format(dateAndTime.getTime()));
    }
}