package com.example.whereami;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class LocationActivity extends AppCompatActivity {
    private  String CHANNEL_ID = "where_am_i";
    private int NOTIFICATION_ID = 0;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView;
    private HashSet<String> gridSquares = new HashSet<String>();
    private Button button2, button4, button6, button8, button10;
    private int figureSize;

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        boolean fineLocationGranted = result.getOrDefault( Manifest.permission.ACCESS_FINE_LOCATION, false);
                        boolean coarseLocationGranted = result.getOrDefault( Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted || coarseLocationGranted) {
                            getFigureSize();
                        } else {
                            Toast.makeText(getApplicationContext(), "Access to the location is Required", Toast.LENGTH_LONG).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.figure_main);
        getLocationPermission();
    }

    private void getLocationPermission() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getFigureSize();
        } else {
            locationPermissionRequest.launch(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            });
        }
    }

    private void getFigureSize() {
        button2 = findViewById(R.id.Button2);
        button4 = findViewById(R.id.Button4);
        button6 = findViewById(R.id.Button6);
        button8 = findViewById(R.id.Button8);
        button10 = findViewById(R.id.Button10);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 2;
                showLocation();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 4;
                showLocation();
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 6;
                showLocation();
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 8;
                showLocation();
            }
        });
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 10;
                showLocation();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void showLocation() {
        setContentView(R.layout.location_main);
        locationTextView = findViewById(R.id.location);
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, location -> {
            if (location != null) {
                String gridRefInitial = OSGridRef.getGridRef(location.getLatitude(), location.getLongitude(), figureSize);
                locationTextView.setText(String.format(gridRefInitial));
                gridSquares.add(gridRefInitial);
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            String gridRef = OSGridRef.getGridRef(location.getLatitude(), location.getLongitude(), figureSize);
                            locationTextView.setText(String.format(gridRef));
                            if (gridSquares.add(gridRef)) {
                                createNotificationChannel(gridRef);
                            }
                        }
                    }
                };
                LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                        .setIntervalMillis(1000)
                        .setMinUpdateIntervalMillis(1000/2)
                        .setWaitForAccurateLocation(false)
                        .setMaxUpdateDelayMillis(100).build();
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        });
    }

    private void createNotificationChannel(String gridRef) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification notificationCompat = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("New Grid Square")
                    .setContentText(gridRef).build();
            notificationManager.notify(NOTIFICATION_ID, notificationCompat);
        }
    }

}