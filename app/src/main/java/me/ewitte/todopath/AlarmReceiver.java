package me.ewitte.todopath;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by vicakatherine on 6/28/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

        MediaPlayer player;
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        Toast.makeText(arg0, "Alarm received!", Toast.LENGTH_LONG).show();
        player = MediaPlayer.create(arg0, R.raw.alarm1);
        player.start();

            Intent notificationIntent = new Intent(arg0, TodoActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(arg0);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(arg0);

            Notification notification = builder.setContentTitle("TodoPath")
                    .setContentText("Look your TODO!")
                 //   .setTicker("New Message Alert!")
                    .setSmallIcon(R.drawable.icontodo)
                    .setContentIntent(pendingIntent).build();

            NotificationManager notificationManager = (NotificationManager) arg0.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }


}
