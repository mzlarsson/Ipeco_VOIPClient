package se.chalmers.fleetspeak.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;

import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.chalmers.fleetspeak.CommandHandler;
import se.chalmers.fleetspeak.Commandable;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.ServerHandler;
import se.chalmers.fleetspeak.SocketService;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.sound.SoundController;
import se.chalmers.fleetspeak.truck.TruckDataHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.ServiceUtil;
import se.chalmers.fleetspeak.util.Utils;

/**
 * A activity that guides the user through choosing or creating a room to join
 *
 * Created by Johan Segerlund on 2014-10-09.
 */
public class JoinRoomActivity extends ActionBarActivity implements TruckStateListener, Commandable {
    private static TruckDataHandler truckDataHandler;
    private Menu menu;
    private ListView roomView;
    private ArrayAdapter<Room> adapter;
    private Room[] rooms;
    private ArrayList ArrayRooms = new ArrayList<Room>();
    private boolean isDriving = false;
    private Messenger messenger = null;

    /**
     * Set up the connection service to the server
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            messenger = new Messenger(service);
            Log.i("SERVICECONNECTION", "Service connected to JoinRoomActivity");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messenger = null;
            Log.i("SERVICECONNECTION", "Disconnected");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onCreateActivityCreateTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        if(ServiceUtil.isMyServiceRunning(this, ServiceConnection.class)) {
            bindService(new Intent(this, SocketService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }

        CommandHandler.addListener(this);

        roomView = (ListView)findViewById(R.id.roomView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rooms = CommandHandler.getRooms();
        for(int i = 0; i < rooms.length; i++) {
            ArrayRooms.add(rooms[i]);
        }
        adapter = new JoinRoomAdapter(this, ArrayRooms);
        roomView.setAdapter(adapter);
        truckModeChanged(TruckDataHandler.getInstance().getTruckMode());
        roomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Checks which room the user choose to join.
                Object object = adapterView.getItemAtPosition(position);
                Room room = (Room)object;
                int roomID = room.getId();

                try {
                    messenger.send(Message.obtain(ServerHandler.move(roomID)));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                joinRoom(roomID);
            }
        });
        Log.i("join", SoundController.hasValue() + "");
    }

    /**
     * Creates a new room on a click action
     * @param view
     */
    public void createNewRoomOnClick(View view) {
        // If the user is not driving create a dialog that promts the user to select a room name and
        // create a room with that name if the user don't put in a room name default to the users name + "'s room"
        if(!isDriving){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Choose a name for your room");
            final EditText input = new EditText(this);
            input.setHint(Utils.getUsername() + "'s room");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newRoomName;
                    if(input.getText().length() == 0){
                        newRoomName = input.getHint().toString();
                    } else {
                        newRoomName = input.getText().toString();
                    }
                    try {
                        messenger.send(ServerHandler.createAndMove(newRoomName));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        } else{ //When the car is driving and the user have selected "Create room" the user won't be
                //allowed to pick a name since it will take to much time.
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String newRoomName = (Utils.getUsername() + "'s room");
            try {
                messenger.send(ServerHandler.createAndMove(newRoomName));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.day_night_menu, menu);
        this.menu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.day_night_toggle:
                Utils.changeTheme(this);
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this,StartActivity.class);
                startActivity(intent);
                try {
                    messenger.send(Message.obtain(ServerHandler.disconnect()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                unbindService(serviceConnection);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void truckModeChanged(boolean mode) {
        // Makes the users text list Gone or Visible depending on Car Mode
        isDriving = mode;
        adapter.notifyDataSetChanged();
        if(menu!=null){
            MenuItem item = menu.findItem(R.id.day_night_toggle);
            item.setVisible(!mode);
        }
    }

    @Override
    public void onDataUpdate(String command) {
        if(command.equals("dataUpdate")){
            updateRoomList();
        } else if(command.startsWith("roomCreated")) {
            String[] s = command.split(",");
            int roomID = Integer.parseInt(s[1]);
            joinRoom(roomID);
            updateRoomList();
        } else if(command.equals("Disconnected")){
            Intent intent = new Intent(this,StartActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Join a specific room
     * @param roomID - the ID of the room to be joined
     */
    private void joinRoom(int roomID) {
        bindService(new Intent(this, SocketService.class),serviceConnection, Context.BIND_AUTO_CREATE);
        unbindService(serviceConnection);
        Intent intent = new Intent(JoinRoomActivity.this, ChatRoomActivity.class);
        intent.putExtra("roomID",roomID);
        startActivity(intent);
    }

    /**
     * Update the list of rooms displayed
     */
    private void updateRoomList(){
        Log.i("JoinRoomActivity", "updateing room list");
        rooms = CommandHandler.getRooms();
        ArrayRooms.clear();
        for(int i = 0; i < rooms.length; i++){
            ArrayRooms.add(rooms[i]);
        }
        adapter.notifyDataSetChanged();
        getWindow().getDecorView().findViewById(android.R.id.content).getRootView().invalidate();
    }

    /**
     * Inner class, Adapter for the Room's ListView
     * The adapter handles all the items in the ListView
     */
    public class JoinRoomAdapter extends ArrayAdapter<Room> {
        public JoinRoomAdapter(Context context, ArrayList<Room> values) {
            super(context, R.layout.list_item_rooms, values);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(isDriving?R.layout.list_item_rooms_while_driving:
                                                   R.layout.list_item_rooms, parent, false);

            TextView roomView = (TextView) view.findViewById(R.id.roomName);
            ImageView imageView = (ImageView) view.findViewById(R.id.roomIcon);
            TextView userView = (TextView) view.findViewById(R.id.list_item_users);

            String whatRoom = getItem(position).getName();

            roomView.setText(whatRoom);
            imageView.setImageResource(R.drawable.ic_room);

            User[] users =  CommandHandler.getUsers(getItem(position).getId());
            String userText = "";
            if(users != null) {
                if (isDriving) {                             //When driving, Show how many users are in each room.
                    userText = ("(" + users.length + ")");
                } else {                                    //When vehicle is not moving, Show each users name in each room.
                    for (int i = 0; i < users.length; i++) {
                        if (i == 0) userText = users[i].getName();
                        else userText = userText + (", " + users[i].getName());
                    }
                }
            }
            userView.setText(userText);

            return view;
        }
    }

    @Override
    protected void onPause(){
        Log.i("JOINROOMACTIVITY", "called onPause unbinding");
        super.onPause();
        if(ServiceUtil.isMyServiceRunning(this, ServiceConnection.class)) {
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onStop(){
        Log.i("JOINROOMACTIVITY", "called onStop unbinding");
        super.onStop();
        if(ServiceUtil.isMyServiceRunning(this, ServiceConnection.class)) {
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("JOINROOMACTIVITY", "called onDestroy unbinding");
        CommandHandler.removeListener(this);
    }

    @Override
    protected void onResume(){
        Log.i("JOINROOMACTIVITY", "called onResume binding");
        bindService(new Intent(this, SocketService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onRestart(){
        Log.i("JOINROOMACTIVITY", "called onRestart binding");
        super.onRestart();
        bindService(new Intent(this, SocketService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this,StartActivity.class);
        startActivity(intent);
        try {
            messenger.send(Message.obtain(ServerHandler.disconnect()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(serviceConnection);
    }
}

