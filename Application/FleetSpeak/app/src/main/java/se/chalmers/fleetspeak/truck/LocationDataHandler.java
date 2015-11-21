package se.chalmers.fleetspeak.truck;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

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

    private Context context;

    public LocationDataHandler(Context context) {
        stateListeners = new LinkedList<TruckStateListener>();
        this.context = context;

        locationUtil = LocationUtil.getInstance(context, false);
        locationUtil.addListener(this);
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

    private boolean setTruckMode(final boolean truckMode){
        if(this.truckMode != truckMode){
            this.truckMode = truckMode;

            for(final TruckStateListener listener : stateListeners){
                if(listener instanceof Activity){
                    ((Activity)listener).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.truckModeChanged(truckMode);
                        }
                    });
                }else {
                    listener.truckModeChanged(truckMode);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean truckModeActive(){
        return truckMode;
    }

    @Override
    public void speedChanged(float speed){
        Toast.makeText(context, "Got an speed update: "+speed, Toast.LENGTH_LONG).show();
        boolean newTruckMode = speed>LOWER_SPEED_BOUNDARY;
        setTruckMode(newTruckMode);

        if(truckMode) {
            startCountdownTimer();
        }else{
            cancelTimer();
        }
    }

    private void startCountdownTimer(){
        cancelTimer();

        nodataTimer = new Timer("No Data Timer");
        nodataTimerTask = new TimerTask() {
            @Override
            public void run() {
                //No data received, meaning you do not move fast enough -> Not driving.
                setTruckMode(false);
            }
        };

        //Schedule countdown - if no data arrives in a while carmode is set to false
        nodataTimer.schedule(nodataTimerTask, (int) (Math.max(locationUtil.getMinTime(), locationUtil.getMinDistance() / LOWER_SPEED_BOUNDARY) * 2));
    }

    private void cancelTimer(){
        if(nodataTimer != null) {
            nodataTimer.cancel();
        }
        if(nodataTimerTask != null) {
            nodataTimerTask.cancel();
        }
    }

    @Override
    public void locationChanged(double latitude, double longitude){
        //Do nothing.
    }
}
