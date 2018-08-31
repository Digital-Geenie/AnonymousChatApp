package com.app.activity;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.chat.MessageSender;
import com.app.geenie.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.List;


public class UserListActivity extends ListActivity {
    private static final String TAG = "UserListActivity";
    TextView content;
    Button refreshButton;
    private Intent intent;
    MessageSender messageSender;
    GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        content = (TextView)findViewById(R.id.output);
        content.setText("Select user to chat:");
        refreshButton = (Button)findViewById(R.id.refreshButton);
        /*intent = new Intent(this, GCMNotificationIntentService.class);*/

        // Registering to update UI on receiving user list
        registerReceiver(broadcastReceiver, new IntentFilter("com.app.geenie.geeniechat.chat.userlist"));

        messageSender = new MessageSender();
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // get user list
                Bundle dataBundle = new Bundle();
                dataBundle.putString("ACTION", "USERLIST");
                messageSender.sendMessage(dataBundle, gcm);
            }
        });

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getStringExtra("USERLIST"));
            updateUI(intent.getStringExtra("USERLIST"));
        }
    };

    private void updateUI(String userList) {
        //get userlist from the intents and update the list

        String[] userListArr = userList.split(":");

        Log.d(TAG,"userListArr: "+userListArr.length+" tostr "+userListArr.toString());

        //remove empty strings :-)
        List<String> list = new ArrayList<String>();
        for(String s : userListArr) {
            if(s != null && s.length() > 0) {
                list.add(s);
            }
        }
        userListArr = list.toArray(new String[list.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, userListArr);
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item index
        int itemPosition     = position;

        // ListView Clicked item value
        String  itemValue    = (String) l.getItemAtPosition(position);

        content.setText("User selected: " +itemValue);


        Intent i = new Intent(getApplicationContext(),
                ChatActivity.class);
        i.putExtra("TOUSER",itemValue);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
