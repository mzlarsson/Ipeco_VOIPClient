package se.chalmers.fleetspeak;

import android.os.AsyncTask;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.swedspot.scs.data.SCSFloat;
import android.util.Log;

import com.swedspot.automotiveapi.AutomotiveFactory;
import com.swedspot.automotiveapi.AutomotiveListener;
import com.swedspot.automotiveapi.AutomotiveManager;
import com.swedspot.vil.distraction.DriverDistractionLevel;
import com.swedspot.vil.distraction.DriverDistractionListener;
import com.swedspot.vil.policy.AutomotiveCertificate;

/**
 * Created by Matz Larsson on 2014-09-19.
 * Used for retrieving and decoding signals from the simulator.
 */

public class TruckCommunicator extends AsyncTask<Integer, Object, Object> {

    public static final String TAG = "simulatorDebug";
    public static AutomotiveManager manager;

    public Object doInBackground(Integer... signalIDs){
        Log.d(TAG, "Starting background connection against simulator... Waiting...");
        manager = AutomotiveFactory.createAutomotiveManagerInstance(new AutomotiveCertificate(new byte[0]), getAutomotiveListener(), getDistractionListener());
        for(int i = 0; i<signalIDs.length; i++){
            manager.register(signalIDs[i]);
        }

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
                        // Unsafe type cast. Could be made safe with automotiveSignal.getData().getDataType()
                        Log.d(TAG, "FMS_WHEEL_BASED_SPEED: " + ((SCSFloat) automotiveSignal.getData()).getFloatValue() + " " + automotiveSignal.getUnit().toString());
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

}
