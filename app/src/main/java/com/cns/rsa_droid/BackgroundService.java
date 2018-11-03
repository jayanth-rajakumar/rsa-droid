package com.cns.rsa_droid;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//https://stackoverflow.com/questions/9177212/creating-background-service-in-android


public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    public static Boolean callstatus=false;
    public DatabaseReference mDatabase;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
      //  Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();


        handler = new Handler();
        runnable = new Runnable() {
            public void run() {


                //String test_str=MainActivity.db.child("Test").getValue().toString();
                if((!MainActivity.latest_message.startsWith("You: ")) && MainActivity.latest_message!=MainActivity.prev_message)
                {
                      show_notif(MainActivity.latest_message,false);
                    MainActivity.prev_message=MainActivity.latest_message;


                }
                if(MainActivity.latest_message.startsWith("You: "))
                {
                    show_notif(MainActivity.latest_message,true);
                }



                handler.postDelayed(runnable, 5000);
            }
        };


        handler.postDelayed(runnable, 50);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
       // Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
       // Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }

    public void show_notif(String message,Boolean option) {
        if (option == false) {

            if (callstatus == true) {

                String not_username = MainActivity.latest_message.substring(0, MainActivity.latest_message.indexOf(':'));
                String not_message = MainActivity.latest_message.substring(MainActivity.latest_message.indexOf(':') + 2);

                Intent intent = new Intent(this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
               // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_name2)
                        .setContentTitle(not_username)
                        .setContentText(not_message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(not_message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(12345, mBuilder.build());

                PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isScreenOn();
                if(isScreenOn==false)
                {
                   PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"unikie:");
                    wl.acquire(10000);
                   // PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"unikie:");

                    //wl_cpu.acquire(10000);
                }

            } else
                callstatus = true;
        }
        else
        {
            if(callstatus==false)
                callstatus=true;
        }


    }



}