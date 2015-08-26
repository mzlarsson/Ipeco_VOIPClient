package se.chalmers.fleetspeak.fragments.NewStructure.location;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.List;

import se.chalmers.fleetspeak.truck.TruckStateListener;

/**
 * Created by David Gustafsson on 2015-08-20.
 */
public class LocationHandler {
        LocationManager locationManager;
    public LocationHandler(Activity activity){
        List<TruckStateListener> listenerList;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                location.getSpeed();

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
    }
}
