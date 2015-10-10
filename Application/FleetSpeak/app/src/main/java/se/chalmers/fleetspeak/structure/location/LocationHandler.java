package se.chalmers.fleetspeak.structure.location;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.truck.TruckStateListener;

/**
 * Created by David Gustafsson on 2015-08-20.
 */
public class LocationHandler {
        LocationManager locationManager;
        List<LocationUpdateListener> updateListeners;
        List<TruckStateListener> truckListeners;
        private boolean moving = false;
        private float lastSpeed = 0;
        private long lastupdate = 0;


    public LocationHandler(Activity activity){
        updateListeners = new ArrayList<LocationUpdateListener>();
        truckListeners = new ArrayList<TruckStateListener>();
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float speed = location.getSpeed();
                Log.d("LocationHandler", " speed is " + speed);
                Log.d("LocationHandler", " Time is " + System.currentTimeMillis());
                if(speed > 0.1 && !moving){
                    moving = true;
                    lastSpeed = speed;
                    updateTruckList();
                    lastupdate = System.currentTimeMillis();
                }else if( moving && ((System.currentTimeMillis() - lastupdate) > 5000)){
                        moving = false;
                        lastSpeed = speed;
                        updateTruckList();
                        lastupdate = System.currentTimeMillis();
                }
            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                10, locationListener);
    }

    private void updateTruckList(){
        for(TruckStateListener lis: truckListeners){
            lis.truckModeChanged(moving);
        }
    }
    private void updateLocationList(Location location){
        for(LocationUpdateListener lis: updateListeners){
            lis.locationUpdated(location);
        }
    }
    public void addLocationUpdateListener(LocationUpdateListener listener){
        updateListeners.add(listener);
    }
    public void removeLocationUpdateListener(LocationUpdateListener listener){
        updateListeners.remove(listener);
    }
    public void addTruckListener(TruckStateListener listener){
        truckListeners.add(listener);
    }
    public void removeTruckListener(TruckStateListener listener){
        truckListeners.remove(listener);
    }
    public boolean getCarMode(){
        return moving;
    }
    public Location getLastLocation(){
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }
    public float getLastSpeed(){
        return lastSpeed;
    }


}
