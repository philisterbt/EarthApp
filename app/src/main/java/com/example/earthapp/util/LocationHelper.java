package com.example.earthapp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class LocationHelper extends LiveData<Location> {
    private final LocationManager locationManager;
    private final Context context;

    public LocationHelper(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    @SuppressLint("MissingPermission") 
    protected void onActive() {
        super.onActive();
        if (locationManager != null) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
                
                Location lastGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location lastNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                
                if (lastGps != null) setValue(lastGps);
                else if (lastNet != null) setValue(lastNet);
                
            } catch (Exception e) {
                Log.e("LocationHelper", "Error requesting location", e);
            }
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public Location getLastLocation() {
        return getValue();
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            setValue(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(@NonNull String provider) {}
        @Override
        public void onProviderDisabled(@NonNull String provider) {}
    };
    

    public void startListening() { /* No-op, handled by onActive */ }
    public void stopListening() { /* No-op, handled by onInactive */ }
}
