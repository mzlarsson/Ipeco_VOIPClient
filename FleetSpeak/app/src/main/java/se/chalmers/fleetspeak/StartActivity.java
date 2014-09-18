package se.chalmers.fleetspeak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.swedspot.automotive.AutomotiveBroadcast;
import android.swedspot.automotive.AutomotiveManager;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.swedspot.automotiveapi.unit.AutomotiveUnit;
import android.swedspot.scs.data.SCSFloat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.swedspot.automotiveapi.AutomotiveListener;


public class StartActivity extends ActionBarActivity {

    public static final String TAG = "tmp";
    public static AutomotiveManager manager;
    public static BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initiateManager();
        initiateReceiver();

        final Handler mHandler = new Handler();
        final Runnable r = new Runnable(){
            @Override
            public void run(){
                StartActivity.manager.send(new AutomotiveSignal(AutomotiveSignalId.FMS_WHEEL_BASED_SPEED, new SCSFloat(120.0f), AutomotiveUnit.KILOMETERS_PER_HOUR));
                mHandler.postDelayed(this, 100);
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initiateManager(){
        manager = (AutomotiveManager) getApplicationContext().getSystemService(Context.AUTOMOTIVE_SERVICE);
        manager.register(AutomotiveSignalId.FMS_WHEEL_BASED_SPEED);
        manager.register(AutomotiveSignalId.FMS_SELECTED_GEAR);
        manager.setListener(new AutomotiveListener() {
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
        });
    }

    public void initiateReceiver(){
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                final int level = intent.getIntExtra(AutomotiveBroadcast.EXTRA_DRIVER_DISTRACTOIN_LEVEL, 5);
                Log.i(TAG, "New broadcast " + level);
            }
        };

        final IntentFilter intentFilter = new IntentFilter(AutomotiveBroadcast.ACTION_DRIVER_DISTRACTION_LEVEL_CHANGED);
        final Intent currentValue = getApplicationContext().registerReceiver(receiver, intentFilter);
        if (currentValue != null) {
            final int level = currentValue.getIntExtra(AutomotiveBroadcast.EXTRA_DRIVER_DISTRACTOIN_LEVEL, 5);
            Log.d(TAG, "Initial driver distraction value: " + level);
        }
    }
}
