package com.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.app.activity.UserActivity;
import com.app.chat.MessageSender;
import com.app.constants.QuickstartPreferences;
import com.app.geenie.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationIntentService extends IntentService{

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private static String userName;

    MessageSender messageSender = new MessageSender();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)  {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userName = intent.getStringExtra("userName");
    try {
        // [START register_for_gcm]
        // Initially this call goes out to the network to retrieve the token, subsequent calls
        // are local.
        // [START get_token]
        InstanceID instanceID = InstanceID.getInstance(this);
        // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
        // See https://developers.google.com/cloud-messaging/android/start for details on this file.
        String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        // [END get_token]
        Log.i(TAG, "GCM Registration Token: " + token);

            sharedPreferences.edit().putBoolean(QuickstartPreferences.DEVICE_REGISTERED, true).apply();
            sharedPreferences.edit().putString("userName", userName).apply();

        sendRegistrationToServer(token);

        // Subscribe to topic channels
        subscribeTopics(token);


        // You should store a boolean that indicates whether the generated token has been
        // sent to your server. If the boolean is false, send the token to your server,
        // otherwise your server should have already received the token.
        /*sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();*/
        sharedPreferences.edit().putBoolean(QuickstartPreferences.DEVICE_REGISTERED, true).apply();
        Intent registeredIntent  = new Intent(RegistrationIntentService.this,UserActivity.class);
        registeredIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registeredIntent);
        /*Intent i = new Intent(RegistrationIntentService.this, UserActivity.class);
        startActivity(i);*/
        // [END register_for_gcm]
    } catch (Exception e) {
        Log.d(TAG, "Failed to complete token refresh", e);
        // If an exception happens while fetching the new token or updating our registration data
        // on a third-party server, this ensures that we'll attempt the update at a later time.
        /*sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();*/
        sharedPreferences.edit().putBoolean(QuickstartPreferences.DEVICE_REGISTERED, false).apply();
    }


    /*// Notify UI that registration has completed, so the progress indicator can be hidden.
    Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);*/
}

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        Bundle dataBundle = new Bundle();
        dataBundle.putString("ACTION", "SIGNUP");
        dataBundle.putString("USER_NAME", userName);
        dataBundle.putString("TOKEN",token);
        messageSender.sendMessage(dataBundle,gcm);

    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
//        GcmPubSub pubSub = GcmPubSub.getInstance(this);
//        for (String topic : TOPICS) {
//            pubSub.subscribe(token, "/topics/" + topic, null);
//        }
    }
    // [END subscribe_topics]

}
