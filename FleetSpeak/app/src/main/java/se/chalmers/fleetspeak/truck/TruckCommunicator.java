package se.chalmers.fleetspeak.truck;

import android.os.AsyncTask;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.swedspot.scs.data.SCSBoolean;
import android.swedspot.scs.data.SCSFloat;
import android.swedspot.scs.data.Uint8;
import android.util.Log;

import com.swedspot.automotiveapi.AutomotiveFactory;
import com.swedspot.automotiveapi.AutomotiveListener;
import com.swedspot.automotiveapi.AutomotiveManager;
import com.swedspot.vil.distraction.DriverDistractionLevel;
import com.swedspot.vil.distraction.DriverDistractionListener;
import com.swedspot.vil.policy.AutomotiveCertificate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matz Larsson on 2014-09-19.
 * Used for retrieving and decoding signals from the simulator.
 */

class TruckCommunicator extends AsyncTask<Void, Void, Object> {
    private static TruckCommunicator instance;
    public static final String TAG = "simulatorDebug";

    private static AutomotiveManager manager;
    private List<TruckListener> listeners;

    private TruckCommunicator(){
        listeners = new ArrayList<TruckListener>();
    }

    public static TruckCommunicator getInstance(){
        if(instance == null){
            instance = new TruckCommunicator();
            instance.execute();
        }
        return instance;
    }

    public void addListener(TruckListener listener){
        if(listener != null){
            listeners.add(listener);
        }
    }

    public void removeListener(TruckListener listener){
        if(listener != null){
            listeners.remove(listener);
        }
    }

    public Void doInBackground(Void... voids){
        Log.d(TAG, "Starting background connection against simulator... Waiting...");
        manager = AutomotiveFactory.createAutomotiveManagerInstance(new AutomotiveCertificate(new byte[0]), getAutomotiveListener(), getDistractionListener());
        manager.register(AutomotiveSignalId.FMS_WHEEL_BASED_SPEED);
        manager.register(AutomotiveSignalId.FMS_PARKING_BRAKE);

        return null;
    }

    private AutomotiveListener getAutomotiveListener(){
        return new AutomotiveListener() {
            @Override
            public void timeout(final int signalId) {
                // Request did time out and no data was received
                Log.d(TAG, "timeout " + signalId);
            }

            @Override
            public void receive(final AutomotiveSignal automotiveSignal) {
                // Incoming signal
                switch (automotiveSignal.getSignalId()) {
                    case AutomotiveSignalId.FMS_WHEEL_BASED_SPEED:
                        speedChanged(((SCSFloat) automotiveSignal.getData()).getFloatValue());
                        break;
                    case AutomotiveSignalId.FMS_PARKING_BRAKE:
                        Log.d("TRUCKS", ((Uint8) automotiveSignal.getData()).getIntValue() + "");
                        parkingBrakeChanged(((Uint8) automotiveSignal.getData()).getIntValue()==0);
                        break;
                    default: Log.d(TAG, "got signal");
                        break;
                }
            }

            @Override
            public void notAllowed(final int signalId) {
                // Policy does not allow for this operation
                Log.d(TAG, "not allowed " + signalId);
            }
        };
    }

    public DriverDistractionListener getDistractionListener() {
        return new DriverDistractionListener() {
            @Override
            public void levelChanged(DriverDistractionLevel driverDistractionLevel) {
                Log.d(TAG, "Driver level changed: "+driverDistractionLevel.getLevel());
            }
        };
    }



    private void speedChanged(float speed){
        for(TruckListener listener : listeners){
            listener.speedChanged(speed);
        }
    }
    private void parkingBrakeChanged(boolean isOn){
        for(TruckListener listener : listeners){
            listener.parkingBrakeChanged(isOn);
        }
    }
}
