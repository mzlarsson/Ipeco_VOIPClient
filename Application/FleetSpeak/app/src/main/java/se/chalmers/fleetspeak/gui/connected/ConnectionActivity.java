package se.chalmers.fleetspeak.gui.connected;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.Room;
import se.chalmers.fleetspeak.gui.establish.BackFragment;
import se.chalmers.fleetspeak.gui.establish.ReconnectFragment;
import se.chalmers.fleetspeak.gui.lists.RoomList.OnRoomClickedListener;
import se.chalmers.fleetspeak.gui.login.LoginActivity;
import se.chalmers.fleetspeak.truck.TruckModeHandler;
import se.chalmers.fleetspeak.truck.TruckModeHandlerFactory;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.MessageValues;

public class ConnectionActivity extends ActionBarActivity implements
        TruckStateListener, LobbyFragment.LobbyFragmentHolder, OnRoomClickedListener, BackFragment.BackFragmentHolder, ReconnectFragment.ReconnectFragmentHolder {
    private Model model;
    private boolean carMode = true;
    private CustomViewPager viewPager;
    private BackFragment backFragment;
    private ReconnectFragment reconnectFragment;

    private List<AppConnectFragment> tabFragments;

    private String username;
    private String password;

    private static Activity currentActivity;

    /**
     * A handler that handles update messages from the server connection
     */
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("updateHandler", "Got Message=" + msg.what);
            switch (msg.what) {
                case MessageValues.CONNECTED:
                    Log.d("UpdateHandler", "Connected");
                    updateView();
                    break;
                case MessageValues.DISCONNECTED:
                    if(!isFinishing()) {
                        Log.d("UpdateHandler", " Disconnected");
                        viewPager.setCurrentItem(tabFragments.size()+1);
                        updateView();
                    }
                    break;
                case MessageValues.MODELCHANGED:
                    updateView();
                    break;
                case MessageValues.CONNECTIONFAILED:
                    Log.d("UpdateHandler", "Connection failed");
                    viewPager.setCurrentItem(tabFragments.size()+1);
                    updateView();
                    break;
                case MessageValues.AUTHENTICATED:
                    Log.d("UpdateHandler", "Authenticated");
                    viewPager.setCurrentItem(0);
                    updateView();
                    break;
                case MessageValues.AUTHENTICATIONFAILED:
                    model.disconnect();
                    returnToLogin("Authentication failed");
                    break;
            }
        }
    };

    public ConnectionActivity(){
        currentActivity = this;
        tabFragments = new ArrayList<>();
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }

    public void updateView() {
        for(AppConnectFragment fragment : tabFragments){
            fragment.refresh();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = this.getIntent().getExtras();
        setContentView(R.layout.activity_connection);
        model = ModelFactory.getModel(updateHandler);
        model.startLocationTracking(this);
        password = extras.getString("password");
        username = extras.getString("username");
        if (!model.isAuthenticated()) {
            Log.d("ConnectionActivity", " Connected");
            model.connect(username, password);
        }

        //Setup view pager
        viewPager = (CustomViewPager) findViewById(R.id.pager);
        viewPager.setHostActivity(this);

        //Lobby fragment
        LobbyFragment lobbyFragment = new LobbyFragment();
        lobbyFragment.setOnRoomClickedListener(this);
        tabFragments.add(lobbyFragment);

        //In room fragment
        InRoomFragment inRoomFragment = new InRoomFragment();
        inRoomFragment.setOnUserClickedListener(null);
        tabFragments.add(inRoomFragment);

        //History fragment
        HistoryFragment historyFragment = new HistoryFragment();
        historyFragment.setOnRoomClickedListener(this);
        tabFragments.add(historyFragment);

        //Proximity fragment
        ProximityFragment proximityFragment = new ProximityFragment();
        proximityFragment.setOnRoomClickedListener(this);
        tabFragments.add(proximityFragment);

        //Add fragments to tabs
        viewPager.addTab(lobbyFragment, R.drawable.ic_house, R.string.lobby, "Lobby");
        viewPager.addTab(inRoomFragment, R.drawable.ic_room, R.string.Chatroom, "InRoom");
        viewPager.addTab(historyFragment, R.drawable.ic_history, R.string.history, "History");
        viewPager.addTab(proximityFragment, R.drawable.ic_location, R.string.proximity, "Proximity");

        //Loose fragments
        backFragment = new BackFragment();
        viewPager.addSpecialTab(backFragment, "Back");
        reconnectFragment = new ReconnectFragment();
        viewPager.addSpecialTab(reconnectFragment, "Reconnect");

        //Finalize
        viewPager.displayTabs();

        //Setup truckmode stuffelistuff
        TruckModeHandler handler = TruckModeHandlerFactory.getHandler(this);
        handler.addListener(this);
        carMode = handler.truckModeActive();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_truck_change, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.changeTruck) {
            truckModeChanged(!carMode);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void returnToLogin(String errorMessage) {
        Intent intent = new Intent(this, LoginActivity.class);
        if (errorMessage != null) {
            intent.putExtra("error", errorMessage);
        }
        startActivity(intent);
        this.finish();
    }

    @Override
    public void truckModeChanged(boolean mode) {
        carMode = mode;
        for(AppConnectFragment fragment : tabFragments){
            fragment.truckModeChanged(carMode);
        }
    }

    @Override
    public void reconnect() {
        Log.d("ConnectionActivity", " reconnect");
        model.connect(username, password);
    }

    @Override
    public void onRoomClicked(Room room) {
        model.move(room.getId());
        updateView();
        viewPager.setCurrentItem(viewPager.getTabIndex("InRoom", false));
    }

    @Override
    public void onBackNo() {
        //Go to default screen
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onBackYes() {
        Log.d("ConnectionActivity", " Back yes & Disconnect");
        model.disconnect();
        returnToLogin(null);
    }

    @Override
    public void onBackPressed() {
        int curItem = viewPager.getCurrentItem();
        //In which fragment?
        if (curItem >=1 && curItem < tabFragments.size()) {
            //If in any other than lobby, go to default screen
            onBackNo();
        } else if (curItem >= tabFragments.size()) {
            //If any non-tabfragment, go to login
            onBackYes();
        } else {
            //If in default screen, go to "really disconnect" screen
            viewPager.setCurrentItem(tabFragments.size());
        }
    }

    @Override
    public void moveToRoom() {
        viewPager.setCurrentItem(viewPager.getTabIndex("InRoom", false));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //model.disconnect();

        //Remove truck mode notifications
        TruckModeHandler handler = TruckModeHandlerFactory.getCurrentHandler();
        handler.removeListener(this);
    }
}
