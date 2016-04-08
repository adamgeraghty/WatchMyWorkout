package com.n00120191.adam.watchmyworkout;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//This is the service that allows notifications to be displayed even when the app is not running.
// The services registers the alarm which refreshes in the background of the system
public class NotifyService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){

        // the notification manager object that will register the system service
        NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        //This creates the notification object and gives it all its attributes. It sets the
        // Title of the notification, and the body text and description that will be displayed
        // alongside it, and the icon that will be displayed on it
        Notification mNotify = new Notification.Builder(this)
                .setContentTitle("Watch My Workout")
                .setContentText("You have an activity planed today!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();
        //this then sends the notification to the service
        mNM.notify(1, mNotify);
    }
}