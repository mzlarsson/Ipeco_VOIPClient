package se.chalmers.fleetspeak.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.chalmers.fleetspeak.IUserHandler;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.RoomHandler;
import se.chalmers.fleetspeak.User;

/**
 * Created by TwiZ on 2014-10-06.
 */
public class ChatRoomActivity extends ActionBarActivity {

    ListView userListView;

    User[] users;
    private RoomHandler handler; //TODO: For test purposes

    ArrayAdapter<User> adapter;
    private boolean isTalkActive = false;
    private int currentRoomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        currentRoomID = intent.getIntExtra("roomID", 0);

        createSimulatedHandler(); //TODO: For Test purposes
        users = getUserList(); //init userList

        userListView = (ListView) findViewById(R.id.userList);
        adapter = new ChatRoomListAdapter(this, users);
        userListView.setAdapter(adapter);

        /*
        * Vad som ska hända när man klicka på en user
        * */

 /*         userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String examplePersonText = String.valueOf(adapterView.getItemAtPosition(position));
                Toast.makeText(ChatRoomActivity.this, examplePersonText + " "+currentRoomID, Toast.LENGTH_SHORT).show();
            }
        });

*/
    }

    private void createSimulatedHandler() { //TODO: For test purposes. Simulerar en handler
        handler = new RoomHandler();
        Room room1 = new Room("Rum1", 11);
        Room room2 = new Room("Rum2", 22);
        User user0 = new User("User0", 0);
        User user1 = new User("User1", 1);
        User user2 = new User("User2",2);
        handler.addUser(user1, room1);
        handler.addUser(user2, room2);
    }

    /**
     * Gets the user list from the handler.
     * @return List of Users from the room with current room id.
     */
    private User[] getUserList(){
        //TODO: Get users from the real handler.
        return handler.getUsers(currentRoomID);
    }


    private void updateUserList() {
        //TODO: When a user joins this room(currentRoomID) call this method
        //users = TODO: Grab the new changes or load the new list.
        adapter.notifyDataSetChanged(); //Pokes the adapter to view the new changes
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
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
        ImageButton button = (ImageButton) findViewById(R.id.pushToTalkButton);
        if(isTalkActive){
            button.setImageResource(getResources().getIdentifier("ic_mic.grey", "drawable", getPackageName()));
            isTalkActive = false;
        }
        else{
            button.setImageResource(getResources().getIdentifier("ic_mic.blue", "drawable", getPackageName()));
            isTalkActive = true;
        }
    }

    /**
     * Inner class, Adapter for the ChatRoom ListView
     */
    public class ChatRoomListAdapter extends ArrayAdapter<User> {

        public ChatRoomListAdapter(Context context, User[] values) {
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

            /*
            * För att ändra icon när någon pratar måste vi uppdatera adaptern varje gång.
            * Adapter får då kolla om någon håller på och prata och på så vis väljer den vilken
            * icon som ska sättas.
            * */
            imageView.setImageResource(R.drawable.ic_inactive_status);

            return view;
        }
    }
}
