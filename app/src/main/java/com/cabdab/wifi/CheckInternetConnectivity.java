package com.cabdab.wifi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class CheckInternetConnectivity extends BroadcastReceiver{

    private final String TAG = CheckInternetConnectivity.class.toString();

    @Override
    public void onReceive(Context context, Intent arg1) {

        boolean isConnected = arg1.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(isConnected){
            Log.d(TAG, "Got internet connection");
        }
        else{
            Log.d(TAG, "Lost internet connection");
        }
    }

    private void showNotification(Context context) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(0)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }
}