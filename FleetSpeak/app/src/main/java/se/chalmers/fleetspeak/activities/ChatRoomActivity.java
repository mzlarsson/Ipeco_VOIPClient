package se.chalmers.fleetspeak.activities;

import android.content.ClipData;
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
import se.chalmers.fleetspeak.util.ThemeUtils;

/**
 * Created by TwiZ on 2014-10-06.
 */
public class ChatRoomActivity extends ActionBarActivity implements TruckStateListener, Commandable {

    ListView userListView;
    private SeekBar volumeControlBar;
    private SeekBar micControlBar;
    private PopupWindow micAndVolumePanel;
    private static TruckDataHandler truckDataHandler;
    User[] users;
    ArrayList<User> arrayUsers = new ArrayList<User>();
    private RoomHandler handler; //TODO: For test purposes
    private Messenger mService = null;
    ArrayAdapter<User> adapter;
    private boolean isTalkActive = false;
    private int currentRoomID;

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
        ThemeUtils.onCreateActivityCreateTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        CommandHandler.getInstance().addListener(this);
        bindService(new Intent(this,SocketService.class), mConnection, Context.BIND_AUTO_CREATE);


        //Shows the up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        currentRoomID = intent.getIntExtra("roomID", 0);
        initUserList(); //init userList

        initUserList(); //init userList

        userListView = (ListView) findViewById(R.id.userList);
        adapter = new ChatRoomListAdapter(this, arrayUsers);
        userListView.setAdapter(adapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos , long l) {
               User user = adapter.getItem(pos);
                if(user.getMuted()){
                    try {
                        mService.send(ServerHandler.unMuteUser(user.getId()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    user.setMuted(false);
                    adapter.notifyDataSetChanged();
                }else{
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
        truckModeChanged(TruckDataHandler.getInstance().getTruckMode());

    }


    /**
     * Gets the user list from the handler.
     * @return List of Users from the room with current room id.
     */
    private void initUserList(){
        users = CommandHandler.getUsers(currentRoomID);
        for(int i = 0; i < users.length; i++){
            arrayUsers.add(users[i]);
        }

    }


    private void updateUserList() {
        //TODO SERVER: When a user joins this room(currentRoomID) call this method
        users = CommandHandler.getUsers(currentRoomID);
        arrayUsers.clear();
        for(int i = 0; i < users.length; i++){
            arrayUsers.add(users[i]);
        }
        adapter.notifyDataSetChanged(); //Pokes the adapter to view the new changes
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

    /**
     * Changes the pushtoTalkButton icon based on isTalkActive
     * @param view
     */
    public void pushToTalk(View view) {
        Log.i("ChatroomActivity", "pushToTalkCalled");
        isTalkActive = !isTalkActive;
        ImageButton button = (ImageButton) findViewById(R.id.pushToTalkButton);
        button.setBackgroundResource(isTalkActive?R.drawable.ic_mic_blue:R.drawable.ic_mic_grey);

        if(isTalkActive){
            SoundController.unmute();
        }else{
            SoundController.mute();
        }
}

    @Override
    public void truckModeChanged(boolean mode) {
        findViewById(R.id.volume_mic_control).setVisibility(mode ? View.INVISIBLE: View.VISIBLE);
    }

    @Override
    public void onDataUpdate(String command) {
        if(command.equals("dataUpdate")){
            updateUserList();
        }
    }

    /**
     * Inner class, Adapter for the ChatRoom ListView
     */
    public class ChatRoomListAdapter extends ArrayAdapter<User> {

        public ChatRoomListAdapter(Context context, ArrayList<User> values) {
            super(context, R.layout.list_item_users, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(R.layout.list_item_users,parent, false);

            User user = getItem(position);
            String userName = user.getName();

            TextView textView = (TextView) view.findViewById(R.id.userName);
            ImageView imageView = (ImageView) view.findViewById(R.id.userTalkImage);


            /*
             * Sätter namnet i listan
             */
            textView.setText(userName);

            imageView.setImageResource(user.getMuted()?R.drawable.ic_mute:R.drawable.ic_user);

            return view;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chatroommenu, menu);
        ImageButton locButton = (ImageButton) menu.findItem(R.id.volume_mic_control).getActionView();
        setUpVolumeAndMicControl(this, locButton);
        ImageButton button = (ImageButton) findViewById(R.id.pushToTalkButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushToTalk(view);
            }
        });
        return true;
    }

    /**
     * Creates a dropdown panel with a volume and mic seekbars, which is activated by a imagebutton
     * @param context
     * @param imageButton
     */
    private void setUpVolumeAndMicControl(Context context, ImageButton imageButton ){
        LayoutInflater inflater = LayoutInflater.from(context);

        View contentView = inflater.inflate(R.layout.drop_down_seek_bar, null);
        volumeControlBar = (SeekBar) findViewById(R.id.volume_seekbar);
        micControlBar = (SeekBar) findViewById(R.id.mic_seekbar);

        micAndVolumePanel = new PopupWindow(context, null,
        android.R.attr.actionDropDownStyle);
        micAndVolumePanel.setFocusable(true);
        micAndVolumePanel.setContentView(contentView);
        setPopupSize(micAndVolumePanel);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                micAndVolumePanel.showAsDropDown(view, 0, 0);
            }
        });
        imageButton.setBackgroundResource(R.drawable.ic_control_mic_volume);

    }

    /**
     * Set the size of the popupWindow
     * @param popupWindow
     */
    private void setPopupSize(PopupWindow popupWindow) {
        View contentView = popupWindow.getContentView();

        int unspecified = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        contentView.measure(unspecified, unspecified);

        int width = contentView.getMeasuredWidth();
        int height = contentView.getMeasuredHeight();

        Drawable background = popupWindow.getBackground();
        if (background != null) {
            Rect rect = new Rect();
            background.getPadding(rect);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            width   = displaymetrics.widthPixels;
            height += rect.top + rect.bottom + 30;
        }
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
    }




}
