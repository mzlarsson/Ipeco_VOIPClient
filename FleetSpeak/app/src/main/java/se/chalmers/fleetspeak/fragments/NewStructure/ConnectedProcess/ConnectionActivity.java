    package se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess;

    import android.app.ActionBar;
    import android.app.FragmentTransaction;
    import android.content.Intent;
    import android.os.Handler;
    import android.os.Message;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentPagerAdapter;
    import android.support.v4.view.ViewPager;
    import android.support.v7.app.ActionBarActivity;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;

    import java.util.ArrayList;
    import java.util.List;

    import se.chalmers.fleetspeak.Model;
    import se.chalmers.fleetspeak.R;
    import se.chalmers.fleetspeak.Room;
    import se.chalmers.fleetspeak.User;
    import se.chalmers.fleetspeak.fragments.FragmentHandler;
    import se.chalmers.fleetspeak.fragments.NewStructure.DummyModel.DummyModel;
    import se.chalmers.fleetspeak.fragments.NewStructure.LoginProcess.LoginActivity;
    import se.chalmers.fleetspeak.truck.TruckDataHandler;
    import se.chalmers.fleetspeak.truck.TruckStateListener;
    import se.chalmers.fleetspeak.util.MessageValues;

    public class ConnectionActivity extends ActionBarActivity implements TruckStateListener, ActionBar.TabListener, ConnectedCommunicator {
        private Model model;
        private boolean carMode = false;
        private ActionBar actionBar;
        private ViewPager viewPager;
        private InRoomFragment inRoomFragment;
        private LoobyFragment loobyFragment;
        private String username;

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
            Bundle extras = this.getIntent().getExtras();
            setContentView(R.layout.activity_connection);
            viewPager = new ViewPager(this);
            viewPager.setId(R.id.pager);
            viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            model = new DummyModel(this, updateHandler);
            model.connect((String) extras.get("password"), 8867);
            username = (String)extras.get("username");

            TruckDataHandler.addListener(this);
            carMode = TruckDataHandler.getInstance().getTruckMode();

            actionBar = getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            setUpTabs();
        }
        public void setUpTabs(){
            ActionBar.Tab rooms = actionBar.newTab();
            rooms.setIcon(R.drawable.ic_room);
            rooms.setText("Looby");
            rooms.setTabListener(this);
            actionBar.addTab(rooms);

            ActionBar.Tab inRoom = actionBar.newTab();
            inRoom.setIcon(R.drawable.ic_house);
            inRoom.setText("ChatRoom");
            inRoom.setTabListener(this);
            actionBar.addTab(inRoom);
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

        private void changeCarModeTabs(boolean b){
            if(loobyFragment != null){
                loobyFragment.truckModeChanged(b);
            }
            if(inRoomFragment != null){
                inRoomFragment.truckModeChanged(b);
            }

        }
        private void reconnect(){

        }
        private void returnToLogin(String errorMessage){
            Intent intent = new Intent(this, LoginActivity.class);
            if(errorMessage != null){
                intent.putExtra("error", errorMessage);
            }
            startActivity(intent);
        }

        @Override
        public void truckModeChanged(boolean mode) {
            changeCarModeTabs(mode);
        }
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public List<Room> getRooms() {
            return model.getRooms();
        }

        @Override
        public ArrayList<User> getUsersForRoom(int RoomID) {
            return model.getUsers(RoomID);
        }

        @Override
        public boolean getTruckState() {
            return false;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public void createAndMoveRoom(String newRoomName) {
            loobyFragment.closeDialog();
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
        }


        public class PagerAdapter extends FragmentPagerAdapter{
            public PagerAdapter(FragmentManager fragmentManager){
                super(fragmentManager);
            }
            @Override
            public android.support.v4.app.Fragment getItem(int arg) {
                if(arg == 0){
                    if(loobyFragment == null){
                        loobyFragment = new LoobyFragment();
                    }
                    return loobyFragment;
                }else if(arg == 1) {
                    if(inRoomFragment == null){
                        inRoomFragment = new InRoomFragment();
                    }
                    return inRoomFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        }


    }
