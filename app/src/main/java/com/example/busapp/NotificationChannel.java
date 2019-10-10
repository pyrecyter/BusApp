package com.example.busapp;

import android.app.Application;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationChannel extends Application {
    public static final String CHANNEL_ID = "DBUpdateService";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            android.app.NotificationChannel notificationChannel = new android.app.NotificationChannel(CHANNEL_ID,"Bus DB update service",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
