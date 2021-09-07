package com.mobdeve.s11.group19.bon_inventaire;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class NotificationAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(Keys.KEY_TITLE.name());
        String body = intent.getStringExtra(Keys.KEY_MSG.name());
        String channel_id = intent.getStringExtra(Keys.KEY_CHANNEL_ID.name());
        PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra(Keys.KEY_REDIRECT_INTENT.name());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id);
        builder.setAutoCancel(true)
                .setSmallIcon(R.drawable.app_name_logo)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(new Random().nextInt(), builder.build());
    }
}
