package app.mangoofood.mangooapp.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import app.mangoofood.mangooapp.R;

public class NotificationHelper extends ContextWrapper {

    private static final String MANGOO_CHANNEL_ID = "app.mangoofood.mangooapp";
    private static final String MANGOO_CHANNEL_NAME = "Mangoo Food";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();

    }

    private void createChannel() {

        NotificationChannel mangooChannel = new NotificationChannel(MANGOO_CHANNEL_ID,
                MANGOO_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        mangooChannel.enableLights(false);
        mangooChannel.enableVibration(true);
        mangooChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(mangooChannel);

    }

    public NotificationManager getManager() {

        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;

    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getMangooChannelNotification(String title, String body,
                                                                         PendingIntent contentIntent, Uri soundUri)
    {

        return new android.app.Notification.Builder(getApplicationContext(),MANGOO_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);

    }

}
