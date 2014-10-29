package se.chalmers.fleetspeak.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import se.chalmers.fleetspeak.CommandHandler;
import se.chalmers.fleetspeak.Commandable;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.RoomHandler;
import se.chalmers.fleetspeak.ServerHandler;
import se.chalmers.fleetspeak.SocketService;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.sound.SoundController;
import se.chalmers.fleetspeak.truck.TruckDataHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.ServiceUtil;
import se.chalmers.fleetspeak.util.Utils;

/**
 * A activity that present the selected chatroom and room specific options to the Users and enables the users
 * to do room specific actions
 * Created by Johan Segerlund on 2014-10-06.
 */
public class ChatRoomActivity extends ActionBarActivity implements TruckStateListener, Commandable {

    ListView userListView;
    private Menu menu;
    private PopupWindow micAndVolumePanel;
    private static TruckDataHandler truckDataHandler;
    private User[] users;
    private ArrayList<User> arrayUsers = new ArrayList<User>();
    private Messenger mService = null;
    private ArrayAdapter<User> adapter;
    private boolean isTalkActive = false;
    public boolean isDriving = false;
    private int currentRoomID;
    /**
     * Sets up the connection service to the server
     */
     private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i("SERVICECONNECTION", "service started");
            mService = new Messenger(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            Log.i("SERVICECONNECTION", "Disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onCreateActivityCreateTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        CommandHandler.getInstance().addListener(this);
        bindService(new Intent(this,SocketService.class), mConnection, Context.BIND_AUTO_CREATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        currentRoomID = intent.getIntExtra("roomID", 0);
        initUserList();

        userListView = (ListView) findViewById(R.id.userList);
        adapter = new ChatRoomListAdapter(this, arrayUsers);
        userListView.setAdapter(adapter);
        truckModeChanged(truckDataHandler.getInstance().getTruckMode());
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                User user = adapter.getItem(pos);
                if (user.getMuted()) {
                    try {
                        mService.send(ServerHandler.unMuteUser(user.getId()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    user.setMuted(false);
                    adapter.notifyDataSetChanged();
                } else {
                    try {
                        mService.send(ServerHandler.muteUser(user.getId()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    user.setMuted(true);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        Log.i("Chat", SoundController.hasValue() + "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                unbindService(mConnection);
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        this.menu = menu;
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chatroommenu, menu);
        ImageButton locButton = (ImageButton) menu.findItem(R.id.volume_mic_control).getActionView();
        setUpVolumeAndMicControl(this, locButton);
        ImageButton button = (ImageButton) findViewById(R.id.pushToTalkButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushToTalk();
            }
        });
        return true;
    }

    /**
     * Gets the user list from the handler.
     * Moves the users to an arraylist which will be used by the adapter.
     */
    private void initUserList(){
        users = CommandHandler.getUsers(currentRoomID);
        Log.i("Users size from server", String.valueOf(users.length));
        for(int i = 0; i < users.length; i++){
            arrayUsers.add(users[i]);
        }
    }

    /**
     * A method to update the userlist
     */
    private void updateUserList() {
        users = CommandHandler.getUsers(currentRoomID);
        arrayUsers.clear();
        for(int i = 0; i < users.length; i++){
            arrayUsers.add(users[i]);
        }
        adapter.notifyDataSetChanged(); //Pokes the adapter to view the new changes
    }

    /**
     * A method that toggles between enables or disables the michophone and shows the current mode in the view
     */
    public void pushToTalk() {
        Log.i("ChatroomActivity", "pushToTalkCalled");
        isTalkActive = !isTalkActive;
        ImageButton button = (ImageButton) findViewById(R.id.pushToTalkButton);
        button.setBackgroundResource(isTalkActive?R.drawable.ic_mic_blue:R.drawable.ic_mic_grey);
        SoundController.mute(!isTalkActive);
    }

    @Override
    public void truckModeChanged(boolean mode) {
        isDriving = mode;
        adapter.notifyDataSetChanged();
        if (menu != null) {
            MenuItem item = menu.findItem(R.id.volume_mic_control);
            item.setVisible(!mode);
        }
    }

    @Override
    public void onDataUpdate(String command) {
        if(command.equals("dataUpdate")){
            updateUserList();
        }
    }

    @Override
    protected void onPause(){
        Log.i("ChatroomActivity", "called onPause unbinding");
        super.onPause();
        CommandHandler.removeListener(this);
        if(ServiceUtil.isMyServiceRunning(this, ServiceConnection.class)) {
            unbindService(mConnection);
        }
    }
    @Override
    protected void onStop(){
        CommandHandler.removeListener(this);
        Log.i("ChatroomActivity", "called onStop unbinding");
        super.onStop();
        if(ServiceUtil.isMyServiceRunning(this, ServiceConnection.class)) {
            unbindService(mConnection);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("CHATROOMACTIVITY", "called onDestroy unbinding");
        CommandHandler.removeListener(this);

    }

    /**
     * A method to creates the dropdown panel with volume and mic control which is activated by a ImageButton
     * @param context - the context of the dropdown panel
     * @param imageButton - the ImageButton that activates the dropdown panel
     */
    private void setUpVolumeAndMicControl(Context context, ImageButton imageButton ){
        //Creates the view of the PopUpWindow
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.drop_down_seek_bar, null);
        // Set up the volume seekbar to control the volume
        setUpVolumeSeekbar((SeekBar)contentView.findViewById(R.id.volume_seekbar));
        // Creates the PopUpWindow and sets the size and view
        micAndVolumePanel = new PopupWindow(context, null,
        android.R.attr.actionDropDownStyle);
        micAndVolumePanel.setFocusable(true);
        micAndVolumePanel.setContentView(contentView);
        setPopupSize(micAndVolumePanel);

        // Set up the activation of the panel to a image button
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the place where the PopUpWindow appears
                micAndVolumePanel.showAsDropDown(view, 0, 0);
            }
        });
        // Set the image view of the imageButton
        imageButton.setBackgroundResource(R.drawable.ic_control_mic_volume);

    }

    /**
     * A method to set up a seekbar to control the volume
     * @param seekbar
     */
    private void setUpVolumeSeekbar(SeekBar seekbar){
        // Check for -1 return since the get-methods return -1 when volume control is not accessible
        if(SoundController.getMaxVolume() != -1) {
            seekbar.setMax(SoundController.getMaxVolume());
            Log.i("VolumeAudio: ", "max volume =" + SoundController.getMaxVolume() );
        }
        if(SoundController.getCurrentVolume() != -1){
            Log.i("VolumeAudio: ", "current volume="+ SoundController.getCurrentVolume());
            seekbar.setProgress(SoundController.getCurrentVolume());
        }
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                SoundController.setVoloume(progress);
                Log.i("Volume value:", " " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    /**
     * Set the size of the popupWindow
     * @param popupWindow - the popupWindow that will have the size set
     */
    private void setPopupSize(PopupWindow popupWindow) {

        // Set the height of the popupWindow to be the measured height of its contentView with background padding
        View contentView = popupWindow.getContentView();
        int unspecified = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        contentView.measure(unspecified, unspecified);
        int height = contentView.getMeasuredHeight();
        Drawable background = popupWindow.getBackground();
        if (background != null) {
            Rect rect = new Rect();
            background.getPadding(rect);
            height += rect.top + rect.bottom;
        }

        // Set the width of the popupWindow to be the width of the android display
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
    }

    /**
     * Inner class, Adapter for the ChatRoom ListView
     * The adapter handles all the items in the listView
     */
    public class ChatRoomListAdapter extends ArrayAdapter<User> {
        public ChatRoomListAdapter(Context context, ArrayList<User> values) {
            super(context, R.layout.list_item_users, values);
            Log.i("ChatRoom Userlist size: ", String.valueOf(values.size()));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(isDriving?R.layout.list_item_users_while_driving:
                    R.layout.list_item_users, parent, false);

            User user = getItem(position);
            String userName = user.getName();

            TextView textView = (TextView) view.findViewById(R.id.userName);
            ImageView imageView = (ImageView) view.findViewById(R.id.userTalkImage);

            textView.setText(userName); //Sets the names in the list

            imageView.setImageResource(user.getMuted()?R.drawable.ic_mute:R.drawable.ic_user); //Sets icon in the list

            return view;
        }
    }

}
