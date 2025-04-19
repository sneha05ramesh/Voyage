package com.example.voyage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.adapters.SafetyAlertAdapter;
import com.example.voyage.models.SafetyAlert;
import com.example.voyage.network.AlertService;
import com.example.voyage.response.AlertResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SafetyAlertsActivity extends AppCompatActivity {

    private static final String TAG = "SafetyAlertsActivity";
    private static final int LOCATION_PERMISSION_CODE = 1001;

    private RecyclerView recyclerView;
    private TextView regionLabel, noAlertsMessage;
    private SafetyAlertAdapter adapter;
    private final List<SafetyAlert> alertList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_alerts);
        Log.d(TAG, "onCreate: Activity started");

        regionLabel = findViewById(R.id.regionLabel);
        noAlertsMessage = findViewById(R.id.noAlertsMessage);
        recyclerView = findViewById(R.id.recyclerViewSafety);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SafetyAlertAdapter(alertList);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "onCreate: RecyclerView setup complete");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocation();
    }

    private void requestLocation() {
        Log.d(TAG, "requestLocation: Requesting new location update");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(0);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                fusedLocationClient.removeLocationUpdates(this);
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLastLocation();
                    Log.d(TAG, "Fresh location received: " + location.getLatitude() + ", " + location.getLongitude());
                    fetchRegionFromLocation(location);
                } else {
                    Log.e(TAG, "LocationResult was empty");
                    Toast.makeText(SafetyAlertsActivity.this, "Could not get updated location", Toast.LENGTH_SHORT).show();
                }
            }
        }, Looper.getMainLooper());
    }

    private void fetchRegionFromLocation(Location location) {
        Log.d(TAG, "fetchRegionFromLocation: Starting geocoding");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> results = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (results != null && !results.isEmpty()) {
                Log.d(TAG, "Full address: " + results.get(0).toString());
                String region = results.get(0).getAdminArea();  // e.g., "Texas"
                Log.d(TAG, "Region determined: " + region);
                regionLabel.setText("Showing safety alerts for " + region);
                fetchSafetyAlerts(region);
            } else {
                Log.e(TAG, "Geocoder returned empty results");
                Toast.makeText(this, "Could not determine your region", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Geocoding error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchSafetyAlerts(String region) {
        Log.d(TAG, "fetchSafetyAlerts: Making API call for region: " + region);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://weatherapi-com.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AlertService api = retrofit.create(AlertService.class);
        Call<AlertResponse> call = api.getAlerts(region);

        Log.d(TAG, "API request URL: " + call.request().url().toString());

        call.enqueue(new Callback<AlertResponse>() {
            @Override
            public void onResponse(Call<AlertResponse> call, Response<AlertResponse> response) {
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API call successful");
                    List<AlertResponse.Alert> alerts = response.body().alerts != null
                            ? response.body().alerts.alert : new ArrayList<>();

                    alertList.clear();
                    for (AlertResponse.Alert alert : alerts) {
                        alertList.add(new SafetyAlert(
                                alert.event,
                                alert.desc,
                                alert.effective,
                                "WeatherAPI"
                        ));
                    }

                    adapter.notifyDataSetChanged();
                    noAlertsMessage.setVisibility(alertList.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Log.e(TAG, "API call failed with response code: " + response.code());
                    Toast.makeText(SafetyAlertsActivity.this, "API Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    noAlertsMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AlertResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                Toast.makeText(SafetyAlertsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                noAlertsMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted");
                requestLocation();
            } else {
                Log.e(TAG, "Location permission denied");
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
