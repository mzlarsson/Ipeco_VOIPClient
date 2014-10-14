package se.chalmers.fleetspeak.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
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
import se.chalmers.fleetspeak.User;

/**
 * Created by TwiZ on 2014-10-06.
 */
public class ChatRoomActivity extends ActionBarActivity {

    ListView userListView;
    private SeekBar volumeControlBar;
    private SeekBar micControlBar;
    private PopupWindow micAndVolumePanel;

    /*
    * Lista som innehåller alla users i ett rum
    * */
    ArrayList<String> listItems = new ArrayList<String>();

    ArrayAdapter<String> adapter;
    private boolean isTalkActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userListView = (ListView) findViewById(R.id.userList);

        /*Ladda listan med users*/
        listItems.add("Olle");

        adapter = new ChatRoomListAdapter(this, listItems);
        //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        userListView.setAdapter(adapter);

        /*
        * Vad som ska hända när man klicka på en user
        * */
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String examplePersonText = String.valueOf(adapterView.getItemAtPosition(position));
                Toast.makeText(ChatRoomActivity.this, examplePersonText, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addUserToList(View v, String idUser) {
        listItems.add(idUser);
        adapter.notifyDataSetChanged();
    }


    private void removeUserFromList(View v, String idUser) {
        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).equals(idUser)) {
                listItems.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    //Button action, For test purposes
    public void subUserDebug(View view) {
        removeUserFromList(view, "PelleID");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.volume_mic_control:
                micAndVolumePanel.showAsDropDown(getActionBar().getCustomView());
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //For test purposes
    public void addUserDebug(View view) {
        addUserToList(view, "PelleID");
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

    //Inner class, Adapter for the ChatRoom ListView
    public class ChatRoomListAdapter extends ArrayAdapter<String> {

        public ChatRoomListAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.list_item_users, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(R.layout.list_item_users,parent, false);

            String user = getItem(position);

            TextView textView = (TextView) view.findViewById(R.id.userName);
            ImageView imageView = (ImageView) view.findViewById(R.id.userTalkImage);

            /*
             * Sätter namnet i listan
             */
            textView.setText(user);

            /*
            * För att ändra icon när någon pratar måste vi uppdatera adaptern varje gång.
            * Adapter får då kolla om någon håller på och prata och på så vis väljer den vilken
            * icon som ska sättas.
            * */
            imageView.setImageResource(R.drawable.ic_inactive_status);

            return view;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.chatroommenu, menu);
        setUppVolumeMicDropDown(this);
        return true;

    }

    /**
     * Sets up two seekbar in a popupwindow to volume and mic seekbar
     * @param context
     */
    private void setUppVolumeMicDropDown(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.drop_down_2seekbar, null);
        volumeControlBar = (SeekBar) contentView.findViewById(R.id.first_seekbar);
        micControlBar = (SeekBar) contentView.findViewById(R.id.second_seekbar);
        micAndVolumePanel = new PopupWindow(context, null, android.R.attr.actionDropDownStyle);
        micAndVolumePanel.setFocusable(true);
        micAndVolumePanel.setContentView(contentView);
        setPopupSize(micAndVolumePanel);
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
