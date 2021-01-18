package com.hatsumi.bluentry_declaration;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent2 = new Intent(context.getApplicationContext(),LoginPageActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(),1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.noti_logo)
                .setContentTitle("Reminder!")
                .setContentText("Hi there, please remember to log your temperature and daily declaration!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int)System.currentTimeMillis(),builder.build());
    }
}

