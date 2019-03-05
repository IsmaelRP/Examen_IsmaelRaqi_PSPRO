package com.practica.ismael.examen_ismaelraqi_pspro.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import com.practica.ismael.examen_ismaelraqi_pspro.R;
import com.practica.ismael.examen_ismaelraqi_pspro.ui.activity.MainActivity;
import com.practica.ismael.examen_ismaelraqi_pspro.utils.Constants;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyIntentService extends JobIntentService {

    private static final int JOB_ID = 7;
    private static final int NOTIFICATION_ID = 9001;
    private static String city;
    private static String descr;

    public static void start(Context context, String city, String descr) {
        MyIntentService.city = city;
        MyIntentService.descr = descr;
        Intent intent = new Intent(context, MyIntentService.class);
        enqueueWork(context, MyIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notif = new NotificationCompat.Builder(this, "10");
        notif.setSmallIcon(R.drawable.ic_wb_sunny_black_24dp)
                .setContentTitle("Weather")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setContentText(city + ", " + descr)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notif.build());


    }

}
