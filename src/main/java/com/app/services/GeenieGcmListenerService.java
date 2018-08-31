package com.app.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import com.app.activity.ChatActivity;
import com.app.geenie.R;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

public class GeenieGcmListenerService extends GcmListenerService{

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("CHATMESSAGE");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        if (data != null) {

                    if("USERLIST".equals(data.get("SM"))){
                        //update the userlist view
                        Intent userListIntent = new Intent("com.app.geenie.geeniechat.chatmessage");
                        String userList = data.get("USERLIST").toString();
                        userListIntent.putExtra("USERLIST",userList);
                        sendBroadcast(userListIntent);
                    } else if("CHAT".equals(data.get("SM"))){
                        Intent chatIntent = new Intent("com.app.geenie.geeniechat.chatmessage");
                        chatIntent.putExtra("CHATMESSAGE",data.get("CHATMESSAGE").toString());
                        chatIntent.putExtra("TOUSER",data.get("FROMUSER").toString());

                        sendBroadcast(chatIntent);
                    }
                }
            sendNotification(message, data.get("FROMUSER").toString());
    }

    private void sendNotification(String message, String from) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("type","notify");
        intent.putExtra("CHATMESSAGE",message);
        intent.putExtra("TOUSER", from);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.logo)
                .setContentText(message).setContentIntent(pendingIntent)
                .setSound(defaultSoundUri).setAutoCancel(true);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public boolean checkApp(){
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);

        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equalsIgnoreCase("com.app.activity.ChatActivity")) {
            return true;
        } else {
            return false;
        }
    }
}
