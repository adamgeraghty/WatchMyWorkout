package com.n00120191.adam.watchmyworkout;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;

//This activity is used  to display a scrolable list on the main activity of the app. It takes an
// array of Strings and an array of icons, then loops through each array and adds it to the list
public class AdvancedListActivity extends Activity implements WearableListView.ClickListener {

    //Reference to the list view XML object
    private WearableListView mListView;
    //This adapter class is used to adapt the array into the list view
    private MyListAdapter mAdapter;

    private float mDefaultCircleRadius;
    private float mSelectedCircleRadius;

    //The on create method run automatically when the activity is created. It initialises the list
    // adapter, creates the view object that the list layout will be created (or inflated) into,
    // then sets the click listener for each of these objects.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        mDefaultCircleRadius = getResources().getDimension(R.dimen.default_settings_circle_radius);
        mSelectedCircleRadius = getResources().getDimension(R.dimen.selected_settings_circle_radius);
        mAdapter = new MyListAdapter();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
                mListView.setAdapter(mAdapter);
                mListView.setClickListener(AdvancedListActivity.this);
            }
        });
    }

    //The array or icons that will be displayed alongside the name
    private static ArrayList<Integer> listItems;

    static {
        listItems = new ArrayList<Integer>();
        listItems.add(R.drawable.ic_run);
        listItems.add(R.drawable.ic_rep);
    }

    //The array of list object names that will be displayed
    private static String[] listNames;

    static {
        listNames = new String[]{
                "Track Run",
                "Track Reps"
//                "Goals",
//                "Sync Data",
//                "Settings"
        };
    }

    //The adapter class will combine the list titles with the list icons and display them on the screen
    public class MyListAdapter extends WearableListView.Adapter {

        //creates the list holder pbject that contains the list
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new MyItemView(AdvancedListActivity.this));
        }

        //This handles relating and assigning the two objects from both arrays
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            MyItemView itemView = (MyItemView) viewHolder.itemView;

            TextView txtView = (TextView) itemView.findViewById(R.id.text);
            txtView.setText(listNames[i]);

            Integer resourceId = listItems.get(i);
            CircledImageView imgView = (CircledImageView) itemView.findViewById(R.id.image);
            imgView.setImageResource(resourceId);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

    }

    //This is the view that will contain each list item
    private final class MyItemView extends FrameLayout implements WearableListView.OnCenterProximityListener {

        final CircledImageView imgView;
        final TextView txtView;
        private float mScale;
        private final int mFadedCircleColor;
        private final int mChosenCircleColor;

        public MyItemView(Context context) {
            super(context);
            View.inflate(context, R.layout.row_advanced_item_layout, this);
            imgView = (CircledImageView) findViewById(R.id.image);
            txtView = (TextView) findViewById(R.id.text);
            mFadedCircleColor = ContextCompat.getColor(context, R.color.dark_grey);//getResources().getColor(android.R.color.darker_gray);
            mChosenCircleColor = ContextCompat.getColor(context, R.color.dark_blue);//getResources().getColor(android.R.color.holo_blue_dark);
        }

        @Override
        public void onCenterPosition(boolean b) {

        }

        @Override
        public void onNonCenterPosition(boolean b) {

        }

    }

    //This function handles the on click event for each item. A switch statement is used which gets
    // the position of the list item in the array and uses that to initialise an intent object with
    // the class that the user wishes to go to, then that activity is started
    public void onClick(WearableListView.ViewHolder viewHolder) {
        //Toast.makeText(this, String.format("You selected item #%s", viewHolder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
        int p = viewHolder.getAdapterPosition();
        Intent intent;

        switch(p){
            case 0: intent = new Intent(this, TrackRunActivity.class); break;
            case 1: intent = new Intent(this, TrackRepActivity.class); break;
            default: intent = new Intent(this, MainActivity.class); return;
        }

        startActivity(intent);
    }

    //default function
    @Override
    public void onTopEmptyRegionClick() {
    }
}