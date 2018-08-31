package com.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.app.geenie.R;

public class SingleItemViewActivity extends Activity {

    private TextView user_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.activity_single_item_view);

        // Retrieve data from MainActivity on item click event
        Intent i = getIntent();

        // Get the name
        String userID = i.getStringExtra("USER_ID");
        String createDate = i.getStringExtra("CREATE_DTTM");
        String locationLatitude = i.getStringExtra("LOCATION_LATITUDE");
        String locationLongitude = i.getStringExtra("LOCATION_LONGITUDE");

        // Locate the TextView in singleitemview.xml
        user_ID = (TextView) findViewById(R.id.userId);
        TextView create_date = (TextView) findViewById(R.id.createDate);
        TextView location_latitude = (TextView) findViewById(R.id.locationLatitude);
        TextView location_longitude = (TextView) findViewById(R.id.locationLongitude);

        // Load the text into the TextView
        user_ID.setText(userID);
        create_date.setText(createDate);
        location_latitude.setText(locationLatitude);
        location_longitude.setText(locationLongitude);

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
    @Override
    public void onStop(){
        super.onStop();
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(SingleItemViewActivity.this, UserActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}