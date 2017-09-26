package com.example.administrator.gpsapplication;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.gpsapplication.Constant.Common;

import static com.example.administrator.gpsapplication.Constant.Common.FIFTY_METER;
import static com.example.administrator.gpsapplication.Constant.Common.FIVE_SECONDS;

public class LocationService extends Service implements LocationListener {
    private static final String TAG = "LocationSvc";
    private LocationManager locationManager;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        /*throw new UnsupportedOperationException("Not yet implemented");*/
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateLoaction();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    //TODO read current Location and store it to SQLite
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Get the current position \n" + location);
        Intent intent = new Intent();
        intent.setAction(Common.LOCATION_ACTION);
        intent.putExtra(Common.LOCATION_LATITUDE, location.getLatitude());
        intent.putExtra(Common.LOCATION_LONGITUDE,location.getLongitude());
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
        stopSelf();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void updateLoaction() {
        PackageManager pm = getPackageManager();
        //check if permission is allowed
        boolean havePermission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.ACCESS_FINE_LOCATION", getPackageName()));

        //according to provider, choose the relevant method
        if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null && havePermission) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    FIVE_SECONDS,
                    FIFTY_METER,
                    this);
        } else if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null && havePermission) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    FIVE_SECONDS,
                    FIFTY_METER,
                    this);
        } else Toast.makeText(this, "无法定位", Toast.LENGTH_SHORT).show();
    }
}
