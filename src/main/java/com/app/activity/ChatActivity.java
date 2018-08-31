package com.app.activity;

import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.chat.ChatArrayAdapter;
import com.app.chat.ChatMessage;
import com.app.chat.MessageSender;
import com.app.constants.GeenieConstants;
import com.app.geenie.R;
import com.app.services.RegistrationIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class ChatActivity extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
	private ListView listView;
	private EditText chatText;
    private Button buttonSend;
    private int counter;

    GoogleCloudMessaging gcm;

	private static Random random;
    Intent i, intent;
    private String toUserName, type;
    MessageSender messageSender;

    SharedPreferences sharedPreferences = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        i = getIntent();
        type = i.getStringExtra("type");
        toUserName = i.getStringExtra("TOUSER");
		setContentView(R.layout.activity_chat);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        buttonSend = (Button) findViewById(R.id.buttonSend);

       /* intent = new Intent(this, GCMNotificationIntentService.class);*/


            intent = new Intent(this, RegistrationIntentService.class);

        // On arrival on new message add it to chat array adapter

            registerReceiver(broadcastReceiver, new IntentFilter("com.app.geenie.geeniechat.chatmessage"),null, null);


		random = new Random();
        messageSender = new MessageSender();
		listView = (ListView) findViewById(R.id.listView1);
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

		chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);

		listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);
        chatText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(isNetworkAvailable()){
                    sendChatMessage();
                }
                else {
                    Toast.makeText(ChatActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(type.equals("notify")) {
            chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
            chatArrayAdapter.add(new ChatMessage(true, i.getStringExtra("CHATMESSAGE")));
            listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            listView.setAdapter(chatArrayAdapter);
            counter++;

            chatText.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        return sendChatMessage();
                    }
                    return false;
                }
            });
        }

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
	}

        @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    private boolean sendChatMessage(){
        //sending gcm message to the paired device
        String fromUserName = sharedPreferences.getString("userName", "default");
        Bundle dataBundle = new Bundle();
        dataBundle.putString("ACTION", "CHAT");
        dataBundle.putString("TOUSER", toUserName); // currently this is publisher's user name
        dataBundle.putString("FROMUSER", fromUserName); // currently this is subscriber's user name
        dataBundle.putString("CHATMESSAGE", chatText.getText().toString());
        dataBundle.putInt("COUNTER", counter);
        messageSender.sendMessage(dataBundle,gcm);

        //updating the current device
        chatArrayAdapter.add(new ChatMessage(false, chatText.getText().toString()));
        chatText.setText("");
        return true;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getStringExtra("CHATMESSAGE"));
            chatArrayAdapter.add(new ChatMessage(true, intent.getStringExtra("CHATMESSAGE")));
        }
    };

    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifi.isConnected() || mobile.isConnected());
    }
}