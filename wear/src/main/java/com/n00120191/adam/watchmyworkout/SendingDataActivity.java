package com.n00120191.adam.watchmyworkout;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

//This class contains the activity that the user will see when the confirmation for sending data
// will be displayed. It will then create a connection to the phone and send the data.
public class SendingDataActivity extends WearableActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //This tag will be used during debugging
    public static final String WEARABLE_DATA = "WearableData";
    //The activity string that contains the activity type
    private String activity;
    //The data that relates to the activity
    private int data;

    //This node object is what is used to store the node, or the device that is connected
    // to the watch
    private Node mNode;
    //The google Api client that connects to the node and sends the data through the connection
    // to the phone
    private GoogleApiClient mGoogleApiClient;
    //This is the path that the data will be sent over. The same wear path string will be used to
    // determine where the data came from and how it should be processed
    private static final String WEAR_PATH = "/from-wear";

    //This method will be run automatically when the activity is created. It creates the google api
    // client which will use the wearable api to send a message to the phone
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //This takes the extra data sent from the previous activity and saves them in order to send
        // them to the phone later
        Bundle extras = getIntent().getExtras();
        activity = extras.getString("activity");
        data = extras.getInt("data");
    }

    //the click handler for the button will determine which button was clicked and handle it
    // accordingly, either close the activity and delete the data, or run the function that sends
    // the data
    public void buttonClickHandler(View view){
        if(view.getId()== R.id.sendBtn){
            sendData(String.valueOf(data));
        }   else{
            closeActivity();
        }
    }

    //Activity is closed and the data is deleted
    private void closeActivity() {
        finish();
        Toast.makeText(getBaseContext(),"Data deleted", Toast.LENGTH_SHORT).show();
    }

    //Function to send the data to the phone. It gets takes a reference for the data to be
    // sent, then uses the wearables message api to send a message containing the data
    // to the the node that is currently connected(the phone). The message must be converted to a byte array in order to be sent
    // over the connection
    private void sendData(final String data) {
        if(mNode != null && mGoogleApiClient != null){
            Wearable.MessageApi.sendMessage(mGoogleApiClient,
                    mNode.getId(), WEAR_PATH, data.getBytes())
            .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    if (!sendMessageResult.getStatus().isSuccess()) {
                        Log.d(WEARABLE_DATA, "Failed message: " +
                                sendMessageResult.getStatus().getStatusMessage());
                        finish();
                        Toast.makeText(getBaseContext(),"Phone not connected", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(WEARABLE_DATA, "Message was successful");
                        finish();
                        Toast.makeText(getBaseContext(),"Data Sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //As there may be many different nodes in the area at one time that can be connected to, this
    // function will ensure that the node selected is the node that the watch is currently
    // connected to, and then save that reference to the node
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (Node node: nodes.getNodes()){
                            if(node != null && node.isNearby()){ //phone that is connected will always report as nearby
                                mNode=node;
                                Log.d(WEARABLE_DATA, "Connected to " + mNode.getDisplayName());
                            }
                        }
                        if(mNode == null){
                            Log.d(WEARABLE_DATA, "Could Not Connect");
                        }
                    }
                });
    }

    //Default methods
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
}
