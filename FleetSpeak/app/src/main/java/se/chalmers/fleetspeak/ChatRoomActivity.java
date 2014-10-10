package se.chalmers.fleetspeak;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by TwiZ on 2014-10-06.
 */
public class ChatRoomActivity extends ActionBarActivity {

    ListView userListView;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        userListView = (ListView)findViewById(R.id.userList);

        listItems.add("Olle");

        adapter = new ChatRoomListAdapter(this, listItems);
        //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        userListView.setAdapter(adapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String examplePersonText = String.valueOf(adapterView.getItemAtPosition(position)  );
                Toast.makeText(ChatRoomActivity.this, examplePersonText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToList(View v,String idPerson) {
        listItems.add(idPerson);
        adapter.notifyDataSetChanged();
    }


    private void removeUserFromList(View v, String idUser) {
        for(int i = 0; i < listItems.size(); i++){
            if(listItems.get(i).equals(idUser)){
                listItems.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void subUserDebug(View view) {
        removeUserFromList(view, "PelleID");
    }

    public void addUserDebug(View view) {
        addUserToList(view, "PelleID");
    }
}
