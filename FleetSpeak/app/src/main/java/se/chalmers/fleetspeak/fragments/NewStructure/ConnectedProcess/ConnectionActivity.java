package se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import se.chalmers.fleetspeak.Model;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.fragments.FragmentHandler;
import se.chalmers.fleetspeak.truck.TruckDataHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.MessageValues;

public class ConnectionActivity extends ActionBarActivity implements TruckStateListener {
    private Model model;
    private boolean carMode = false;
    private ActionBar actionBar;

    /**
     * A handler that handles update messages from the server connection
     */
    private Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.d("updateHandler", "Got Message=" + msg.what);
            switch (msg.what){
                case MessageValues.CONNECTED:
                    // If the user is able to be connected go to the join fragment
                    // where the user can choose which room to join
                    break;
                case MessageValues.DISCONNECTED:
                    // If disconnected from the server return the to start fragment
                    // where user can try to connect again
                    break;
                case MessageValues.MODELCHANGED:
                    break;

                case MessageValues.CONNECTIONFAILED:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        model = new Model(this, updateHandler);
        model.connect((String)savedInstanceState.get("password"), 8867);

        TruckDataHandler.addListener(this);
        carMode = TruckDataHandler.getInstance().getTruckMode();

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        setUpTabs();
    }
    public void setUpTabs(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void truckModeChanged(boolean mode) {

    }
}
