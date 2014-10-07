package se.chalmers.fleetspeak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * Created by TwiZ on 2014-09-26.
 * TODO: Make it possible to add/remove servers and update the list.
 */

public class BookmarkActivity extends ActionBarActivity {

    ListView serverView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        serverView = (ListView)findViewById(R.id.serverView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] demoServers = {"DemoServer1", "Server2"};

        ArrayAdapter<String> adapter = new BookmarkListAdapter(this,demoServers);
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
}
