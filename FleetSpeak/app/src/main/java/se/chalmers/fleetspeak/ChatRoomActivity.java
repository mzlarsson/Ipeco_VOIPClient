package se.chalmers.fleetspeak;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by TwiZ on 2014-10-06.
 *  TODO: Make it possible to add/remove users and update the list.
 */
public class ChatRoomActivity extends ActionBarActivity {


    ListView userListView;
    Button debugg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        userListView = (ListView)findViewById(R.id.userList);

        /*Users that is inside the room */

        String[] demoUsers = {"User 1", "User2" ,"User3", "User4"};

        ArrayAdapter<String> adapter = new ChatRoomListAdapter(this, demoUsers);
        userListView.setAdapter(adapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String examplePersonText = String.valueOf(adapterView.getItemAtPosition(position)  );
                Toast.makeText(ChatRoomActivity.this, examplePersonText, Toast.LENGTH_SHORT).show();
            }
        });
    }



}
