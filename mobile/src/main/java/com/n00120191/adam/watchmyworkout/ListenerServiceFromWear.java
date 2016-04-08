package com.n00120191.adam.watchmyworkout;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

//This class is used to detect and manage data sent from the connected watch. The data can be
// sent at any time, even if the app is not running on the phone. This service must be registered
// in the manifest in order to recieve events from the google play services.
public class ListenerServiceFromWear extends WearableListenerService{

    //this path is the same constant that was declared on the watch and is used to determine
    // the location the data was sent from and the path it should take.
    private static final String WEAR_PATH = "/from-wear";
    //Here another datasource connection to the database is created so that this class can update
    // the database easily
    private UserActivityDataSource datasource;

    //This default function is called when the phone receives data from the watch. The message
    // event object contains an id, a path, and the data that has been sent in a byte array
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d("WEARABLE", "Is working");
        //activity = new UserActivity();
        //initialising the database object
        datasource = new UserActivityDataSource(this);

        //attempt to open a connection to the database
        try{
            datasource.open();
        } catch (SQLException e){
            e.printStackTrace();
        }

        //checks to see if the path that the data was sent to is the data we want to use
        if(messageEvent.getPath().equals(WEAR_PATH)){
            //this creates a new string object from the data in the message event. this data is in
            // a byte array but the String constructor converts it from bytes to a usable string
            String data = new String(messageEvent.getData());
            Log.d("WEARABLE", data);
            int num = 0;
            //This creates an int from the data retrieved from the watch, Then adds the object
            // to the database
            try{
                num = Integer.parseInt(data);
                datasource.createActivity("rep", num);
                Log.d("WEARABLE", String.valueOf(num));
            }   catch (NumberFormatException nfe){

            }
            datasource.close();
        }
    }

//    public static byte[] serialize(Object obj) throws IOException {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        ObjectOutputStream os = new ObjectOutputStream(out);
//        os.writeObject(obj);
//        return out.toByteArray();
//    }
//    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
//        ByteArrayInputStream in = new ByteArrayInputStream(data);
//        ObjectInputStream is = new ObjectInputStream(in);
//        return is.readObject();
//    }
}
