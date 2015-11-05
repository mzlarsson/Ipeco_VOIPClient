    package se.chalmers.fleetspeak.structure.connected;

    import android.content.Intent;
import android.content.res.Resources;
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

import se.chalmers.fleetspeak.Model;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.structure.establish.BackFragment;
import se.chalmers.fleetspeak.structure.login.LoginActivity;
import se.chalmers.fleetspeak.truck.TruckModeHandlerFactory;
import se.chalmers.fleetspeak.truck.TruckModeHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.MessageValues;
import se.chalmers.fleetspeak.util.ModelFactory;

    public class ConnectionActivity extends ActionBarActivity implements TruckStateListener, ActionBar.TabListener, ConnectedCommunicator {
            private Model model;
            private boolean carMode = true;
            private ActionBar actionBar;
            private CViewPager viewPager;
            private InRoomFragment inRoomFragment;
            private LobbyFragment lobbyFragment;
            private BackFragment backFragment;

            private String username;
            private String password;

            /**
             * A handler that handles update messages from the server connection
             */
            private Handler updateHandler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    Log.d("updateHandler", "Got Message=" + msg.what);
                    switch (msg.what){
                        case MessageValues.CONNECTED:
                            Log.d("UpdateHandler", "Connected");
                            lobbyFragment.isConnected(true);
                            updateView();
                            break;
                        case MessageValues.DISCONNECTED:
                            Log.d("UpdateHandler", " Disconnected");
                            lobbyFragment.isConnected(false);
                            updateView();
                            break;
                        case MessageValues.MODELCHANGED:
                            updateView();
                            break;
                        case MessageValues.CONNECTIONFAILED:
                            Log.d("UpdateHandler", "Connection failed");
                            lobbyFragment.isConnected(false);
                            updateView();
                            break;
                        case MessageValues.AUTHENTICATED:
                            updateView();
                            lobbyFragment.isConnected(true);
                            break;
                        case MessageValues.AUTHENTICATIONFAILED:
                            model.disconnect();
                            returnToLogin("Authentication failed");
                            break;
                    }
                }
            };
            public void updateUsersView(){
                inRoomFragment.resetList(model.getUsers(model.getCurrentRoom()));
            }
            public void updateRoomView(){
                lobbyFragment.resetList(model.getRooms());
            }
            public void updateView(){
                updateRoomView();
                updateUsersView();
            }
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Bundle extras = this.getIntent().getExtras();
                setContentView(R.layout.activity_connection);
                model = ModelFactory.getModel(updateHandler);
                password = extras.getString("password");
                username=  extras.getString("username");
                if( !model.isAuthenticated()){
                    Log.d("ConnectionActivity", " Connected");
                    model.connect(username ,password);
                }else{
                    lobbyFragment.isConnected(true);
                }

                viewPager = (CViewPager)findViewById(R.id.pager);
                viewPager.setId(R.id.pager);
                viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position != 2) {
                            actionBar.setSelectedNavigationItem(position);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });


                lobbyFragment = new LobbyFragment();
                inRoomFragment = new InRoomFragment();
                backFragment = new BackFragment();
                Log.d("ConnectionActivity", " Checking if connected");

                TruckModeHandler handler = TruckModeHandlerFactory.getCurrentHandler(this);
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
                String chatroom = getRecources().getString(R.string.Chatroom);
                inRoom.setText(chatroom);
                inRoom.setTabListener(this);
                actionBar.addTab(inRoom);
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
                    return true;}


                return super.onOptionsItemSelected(item);
            }

            private void changeCarModeTabs(boolean b){
                if(lobbyFragment != null){
                    lobbyFragment.truckModeChanged(b);

                }
                if(inRoomFragment != null){
                    inRoomFragment.truckModeChanged(b);
                }
            }
            private void returnToLogin(String errorMessage){
                Intent intent = new Intent(this, LoginActivity.class);
                if(errorMessage != null){
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
            public List<Room> getRooms() {
                return model.getRooms();
            }

        @Override
        public void reconnect() {
            model.connect(username, password);
        }

        @Override
            public ArrayList<User> getUsersForRoom(int RoomID) {
                return model.getUsers(RoomID);
            }

            @Override
            public boolean getTruckState() {
                return carMode;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public void createAndMoveRoom(String newRoomName) {
                lobbyFragment.closeDialog();
                model.moveNewRoom(newRoomName);
                updateView();
                lobbyFragment.movedToRoom(getCurrentRoomId());
                viewPager.setCurrentItem(1);
                actionBar.setSelectedNavigationItem(1);
            }

            @Override
            public int getCurrentRoomId() {
                return model.getCurrentRoom();
            }

            @Override
            public List<User> getCurrentRoomsUsers() {
                return model.getUsers(model.getCurrentRoom());
            }

            @Override
            public void sendUserClicked(User user) {

            }

            @Override
            public void roomClicked(Room room) {
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
            public void onBackPressed(){
                if(viewPager.getCurrentItem() == 1){
                    Log.d("ConnectionActivity", " current item on back is 1");
                    onBackNo();
                }
                else if(viewPager.getCurrentItem() == 2){
                    onBackYes();
                }else{
                    viewPager.setCurrentItem(2);
                }
            }
        @Override
        public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
            Log.d("ConnectionActivity", " clicked tab" + tab.getPosition());
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

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
                    }else if (arg == 2){
                        Log.d("ConnectionActivity"," Back called");
                        return backFragment;
                    }
                    return null;
                }

                @Override
                public int getCount() {
                    return 3;
                }
            }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            model.disconnect();

            //Remove truck mode notifications
            TruckModeHandler handler = TruckModeHandlerFactory.getCurrentHandler(this);
            handler.removeListener(this);
        }
        public Resources getRecources(){
           return getResources();
        }

}
