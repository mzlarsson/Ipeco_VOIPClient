package se.chalmers.fleetspeak.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Matz on 2015-11-05.
 */
public class LocationUtil {

    private static LocationUtil instance;

    private int minTime = 5000;
    private int minDistance = 5;

    private LocationProvider provider;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private final List<LocationChangeListener> changeListeners = new LinkedList<LocationChangeListener>();

    private LocationUtil(Context context){
        startTracking(context, LocationManager.GPS_PROVIDER);
    }

    public static LocationUtil getInstance(Context context, boolean forceRestart){
        if(instance == null || forceRestart){
            if(forceRestart){
                instance.stopTracking();
            }
            instance = new LocationUtil(context);
        }

        return instance;
    }

    public void addListener(LocationChangeListener listener){
        if(listener != null){
            changeListeners.add(listener);
        }
    }

    public void removeListener(LocationChangeListener listener){
        if(listener != null){
            changeListeners.remove(listener);
        }
    }

    public int getMinTime(){
        return minTime;
    }

    public int getMinDistance(){
        return minDistance;
    }

    private boolean startTracking(Context context, final String providerName){
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    for(LocationChangeListener listener : changeListeners){
                        //Announce change of position
                        listener.locationChanged(location.getLatitude(), location.getLongitude());

                        //Announce change of speed
                        if(location.hasSpeed()){
                            listener.speedChanged(location.getSpeed());
                        }
                    }
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String enabledProvider) {}
                public void onProviderDisabled(String disabledProvider) {}
            };
            locationManager.requestLocationUpdates(providerName, minTime, minDistance, locationListener);
            provider = locationManager.getProvider(providerName);
            return true;
        } catch (SecurityException se) {
            return false;
        }
    }

    private void stopTracking(){
        if(locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public interface LocationChangeListener{
        public void speedChanged(float speed);
        public void locationChanged(double latitude, double longitude);
    }
}
