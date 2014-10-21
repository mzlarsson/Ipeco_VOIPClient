package se.chalmers.fleetspeak.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
 * Created by TwiZ on 2014-09-26.
 * TODO: Make it possible to add/remove servers and onDataUpdate the list.
 */

public class BookmarkActivity extends ActionBarActivity {

    ListView serverView;
    ArrayList<String> listItems = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        serverView = (ListView)findViewById(R.id.serverView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listItems.add("DemoServer1");
        listItems.add("DemoServer2");
        //String[] demoServers = {"DemoServer1", "Server2"};

        ArrayAdapter<String> adapter = new BookmarkListAdapter(this, listItems);
        serverView.setAdapter(adapter);

        serverView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String example = String.valueOf(adapterView.getItemAtPosition(position)  );

                Toast.makeText(BookmarkActivity.this, example, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmarkmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_server) {
            Intent getNewServerIntent = new Intent(this, NewServerActivity.class);
            startActivity(getNewServerIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Inner Class, The adapter for Bookmarks ListView
    public class BookmarkListAdapter extends ArrayAdapter<String> {

        public BookmarkListAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.list_item_servers, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(R.layout.list_item_servers,parent, false);

            String server = getItem(position);

            TextView textView = (TextView) view.findViewById(R.id.serverName);
            ImageView imageView = (ImageView) view.findViewById(R.id.serverImage);

            textView.setText(server);
            imageView.setImageResource(R.drawable.ic_server);

            return view;
        }
    }

}
