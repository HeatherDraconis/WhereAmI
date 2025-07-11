package com.example.whereami;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.location.Location;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import static android.graphics.Color.rgb;

public class LocationActivity extends AppCompatActivity {
    public static FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView, locationFullTextView;
    private ImageView mapImageView;
    static int figureSize, scale;
    private final HashSet<String> gridSquares = new HashSet<>();
    private final int side = 250;
    private final int boarder = 3;
    Bitmap bitmap;
    Canvas canvas;

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
        this.<Button>findViewById(R.id.Button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 4;
                scale = 1_000;
                showLocation();
            }
        });
        this.<Button>findViewById(R.id.Button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 6;
                scale = 100;
                showLocation();
            }
        });
        this.<Button>findViewById(R.id.Button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                figureSize = 8;
                scale = 10;
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
        locationFullTextView = findViewById(R.id.location_full);

        mapImageView = findViewById(R.id.map);

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, location -> {
            if (location != null) {
                getLocation(location);
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            getLocation(location);
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

    private void getLocation(Location location) {
        String gridRefLarge = String.format(OSGridRef.getGridRef(location.getLatitude(), location.getLongitude(), figureSize));
        locationTextView.setText(gridRefLarge);
        String gridRefSmall = String.format(OSGridRef.getGridRef(location.getLatitude(), location.getLongitude(), 10));
        locationFullTextView.setText(gridRefSmall);

        if (gridSquares.add(gridRefSmall)) {
            bitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);

            Paint circlePaint = new Paint();
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setAntiAlias(true);
            float radius = (float) side / (scale * 2) > 5 ? (float) side / (scale * 2) : 5;

            int minE = (Integer.parseInt(gridRefLarge.split(" ")[1])) * scale - (int) (boarder * 0.1 * scale);
            int maxE = (Integer.parseInt(gridRefLarge.split(" ")[1]) + 1) * scale + (int) (boarder * 0.1 * scale);;
            int minN = Integer.parseInt(gridRefLarge.split(" ")[2]) * scale - (int) (boarder * 0.1 * scale);;
            int maxN = (Integer.parseInt(gridRefLarge.split(" ")[2]) + 1) * scale + (int) (boarder * 0.1 * scale);;

            circlePaint.setColor(Color.GRAY);
            for (String gridSquare: gridSquares) {
                float e = (float) (Integer.parseInt(gridSquare.split(" ")[1]) - minE) * side / (maxE - minE);
                float n = side - (float) (Integer.parseInt(gridSquare.split(" ")[2]) - minN) * side / (maxN - minN);
                canvas.drawCircle(e, n, radius, circlePaint);
            }

            setMap();

            circlePaint.setColor(rgb(103,80,163));
            float e = (float) (Integer.parseInt(gridRefSmall.split(" ")[1]) - minE) * side / (maxE - minE);
            float n = side - (float) (Integer.parseInt(gridRefSmall.split(" ")[2]) - minN) * side / (maxN - minN);
            canvas.drawCircle(e, n, radius, circlePaint);

            mapImageView.setImageBitmap(bitmap);
        }
    }

    private void setMap() {
        Path path = new Path();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth((float) side / (scale * 2) > 5 ? (float) side / (scale * 2) : 5);
        float same = (float) (boarder * side / 16);
        path.moveTo(0, same);
        path.lineTo(side, same);
        path.moveTo(0, side - same);
        path.lineTo(250, side - same);
        path.moveTo(same, 0);
        path.lineTo(same, 250);
        path.moveTo(side - same, 0);
        path.lineTo(side - same, 250);
        canvas.drawPath(path, paint);
        mapImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, NotificationService.class));
    }
}
