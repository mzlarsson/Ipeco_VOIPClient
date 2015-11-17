package se.chalmers.fleetspeak.structure.connected;

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

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.Room;
import se.chalmers.fleetspeak.model.User;
import se.chalmers.fleetspeak.structure.establish.BackFragment;
import se.chalmers.fleetspeak.structure.establish.ReconnectFragment;
import se.chalmers.fleetspeak.structure.lists.RoomList.OnRoomClickedListener;
import se.chalmers.fleetspeak.structure.lists.UserList.OnUserClickedListener;
import se.chalmers.fleetspeak.structure.login.LoginActivity;
import se.chalmers.fleetspeak.truck.TruckModeHandler;
import se.chalmers.fleetspeak.truck.TruckModeHandlerFactory;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.MessageValues;

public class ConnectionActivity extends ActionBarActivity implements
        TruckStateListener, ActionBar.TabListener, LobbyFragment.LobbyFragmentHolder, OnRoomClickedListener, BackFragment.BackFragmentHolder, ReconnectFragment.ReconnectFragmentHolder
        , HistoryFragment.HistoryFragmentHolder{
    private Model model;
    private boolean carMode = true;
    private ActionBar actionBar;
    private CustomViewPager viewPager;
    private InRoomFragment inRoomFragment;
    private LobbyFragment lobbyFragment;
    private BackFragment backFragment;
    private ReconnectFragment reconnectFragment;
    private HistoryFragment historyFragment;

    private String username;
    private String password;

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
                    Log.d("UpdateHandler", " Disconnected");
                    viewPager.setCurrentItem(4);
                    updateView();
                    break;
                case MessageValues.MODELCHANGED:
                    updateView();
                    break;
                case MessageValues.CONNECTIONFAILED:
                    Log.d("UpdateHandler", "Connection failed");
                    viewPager.setCurrentItem(4);
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

    public void updateUsersView() {
        inRoomFragment.refresh();
    }

    public void updateRoomView() {
        lobbyFragment.refresh();
    }
    public void updateHistory() {historyFragment.refresh();}
    public void updateView() {
        updateRoomView();
        updateUsersView();
        updateHistory();
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
        viewPager = (CustomViewPager) findViewById(R.id.pager);
        viewPager.setId(R.id.pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position < 3) {
                        actionBar.setSelectedNavigationItem(position);
                    }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        lobbyFragment = new LobbyFragment();
        lobbyFragment.setOnRoomClickedListener(this);
        inRoomFragment = new InRoomFragment();
        inRoomFragment.setOnUserClickedListener(null);          //Add listener if any functionality is needed.
        backFragment = new BackFragment();
        reconnectFragment = new ReconnectFragment();
        historyFragment = new HistoryFragment();
        Log.d("ConnectionActivity", " Checking if connected");

        TruckModeHandler handler = TruckModeHandlerFactory.getHandler(this);
        handler.addListener(this);
        carMode = handler.truckModeActive();

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab rooms = actionBar.newTab();
        rooms.setIcon(R.drawable.ic_house);
        String lobby = getResources().getString(R.string.lobby);
        rooms.setText(lobby);
        rooms.setTabListener(this);
        actionBar.addTab(rooms);

        ActionBar.Tab inRoom = actionBar.newTab();
        inRoom.setIcon(R.drawable.ic_room);
        String chatroom = getResources().getString(R.string.Chatroom);
        inRoom.setText(chatroom);
        inRoom.setTabListener(this);
        actionBar.addTab(inRoom);

        ActionBar.Tab history = actionBar.newTab();
        history.setIcon(R.drawable.ic_history);
        String historyText = getResources().getString(R.string.history);
        history.setText(historyText);
        history.setTabListener(this);
        actionBar.addTab(history);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_truck_change, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // home id seems overwritten write out number
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.changeTruck) {
            truckModeChanged(!carMode);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void changeCarModeTabs(boolean b) {
        if (lobbyFragment != null) {
            lobbyFragment.truckModeChanged(b);

        }
        if (inRoomFragment != null) {
            inRoomFragment.truckModeChanged(b);
        }
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
        changeCarModeTabs(mode);
    }

    @Override
    public void reconnect() {
        Log.d("ConnectionActivity", " reconnect");
        model.connect(username, password);
    }

    @Override
    public void onRoomClicked(Room room) {
        model.move(room.getId());
        updateUsersView();
        lobbyFragment.movedToRoom(room.getId());
        viewPager.setCurrentItem(1);
        actionBar.setSelectedNavigationItem(1);
    }

    @Override
    public void onBackNo() {
        viewPager.setCurrentItem(0);
        actionBar.setSelectedNavigationItem(0);
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
        if (curItem <=1 && curItem < 3) {
            Log.d("ConnectionActivity", " current item on back is 1");
            onBackNo();
        } else if (curItem >= 3) {
            onBackYes();
        } else {
            viewPager.setCurrentItem(3);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        Log.d("ConnectionActivity", " clicked tab" + tab.getPosition());
        if(viewPager.getCurrentItem() < 3) {
            viewPager.setCurrentItem(tab.getPosition());
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void moveToRoom() {
        viewPager.setCurrentItem(1);
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int arg) {
            Log.d("ConnectionActivity", " getItem called");

            if (arg == 0) {
                Log.d("ConnectionActivity", " Lobby called");
                return lobbyFragment;
            } else if (arg == 1) {
                Log.d("ConnectionActivity", " InRoom called");
                return inRoomFragment;
            }else if(arg == 2){
                return historyFragment;
            } else if (arg == 3) {
                Log.d("ConnectionActivity", " Back called");
                return backFragment;
            } else if(arg == 4){
                return reconnectFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.disconnect();

        //Remove truck mode notifications
        TruckModeHandler handler = TruckModeHandlerFactory.getHandler(this);
        handler.removeListener(this);
    }
}
