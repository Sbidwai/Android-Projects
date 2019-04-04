package com.example.sbidw.knowyourgov;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;

public class Locator {

    private MainActivity mainActivity;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public Locator(){}
    public Locator(MainActivity activity) {
        mainActivity = activity;

        if (checkPermission()) {
            setUpLocationManager();
            determineLocation();
        }
    }

    public void setUpLocationManager() {

        if (locationManager != null)
            return;

        if (!checkPermission())
            return;

        locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mainActivity.setData(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {

            }

            public void onProviderDisabled(String provider) {

            }
        };

       locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    public void shutdown() {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }
   public void determineLocation() {

        if (!checkPermission())
            return;

        if (locationManager == null)
            setUpLocationManager();

        if (locationManager != null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                double latitude=location.getLatitude();
                double longitude=location.getLongitude();

                mainActivity.setData(latitude,longitude);
                return;
            }
        }

        if (locationManager != null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                mainActivity.setData(location.getLatitude(), location.getLongitude());
                return;
            }
        }

        if (locationManager != null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                mainActivity.setData(location.getLatitude(), location.getLongitude());
                return;
            }
        }

       mainActivity.noLocationAvailable();
        return;
    }


    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            return false;
        }
        return true;
    }
}
