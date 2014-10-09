package se.chalmers.fleetspeak.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
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

import se.chalmers.fleetspeak.R;

/**
 * Created by TwiZ on 2014-10-09.
 */
public class JoinRoomActivity extends ActionBarActivity {

    ListView roomView;
    ArrayList<String> listItems = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        roomView = (ListView)findViewById(R.id.roomView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listItems.add("Room 1");
        listItems.add("Room 2");
        //String[] demoServers = {"DemoServer1", "Server2"};

        ArrayAdapter<String> adapter = new JoinRoomAdapter(this, listItems);
        roomView.setAdapter(adapter);

        roomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String example = String.valueOf(adapterView.getItemAtPosition(position)  );

                Toast.makeText(JoinRoomActivity.this, example, Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void create_new_room_onClick(View view) {
        Intent intent = new Intent(this,ChatRoomActivity.class);
        startActivity(intent);
    }

    //Inner Class, The adapter for Bookmarks ListView
    public class JoinRoomAdapter extends ArrayAdapter<String> {

        public JoinRoomAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.list_item_rooms, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(R.layout.list_item_rooms, parent, false);

            String whatRoom = getItem(position);

            TextView textView = (TextView) view.findViewById(R.id.roomName);
            ImageView imageView = (ImageView) view.findViewById(R.id.roomIcon);

            textView.setText(whatRoom);
            imageView.setImageResource(R.drawable.ic_room);

            return view;
        }
    }

}

