package com.example.quarantine_monitor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Date;

public class NotificationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendNotification(String title, String body) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(), "notify_001");
        Bitmap licon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

//        mBuilder.setContentTitle(title);
        mBuilder.setContentText(body);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
//        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
//        mBuilder.setLargeIcon(licon);
        mBuilder.setTimeoutAfter(60*10*1000);
//        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel for QMonitor",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        // Generate unique id
        int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        manager.notify(id, mBuilder.build());
    }
}
