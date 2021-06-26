package com.example.dell.smartchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String notificationTitle=remoteMessage.getNotification().getTitle();
        String notificationMessage=remoteMessage.getNotification().getBody();
        String   click_action=remoteMessage.getNotification().getClickAction();
        String  from_user_id=remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,SmartChat.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_launcher))
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent resultIntent=new Intent(click_action);
        resultIntent.putExtra("user_id",from_user_id);

        PendingIntent resultPendingIntent=PendingIntent.getActivity(this,
                0,
                resultIntent
        ,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);




        int mNotificationId=001;

        NotificationManager mNotifymgr=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Builds the notification and issues it
        mNotifymgr.notify(mNotificationId,mBuilder.build());

    }
}
