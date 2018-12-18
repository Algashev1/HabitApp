package com.example.algashev.habitapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.PowerManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "habitapp: locktag");
        wl.acquire();

        final NotificationUtils mNotificationUtils = new NotificationUtils(context);
        Notification.Builder nb = mNotificationUtils.
                getAndroidChannelNotification(intent.getStringExtra("name"), intent.getStringExtra("question"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationUtils.getManager().notify(101, nb.build());
        }

        wl.release();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        setAlarm(context,
                intent.getStringExtra("id"),
                intent.getStringExtra("name"),
                intent.getStringExtra("question"),
                intent.getStringExtra("time"),
                calendar);
    }

    public void setAlarm(Context context, String id, String name, String question, String time, Calendar calendar) {
        try {
            String[] list = time.split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(list[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(list[1]));


            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context, DBHelper.TABLE_NAME, null, 2);
            try {
                db = dbHelper.getWritableDatabase();

                Cursor cursor = db.query(DBHelper.TABLE_NAME, null, DBHelper.ID + "=" + id,
                        null, null, null, null, null);

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    cursor.getString(1);
                    String[] days = cursor.getString(1).split(",");
                    for (String day : days) {
                        int d = calendar.get(Calendar.DAY_OF_WEEK);
                        if (Integer.parseInt(day) == d) {
                            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(context, BootReceiver.class);
                            intent.putExtra("id", id);
                            intent.putExtra("name", name);
                            intent.putExtra("question", question);
                            intent.putExtra("time", time);
                            PendingIntent pi = PendingIntent.getBroadcast(context, Integer.parseInt(id), intent, 0);
                            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                            break;
                        }

                    }


                }
            } catch (SQLiteException ex) {
                db = dbHelper.getReadableDatabase();
            }


        } catch (Exception e) {
            int a = 0;

        }
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, BootReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BootReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }
}
