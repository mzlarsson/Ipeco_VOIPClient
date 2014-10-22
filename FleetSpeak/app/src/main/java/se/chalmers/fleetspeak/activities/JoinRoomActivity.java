package se.chalmers.fleetspeak.activities;

import android.app.ActivityManager;
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

import se.chalmers.fleetspeak.CommandHandler;
import se.chalmers.fleetspeak.Commandable;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.ServerHandler;
import se.chalmers.fleetspeak.SocketService;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.truck.TruckDataHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.ThemeUtils;

/**
 * Created by TwiZ on 2014-10-09.
 */
public class JoinRoomActivity extends ActionBarActivity implements TruckStateListener, Commandable {
    SharedPreferences prefs;
    private ListView roomView;
    private ArrayAdapter<Room> adapter;
    private Room[] rooms;
    private static TruckDataHandler truckDataHandler;

   // private RoomHandler handler;
    private boolean isDriving = false;
    private Messenger messenger = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            messenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messenger = null;
            Log.i("SERVICECONNECTION", "Disconnected");
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.onCreateActivityCreateTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        bindService(new Intent(this, SocketService.class),serviceConnection,Context.BIND_AUTO_CREATE);

        CommandHandler.addListener(this);

        roomView = (ListView)findViewById(R.id.roomView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rooms = CommandHandler.getRooms(); //Init roomslist

        adapter = new JoinRoomAdapter(this, rooms);
        roomView.setAdapter(adapter);

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
                unbindService(serviceConnection);
                //Join the choosen room
                joinRoom(roomID);

                //String example = room.getName();
                //String example = String.valueOf(adapterView.getItemAtPosition(position));
                //Toast.makeText(JoinRoomActivity.this, example, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void create_new_room_onClick(View view) {
       // Toast.makeText(JoinRoomActivity.this, "TODO: Join created room", Toast.LENGTH_SHORT).show(); //TODO: Remove this
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Choose a name for your room");
        final EditText input = new EditText(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //input.setHint(prefs.getString("Username", "username"));
        input.setHint(ThemeUtils.getUsername() + "'s room");
        if(!isDriving){
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
                        Toast.makeText(JoinRoomActivity.this, "Create a room with name: "+ newRoomName, Toast.LENGTH_SHORT).show();
                    } else {
                        newRoomName = input.getText().toString();
                        Toast.makeText(JoinRoomActivity.this, "Create a room with name: "+ newRoomName, Toast.LENGTH_SHORT).show();

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
                  //  Toast.makeText(JoinRoomActivity.this, "You didn't create a room", Toast.LENGTH_SHORT).show(); //TODO Remove this line when finished
                }
            });

            alertDialog.show();

        } else{ //When the car is driving and the user have selected "Create room" the user won't be
                //allowed to pick a name since it will take to much time.
            String newRoomName = (prefs.getString("Username", "username") + "'s room");
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

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.day_night_toggle:
                ThemeUtils.changeTheme(this);
                return true;
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
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
/*        for(int i = 0; i < adapter.getCount(); i++) {
            int id = adapter.getItem(i).getId();
            View view = findViewById(id).findViewById(R.id.list_item_users);
            view.setVisibility(mode?View.GONE:View.VISIBLE);
        }*/
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDataUpdate(String command) {
        if(command.equals("dataUpdate")){
            updateRoomList();
            updateUserList();
        } else if(command.startsWith("roomCreated")) {
            String[] s = command.split(",");
            int roomID = Integer.parseInt(s[1]);
            joinRoom(roomID);
        }
    }

    private void joinRoom(int roomID) {
        bindService(new Intent(this, SocketService.class),serviceConnection, Context.BIND_AUTO_CREATE);
        unbindService(serviceConnection);
        Intent intent = new Intent(JoinRoomActivity.this, ChatRoomActivity.class);
        intent.putExtra("roomID",roomID);
        startActivity(intent);
    }

    private void updateRoomList(){
        rooms = CommandHandler.getRooms();
        adapter.notifyDataSetChanged();
    }

    private void updateUserList() {
        //The adapter grabs the new userlist
        adapter.notifyDataSetChanged();
    }


    //Inner Class, The adapter for Bookmarks ListView
    public class JoinRoomAdapter extends ArrayAdapter<Room> {
        private Room[] rooms;

        public JoinRoomAdapter(Context context, Room[] values) {
            super(context, R.layout.list_item_rooms, values);
            rooms = values;
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
            if(isDriving) {                             //When driving, Show how many users are in each room.
                userText = ("(" + users.length + ")");
            } else {                                    //When vehicle is not moving, Show each users name in each room.
                for(int i = 0; i < users.length; i++){
                    if(i == 0) userText = users[i].getName();
                       else userText = userText + (", " + users[i].getName());
                }
            }
            userView.setText(userText);

            return view;
        }


    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause(){
        Log.i(this.getClass().toString(), "called onPause unbinding");
        super.onPause();
        if(this.isMyServiceRunning(ServiceConnection.class)) {
            unbindService(serviceConnection);
        }
    }
    @Override
    protected void onStop(){
        Log.i(this.getClass().toString(), "called onStop unbinding");
        super.onStop();
        if(this.isMyServiceRunning(ServiceConnection.class)) {
            unbindService(serviceConnection);
        }
    }
    @Override
    protected void onDestroy(){
        Log.i(this.getClass().toString(), "called onDestroy unbinding" );
        super.onDestroy();
        if(this.isMyServiceRunning(ServiceConnection.class)) {
            unbindService(serviceConnection);
        }

    }
    @Override
    protected void onResume(){
        Log.i(this.getClass().toString(), "called onResume binding");
        bindService(new Intent(this, SocketService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();

    }
    @Override
    protected void onRestart(){
        Log.i(this.getClass().toString(), "called onRestart binding");
        super.onRestart();
        bindService(new Intent(this, SocketService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }
}

