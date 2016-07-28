package me.ewitte.todopath;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import me.ewitte.todopath.model.Todo;

/**
 * Created by vicakatherine on 6/28/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

        MediaPlayer player;
        @Override
        public void onReceive(Context arg0, Intent intent) {
            Bundle extras = intent.getExtras();
            Todo todo = extras.getParcelable(Todo.TAG);

            Intent notificationIntent = new Intent(arg0, TodoActivity.class);
            notificationIntent.putExtra(TodoActivity.EXTRA_LIST_ID, todo.getList_id());

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(arg0);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(arg0);

            Notification notification = builder.setContentTitle("TodoPath - Pending Todo")
                    .setContentText(todo.getName())
                 //   .setTicker("New Message Alert!")
                    .setSmallIcon(R.drawable.icontodo)
                    .setContentIntent(pendingIntent).build();

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(arg0);
            Boolean notPref = sharedPref.getBoolean("pref_notifications", true);
            Boolean hpNot = sharedPref.getBoolean("pref_notification_priority", false);

            if (notPref && (!hpNot || (hpNot && todo.getPriority()==Todo.PRIORITY_HIGH))) {
                NotificationManager notificationManager = (NotificationManager) arg0.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notification);
            }
        }


}
