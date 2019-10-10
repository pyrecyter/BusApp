package com.example.busapp;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.busapp.NotificationChannel.CHANNEL_ID;


public class dbUpdateService extends Service {
    private LocationCallback locationCallback;
    private FusedLocationProviderClient client;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (client != null) {
            client.removeLocationUpdates(locationCallback);
        }
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.child(id).child("live").setValue(false);
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bus Service")
                .setContentText("GPS service and DB update service")
                .setSmallIcon(R.drawable.ic_bus)
                .build();

        startForeground(1, notification);

        requestLocationUpdates();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (client != null) {
            client.removeLocationUpdates(locationCallback);
        }
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
        db.child(id).child("live").setValue(false);
    }

    private void requestLocationUpdates() {
        final LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setFastestInterval(2000);
        request.setSmallestDisplacement(5);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(id);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        db.child("latitude").setValue(location.getLatitude());
                        db.child("longitude").setValue(location.getLongitude());
                        db.child("live").setValue(true);
                    }else{
                        db.child("live").setValue(false);
                    }
                }
            };
            client.requestLocationUpdates(request,locationCallback, Looper.getMainLooper());
        }
    }

}
