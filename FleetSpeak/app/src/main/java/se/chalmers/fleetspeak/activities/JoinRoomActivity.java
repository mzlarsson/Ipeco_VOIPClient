package se.chalmers.fleetspeak.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.CommandHandler;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.RoomHandler;
import se.chalmers.fleetspeak.SocketService;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.truck.TruckDataHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;

/**
 * Created by TwiZ on 2014-10-09.
 */
public class JoinRoomActivity extends ActionBarActivity implements TruckStateListener {

    ListView roomView;
    ArrayAdapter<Room> adapter;
    Room[] rooms;
    private static TruckDataHandler truckDataHandler;

    public RoomHandler handler;

    private Room room1;
    private Room room2;
    private User user1;
    private User user2;
    private User user0;

    private Messenger messenger;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        bindService(new Intent(this, SocketService.class),serviceConnection,Context.BIND_AUTO_CREATE);
        


        roomView = (ListView)findViewById(R.id.roomView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateRoomList(); //Init roomList


        adapter = new JoinRoomAdapter(this, rooms);
        roomView.setAdapter(adapter);

        roomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //Checks which room the user choose to join.
                Object object = adapterView.getItemAtPosition(position);
                Room room = (Room)object;
                int roomID = room.getId();

                //TODO: Tell the server user want to join selected roomID

                //Join the choosen room
                Intent intent = new Intent(JoinRoomActivity.this, ChatRoomActivity.class);
                intent.putExtra("roomID",roomID);
                startActivity(intent);

                //String example = room.getName();
                //String example = String.valueOf(adapterView.getItemAtPosition(position));
                //Toast.makeText(JoinRoomActivity.this, example, Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void updateRoomList() {
        createSimulatedHandler(); //TODO: For test purposes. Simulerar en handler
        //TODO: get the rooms from the real handler handler
        rooms = handler.getRooms();

    }

    private void createSimulatedHandler() { //TODO: Simulerar en handler
        handler = new RoomHandler();
        room1 = new Room("Rum1", 11);
        room2 = new Room("Rum2", 22);
        user0 = new User("User0", 0);
        user1 = new User("User1", 1);
        user2 = new User("User2",2);
        handler.addUser(user1, room1);
        handler.addUser(user2, room2);
    }

    public void create_new_room_onClick(View view) {
        Toast.makeText(JoinRoomActivity.this, "Creating a room", Toast.LENGTH_SHORT).show();
        //TODO:
    //    Intent intent = new Intent(this,ChatRoomActivity.class);
    //    startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void truckModeChanged(boolean mode) {
        // Makes the users text list Gone or Visible depending on Car Mode
        for(int i = 0; i < adapter.getCount(); i++) {
            int id = adapter.getItem(i).getId();
            View view = findViewById(id).findViewById(R.id.list_item_users);
            view.setVisibility(mode?View.GONE:View.VISIBLE);

        }
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

            View view = inflater.inflate(R.layout.list_item_rooms, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.roomName);
            ImageView imageView = (ImageView) view.findViewById(R.id.roomIcon);

            String whatRoom = getItem(position).getName();

            textView.setText(whatRoom);
            imageView.setImageResource(R.drawable.ic_room);

            return view;
        }


    }
}

