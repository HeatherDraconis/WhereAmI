package com.example.whereami;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import org.jetbrains.annotations.NotNull;

public class LocationActivity extends AppCompatActivity {
    public static FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView;
    private int figureSize;

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                        boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
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
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            });
        }
    }

    private void getFigureSize() {
        this.<Button>findViewById(R.id.Button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 2;
                NotificationService.setFigureSize(2);
                showLocation();
            }
        });
        this.<Button>findViewById(R.id.Button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 4;
                NotificationService.setFigureSize(4);
                showLocation();
            }
        });
        this.<Button>findViewById(R.id.Button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 6;
                NotificationService.setFigureSize(6);
                showLocation();
            }
        });
        this.<Button>findViewById(R.id.Button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 8;
                NotificationService.setFigureSize(8);
                showLocation();
            }
        });
        this.<Button>findViewById(R.id.Button10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 10;
                NotificationService.setFigureSize(10);
                showLocation();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void showLocation() {
        setContentView(R.layout.location_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, NotificationService.class));
        } else {
            startService(new Intent(this, NotificationService.class));
        }
        locationTextView = findViewById(R.id.location);
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, location -> {
            if (location != null) {
                locationTextView.setText(String.format(OSGridRef.getGridRef(location.getLatitude(), location.getLongitude(), figureSize)));
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            locationTextView.setText(String.format(OSGridRef.getGridRef(location.getLatitude(), location.getLongitude(), figureSize)));
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
        Context context = this;
        this.<Button>findViewById(R.id.Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, LocationActivity.class));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, NotificationService.class));
    }
}
