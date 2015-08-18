    package se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess;

    import android.animation.Animator;
    import android.animation.AnimatorListenerAdapter;
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
    import android.view.View;

    import java.util.ArrayList;
    import java.util.List;

    import se.chalmers.fleetspeak.Model;
    import se.chalmers.fleetspeak.R;
    import se.chalmers.fleetspeak.Room;
    import se.chalmers.fleetspeak.User;
    import se.chalmers.fleetspeak.fragments.NewStructure.DummyModel.DummyModel;
    import se.chalmers.fleetspeak.fragments.NewStructure.EstablishConnection.BackFragment;
    import se.chalmers.fleetspeak.fragments.NewStructure.LoginProcess.LoginActivity;
    import se.chalmers.fleetspeak.truck.TruckDataHandler;
    import se.chalmers.fleetspeak.truck.TruckStateListener;
    import se.chalmers.fleetspeak.util.MessageValues;

    public class ConnectionActivity extends ActionBarActivity implements TruckStateListener, ActionBar.TabListener, ConnectedCommunicator {
            private Model model;
            private boolean carMode = true;
            private ActionBar actionBar;
            private CViewPager viewPager;
            private InRoomFragment inRoomFragment;
            private LoobyFragment loobyFragment;
            private BackFragment backFragment;
            private View loadingView;
            private String username;
            private boolean isConnected = false;


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
            public void updateUsersView(){
                inRoomFragment.resetList(model.getUsers(model.getCurrentRoom()));
            }
            public void updateRoomView(){
                loobyFragment.resetList(model.getRooms());
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
                loadingView = findViewById(R.id.loading_spinner);
                viewPager = (CViewPager)findViewById(R.id.pager);
                viewPager.setId(R.id.pager);
                viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if(position != 2) {
                            actionBar.setSelectedNavigationItem(position);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
                model = new DummyModel(this, updateHandler);
                //model.connect((String) extras.get("password"), 8867);
                username = (String)extras.get("username");

                TruckDataHandler.addListener(this);
                carMode = TruckDataHandler.getInstance().getTruckMode();

                actionBar = getActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                ActionBar.Tab rooms = actionBar.newTab();
                rooms.setIcon(R.drawable.ic_house);
                rooms.setText("Looby");
                rooms.setTabListener(this);
                actionBar.addTab(rooms);


                ActionBar.Tab inRoom = actionBar.newTab();
                inRoom.setIcon(R.drawable.ic_room);
                inRoom.setText("ChatRoom");
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
                int id = item.getItemId();
                //noinspection SimplifiableIfStatement
                if (id == R.id.changeTruck) {
                    truckModeChanged(!carMode);
                    return true;
                }else if(id == R.id.menuCon){
                    if(isConnected) {
                        model.disconnect();
                    }else{
                        model.connect("lalal", 88867);
                        updateRoomView();
                    }
                    isConnected = !isConnected;
                    // home id seems overwritten write out number
                }else if(id == 16908332){
                    if(viewPager.getCurrentItem() == 2){
                        returnToLogin(null);
                    }else{
                        viewPager.setCurrentItem(2);
                    }
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
            private void returnToLogin(String errorMessage){
                Intent intent = new Intent(this, LoginActivity.class);
                if(errorMessage != null){
                    intent.putExtra("error", errorMessage);
                }
                startActivity(intent);
            }

            @Override
            public void truckModeChanged(boolean mode) {
                carMode = mode;
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
                return carMode;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public void createAndMoveRoom(String newRoomName) {
                loobyFragment.closeDialog();
                model.moveNewRoom(newRoomName);
                updateView();
                loobyFragment.movedToRoom(getCurrentRoomId());
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
                loobyFragment.movedToRoom(room.getId());
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
            returnToLogin(null);
        }

        @Override
            public void onBackPressed(){
                if(viewPager.getCurrentItem() == 2){
                    returnToLogin(null);
                }else{
                    viewPager.setCurrentItem(2);
                }
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
                        if (loobyFragment == null) {
                            loobyFragment = new LoobyFragment();
                        }
                        return loobyFragment;
                    } else if (arg == 1) {
                        Log.d("ConnectionActivity", " InRoom called");
                        if (inRoomFragment == null) {
                            inRoomFragment = new InRoomFragment();
                        }
                        return inRoomFragment;
                    }else if (arg == 2){
                        Log.d("ConnectionActivity"," Back called");
                        if(backFragment == null)
                            backFragment = new BackFragment();
                        return backFragment;
                    }
                    return null;
                }

                @Override
                public int getCount() {
                    return 3;
                }
            }
            private void crossFade(){
                viewPager.setVisibility(View.GONE);
                int aniTime = getResources().getInteger(
                        android.R.integer.config_longAnimTime);
                // Set the content view to 0% opacity but visible, so that it is visible
                // (but fully transparent) during the animation.
                viewPager.setAlpha(0f);
                viewPager.setVisibility(View.VISIBLE);

                // Animate the content view to 100% opacity, and clear any animation
                // listener set on the view.
                viewPager.animate()
                        .alpha(1f)
                        .setDuration(aniTime)
                        .setListener(null);

                // Animate the loading view to 0% opacity. After the ananimation ends,
                // set its visibility to GONE as an optimization step (it won't
                // participate in layout passes, etc.)
                loadingView.animate()
                        .alpha(0f)
                        .setDuration(aniTime)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                loadingView.setVisibility(View.GONE);
                            }
                        });
            }

    }