package se.chalmers.fleetspeak.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
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
    private SeekBar volumeControlBar;
    private SeekBar micControlBar;
    private PopupWindow micAndVolumePanel;

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
            case R.id.volume_mic_control:
                micAndVolumePanel.showAsDropDown(findViewById(android.R.id.home));
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
        Log.i("ChatroomActivity", "pushToTalkCalled");
        ImageButton button = (ImageButton) findViewById(R.id.pushToTalkButton);
        button.setImageResource(isTalkActive?getResources().getIdentifier("ic_mic.grey", "drawable", getPackageName()):getResources().getIdentifier("ic_mic.blue", "drawable", getPackageName()));
        isTalkActive = isTalkActive? false: true;
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

            imageView.setImageResource(R.drawable.ic_user_inroom);

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

    private void setUpVolumeAndMicControl(Context context, ImageButton imageButton ){
        LayoutInflater inflater = LayoutInflater.from(context);

        View contentView = inflater.inflate(R.layout.drop_down_seek_bar, null);
        volumeControlBar = (SeekBar) findViewById(R.id.volume_seekbar);
        micControlBar = (SeekBar) findViewById(R.id.mic_seekbar);

        micAndVolumePanel = new PopupWindow(context, null,
                android.R.attr.actionDropDownStyle);
        micAndVolumePanel.setFocusable(true); // seems to take care of dismissing on click outside
        micAndVolumePanel.setContentView(contentView);
        setPopupSize(micAndVolumePanel);
        final int paddigTop = getPaddingTop(micAndVolumePanel);
        if(imageButton == null)
            Log.i("Chatroomactivity", "imagebutton = null");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                micAndVolumePanel.showAsDropDown(view, 0 , -paddigTop);
            }
        });
    }
    private int getPaddingTop(PopupWindow popupWindow) {
        Drawable background = popupWindow.getBackground();
        if (background == null)
            return 0;

        Rect padding = new Rect();
        background.getPadding(padding);
        return padding.top;
    }
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
            width += rect.left + rect.right;
            height += rect.top + rect.bottom;
        }

        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
    }




}
