package se.chalmers.fleetspeak.truck;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.fleetspeak.util.LocationUtil;
import se.chalmers.fleetspeak.util.LocationUtil.LocationChangeListener;

/**
 * Created by Matz on 2015-11-05.
 */
public class LocationDataHandler implements LocationChangeListener, TruckModeHandler {

    private static double LOWER_SPEED_BOUNDARY = 2;

    private boolean truckMode = false;
    private List<TruckStateListener> stateListeners;

    private LocationUtil locationUtil;
    private Timer nodataTimer;
    private TimerTask nodataTimerTask;

    public LocationDataHandler(Context context) {
        stateListeners = new LinkedList<TruckStateListener>();

        locationUtil = LocationUtil.getInstance(context, false);
        locationUtil.addListener(this);

        nodataTimer = new Timer("No Data Timer");
        nodataTimerTask = new TimerTask() {
            @Override
            public void run() {
                //No data received, meaning you do not move fast enough -> Not driving.
                truckMode = false;
            }
        };
    }

    @Override
    public void addListener(TruckStateListener listener){
        if(listener != null){
            stateListeners.add(listener);
        }
    }

    @Override
    public void removeListener(TruckStateListener listener){
        if(listener != null){
            stateListeners.remove(listener);
        }
    }

    @Override
    public boolean truckModeActive(){
        return truckMode;
    }

    @Override
    public void speedChanged(float speed){
        boolean newTruckMode = speed<LOWER_SPEED_BOUNDARY;
        if(newTruckMode != truckMode){
            truckMode = newTruckMode;
            for(TruckStateListener listener : stateListeners){
                listener.truckModeChanged(truckMode);
            }

            if(truckMode){
                nodataTimer.schedule(nodataTimerTask, locationUtil.getMinTime()*2);
            }
        }
    }

    @Override
    public void locationChanged(double latitude, double longitude){
        //Do nothing.
    }
}
