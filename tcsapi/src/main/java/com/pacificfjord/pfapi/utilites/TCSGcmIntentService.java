package com.pacificfjord.pfapi.utilites;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pacificfjord.pfapi.R;
import com.pacificfjord.pfapi.TCSAppInstance;

/**
 * Created by tom on 11/03/14.
 */
public class TCSGcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String TAG = "Notification";
    private int icon = -1;

    //Handle Smart Demo display
    public static final String BROADCAST_MESSAGE = "com.sparkcompass.cast.message";
    public static final String MESSAGE_STRING = "message_string";
    private Intent broadcastMessageIntent;

    public TCSGcmIntentService() {
        super("TCSGcmIntentService");
        icon = TCSAppInstance.getInstance().getAppIcon();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(TAG, "Received: " + extras.toString());
                broadcastMessageIntent = new Intent(BROADCAST_MESSAGE);
                sendNotification(extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        TCSGcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    protected void sendNotification(Bundle messageInfo) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Class targetActivity = TCSAppInstance.getInstance().getTargetNotificationClass();
        Intent notificationIntent;

        if (targetActivity != null)
            notificationIntent = new Intent(this, targetActivity);
        else
            notificationIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());


        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle(TCSAppInstance.getInstance().getApplication().getString(R.string.app_name))
                        .setContentText(messageInfo.getString("message"))
                        .setWhen(System.currentTimeMillis())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageInfo.getString("message")))
                        .setAutoCancel(true);

        if (icon > 0) {
            mBuilder.setSmallIcon(icon);
        }

        if (messageInfo.getString("alertSound") != null && messageInfo.getString("alertSound").equals("true")) {
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }

        mBuilder.setContentIntent(contentIntent);

        int notificationId = TCSUtilities.generateRandomNumber();
        mNotificationManager.notify(notificationId, mBuilder.build());

        broadcastMessageIntent.putExtra(MESSAGE_STRING, messageInfo.getString("message"));
        sendBroadcast(broadcastMessageIntent);
    }
}
