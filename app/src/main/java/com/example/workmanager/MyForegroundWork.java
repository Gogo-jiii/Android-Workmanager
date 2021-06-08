package com.example.workmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyForegroundWork extends Worker {

    private NotificationManager notificationManager;
    private Context context;
    String progress = "Starting work...";
    int NOTIFICATION_ID = 1;
    static boolean isStopped = false;

    public MyForegroundWork(@NonNull Context context,
                            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
        isStopped = false;
    }

    @NonNull @Override public Result doWork() {
        setForegroundAsync(showNotification(progress));

        for (int i = 0; i < 100; i++) {

            if (isStopped) {
                break;
            }
            progress = "" + i;
            Log.d("TAG", progress);
            updateNotification(progress);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return Result.success();
    }

    @NonNull
    private ForegroundInfo showNotification(String progress) {
        return new ForegroundInfo(NOTIFICATION_ID, createNotification(progress));
    }

    private Notification createNotification(String progress) {
        String CHANNEL_ID = "100";
        String title = "Foreground Work";
        String cancel = "Cancel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, title,
                            NotificationManager.IMPORTANCE_HIGH));
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(progress)
                .setSmallIcon(R.drawable.ic_android)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();

        return notification;
    }

    private void updateNotification(String progress) {
        Notification notification = createNotification(progress);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
