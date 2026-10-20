package com.ahmed.leavephone;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    public static void schedule(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences("cfg", Context.MODE_PRIVATE);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, sp.getInt("h", 22));
        c.set(Calendar.MINUTE, sp.getInt("m", 0));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(ctx, 1,
                new Intent(ctx, AlarmReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent show = PendingIntent.getActivity(ctx, 2,
                new Intent(ctx, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE);
        am.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), show), pi);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        schedule(ctx);

        Intent li = new Intent(ctx, LockActivity.class);
        li.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(ctx, 3, li,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(
                "lock", "Leave phone", NotificationManager.IMPORTANCE_HIGH));

        Notification n = new Notification.Builder(ctx, "lock")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("سيب الموبايل")
                .setCategory(Notification.CATEGORY_ALARM)
                .setFullScreenIntent(pi, true)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        nm.notify(1, n);

        try {
            ctx.startActivity(li);
        } catch (Exception e) {
            // الإشعار هو الطريقة الاحتياطية
        }
    }
}
