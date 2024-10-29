package com.example.whereami;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.Task;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

public class NotificationService extends Service {
    private final String CHANNEL_ID = "where_am_i";
    private final int NOTIFICATION_ID = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private final HashSet<String> gridSquares = new HashSet<>();
    static int figureSize;

    public static void setFigureSize(int fS) {
        figureSize = fS;
    }

    @Override
    public void onCreate() {
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        return START_STICKY;
    }

    private void startForeground() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            stopSelf();
            return;
        }
        try {
            Intent activity = new Intent(this, LocationActivity.class);
            activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent tapPendingIntent = PendingIntent.getActivity(this, 0, activity, PendingIntent.FLAG_IMMUTABLE);

            Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentIntent(tapPendingIntent)
                        .setContentTitle("Running in Background").build();
        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            type = FOREGROUND_SERVICE_TYPE_LOCATION;
        }
        ServiceCompat.startForeground(this, 100, notification, type);
        getLocation();
        } catch (Exception ignored) {
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);

        task.addOnSuccessListener(location -> {
            if (location != null) {
                String gridRefInitial = OSGridRef.getGridRef(location.getLatitude(), location.getLongitude(), figureSize);
                gridSquares.add(gridRefInitial);
                notificationManager.notify(NOTIFICATION_ID, getNotification(gridRefInitial));
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            String gridRef = OSGridRef.getGridRef(location.getLatitude(), location.getLongitude(), figureSize);
                            if (gridSquares.add(gridRef)) {
                                notificationManager.notify(NOTIFICATION_ID, getNotification(gridRef));
                            }
                        }
                    }
                };
                LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                        .setIntervalMillis(1000)
                        .setMinUpdateIntervalMillis(1000 / 2)
                        .setWaitForAccurateLocation(false)
                        .setMaxUpdateDelayMillis(100).build();
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        });
    }

    private Notification getNotification(String gridRef) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("New Grid Square")
                .setContentText(gridRef).build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
    }
}
