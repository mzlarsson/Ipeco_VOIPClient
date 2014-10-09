package se.chalmers.fleetspeak.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
    ArrayList<String> listItems = new ArrayList<String>();
    User[] userList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        userListView = (ListView) findViewById(R.id.userList);

        listItems.add("Olle");

        adapter = new ChatRoomListAdapter(this, listItems);
        //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        userListView.setAdapter(adapter);

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

    //For test purposes
    public void addUserDebug(View view) {
        addUserToList(view, "PelleID");
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

            textView.setText(user);

            imageView.setImageResource(R.drawable.ic_inactive_status);

            return view;
        }
    }
}
