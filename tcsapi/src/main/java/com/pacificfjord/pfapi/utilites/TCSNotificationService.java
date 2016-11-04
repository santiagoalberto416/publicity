package com.pacificfjord.pfapi.utilites;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pacificfjord.pfapi.TCSAppInstance;

import java.util.Random;

/**
 * Created by tom on 28/01/14.
 */
public class TCSNotificationService extends IntentService {

    private static final String DEBUG_TAG = "com.pacificfjord.pfapi.utilities.TCSNotificationService";
    public static final String KEYNOTIFICATIONTITLE = "NotificationTitle";
    public static final String KEYNOTIFICATIONMESSAGE = "NotificationMessage";
    public static final String KEYNOTIFICATIONACTIONUID = "actionUID";
    public static final String KEYNOTIFICATIONUID = "uid";
    public static final String KEYNOTIFICATIONACTIONICON = "NotificationIcon";

    NotificationManager notificationManager;

    public TCSNotificationService()
    {
        super(DEBUG_TAG);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = intent.getStringExtra(KEYNOTIFICATIONTITLE);
        String message = intent.getStringExtra(KEYNOTIFICATIONMESSAGE);
        String uid = intent.getStringExtra(KEYNOTIFICATIONACTIONUID);
        int iconId = intent.getIntExtra(KEYNOTIFICATIONACTIONICON, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setWhen(System.currentTimeMillis())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Class targetActivity = TCSAppInstance.getInstance().getTargetNotificationClass();
        Intent resultIntent = new Intent(getApplicationContext(), targetActivity);
        //Add the notification UID so we can identify it if it opens the app.
        resultIntent.putExtra(KEYNOTIFICATIONACTIONUID,uid);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        try{
            stackBuilder.addParentStack(targetActivity);
        }catch(Exception e)
        {
            Log.d("NOTIFICATION", e.getLocalizedMessage());
        }
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        int notificationId = TCSUtilities.generateRandomNumber();
        notificationManager.notify(notificationId, mBuilder.build());
    }
}
