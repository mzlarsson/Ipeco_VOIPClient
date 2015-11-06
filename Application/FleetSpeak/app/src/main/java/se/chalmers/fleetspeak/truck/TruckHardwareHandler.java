package se.chalmers.fleetspeak.truck;

import android.app.Activity;
import android.os.AsyncTask;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.swedspot.scs.data.SCSFloat;
import android.swedspot.scs.data.Uint8;
import android.util.Log;

import com.swedspot.automotiveapi.AutomotiveFactory;
import com.swedspot.automotiveapi.AutomotiveListener;
import com.swedspot.automotiveapi.AutomotiveManager;
import com.swedspot.vil.distraction.DriverDistractionLevel;
import com.swedspot.vil.distraction.DriverDistractionListener;
import com.swedspot.vil.distraction.LightMode;
import com.swedspot.vil.distraction.StealthMode;
import com.swedspot.vil.policy.AutomotiveCertificate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matz Larsson on 2014-09-19.
 * Used for retrieving and decoding signals from the simulator.
 */

class TruckHardwareHandler extends AsyncTask<Void, Void, Object> implements TruckModeHandler {

    public static final String TAG = "TruckHardwareDebug";

    private static AutomotiveManager manager;
    private List<TruckStateListener> listeners;

    private float speed = 0.0f;
    private boolean parkingBrake;

    protected TruckHardwareHandler(){
        listeners = new ArrayList<TruckStateListener>();
    }

    @Override
    public void addListener(TruckStateListener listener){
        if(listener != null){
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(TruckStateListener listener){
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

            @Override
            public void lightModeChanged(LightMode lightMode) {
                Log.d(TAG, "Light mode changed: "+lightMode.name());
            }

            @Override
            public void stealthModeChanged(StealthMode stealthMode) {
                Log.d(TAG, "Stealth mode changed: "+stealthMode.name());
            }
        };
    }




    public void speedChanged(float speed){
        boolean oldMode = truckModeActive();
        this.speed = speed;
        evaluateTruckMode(oldMode);
    }

    public void parkingBrakeChanged(boolean isOn){
        boolean oldMode = truckModeActive();
        this.parkingBrake = isOn;
        evaluateTruckMode(oldMode);
    }

    @Override
    public boolean truckModeActive(){
        return (this.speed != 0 && !this.parkingBrake);
    }

    private void evaluateTruckMode(boolean oldMode){
        final boolean mode = truckModeActive();
        if(oldMode != mode) {
            for (int i = 0; i < listeners.size(); i++) {
                final TruckStateListener l = listeners.get(i);
                if(l instanceof Activity) {
                    final Activity c = (Activity)l;
                    c.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l.truckModeChanged(mode);
                        }
                    });
                }else{
                    l.truckModeChanged(mode);
                }
            }
        }
    }
}
