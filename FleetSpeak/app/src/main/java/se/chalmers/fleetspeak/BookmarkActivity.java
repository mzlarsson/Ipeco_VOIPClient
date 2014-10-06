package se.chalmers.fleetspeak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * Created by TwiZ on 2014-09-26.
 */

public class BookmarkActivity extends ActionBarActivity {

    ListView serverView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        serverView = (ListView)findViewById(R.id.serverView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] demoServers = {"Server1", "Server2"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, demoServers);
        serverView.setAdapter(adapter);
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
