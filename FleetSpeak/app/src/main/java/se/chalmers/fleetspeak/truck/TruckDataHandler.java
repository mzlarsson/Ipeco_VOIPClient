package se.chalmers.fleetspeak.truck;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matz on 2014-10-17.
 */
public class TruckDataHandler implements TruckListener{

    private static TruckDataHandler instance;

    private TruckCommunicator tc;
    private List<TruckStateListener> listeners;

    private float speed = 0.0f;
    private boolean parkingBrake;

    private TruckDataHandler(TruckCommunicator tc){
        this.tc = tc;
        this.listeners = new ArrayList<TruckStateListener>();
    }

    public static void start(){
        if(instance == null){
            instance = new TruckDataHandler(TruckCommunicator.getInstance());
            instance.tc.addListener(instance);
        }
    }

    private static TruckDataHandler getInstance(){
        if(instance == null){
            start();
        }

        return instance;
    }

    public static void addListener(TruckStateListener listener){
        TruckDataHandler handler = getInstance();
        if(listener != null && handler!= null && handler.listeners != null){
            handler.listeners.add(listener);
        }
    }

    public static void removeListener(TruckStateListener listener){
        TruckDataHandler handler = getInstance();
        if(listener != null){
            handler.listeners.add(listener);
        }
    }

    public void speedChanged(float speed){
        boolean oldMode = getTruckMode();
        this.speed = speed;
        evaluateTruckMode(oldMode);
    }

    public void parkingBrakeChanged(boolean isOn){
        boolean oldMode = getTruckMode();
        this.parkingBrake = isOn;
        evaluateTruckMode(oldMode);
    }

    public boolean getTruckMode(){
        return (this.speed != 0 && !this.parkingBrake);
    }

    private void evaluateTruckMode(boolean oldMode){
        boolean mode = getTruckMode();
        if(oldMode != mode) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).truckModeChanged(mode);
            }
        }
    }
}
