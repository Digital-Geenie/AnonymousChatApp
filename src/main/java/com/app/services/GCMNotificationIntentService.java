package com.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.app.activity.ChatActivity;
import com.app.activity.SignUpActivity;
import com.app.geenie.R;
import com.app.receivers.GcmBroadcastReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCMNotificationIntentService";

    @Override
    protected void onHandleIntent(Intent intent) {
      //  Log.d(TAG, "onHandleIntent "+intent.getDataString());
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (extras != null) {
            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                        .equals(messageType)) {
                    sendNotification("Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                        .equals(messageType)) {
                    sendNotification("Deleted messages on server: "
                            + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                        .equals(messageType)) {

                    if("USERLIST".equals(extras.get("SM"))){
                        //Log.d(TAG, "onHandleIntent - USERLIST ");
                        //update the userlist view
                        Intent userListIntent = new Intent("com.app.geenie.geeniechat.chatmessage");
                        String userList = extras.get("USERLIST").toString();
                        userListIntent.putExtra("USERLIST",userList);
                        sendBroadcast(userListIntent);
                    } else if("CHAT".equals(extras.get("SM"))){
                       // Log.d(TAG, "onHandleIntent - CHAT ");
                        Intent chatIntent = new Intent("com.app.geenie.geeniechat.chatmessage");
                        chatIntent.putExtra("CHATMESSAGE",extras.get("CHATMESSAGE").toString());
                        sendBroadcast(chatIntent);
                    }
                   // Log.i(TAG, "SERVER_MESSAGE: " + extras.toString());

                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        //Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ChatActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.gcm_cloud)
                .setContentTitle("GCM XMPP Message")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        //Log.d(TAG, "Notification sent successfully.");
    }
}

