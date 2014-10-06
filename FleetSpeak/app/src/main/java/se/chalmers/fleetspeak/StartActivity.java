package se.chalmers.fleetspeak;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends ActionBarActivity {
    private EditText ipTextField;
    private EditText portTextField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        new TruckCommunicator().execute(AutomotiveSignalId.FMS_WHEEL_BASED_SPEED, AutomotiveSignalId.FMS_SELECTED_GEAR);
        ipTextField = (EditText) findViewById(R.id.ipField);
        portTextField = (EditText) findViewById(R.id.portField);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToBookmarks(View view) {
        Intent getBookmarkIntent = new Intent(this, BookmarkActivity.class);
        startActivity(getBookmarkIntent);
    }

    public void onConnectButtonClick(View view) {

        String ipAdress = String.valueOf(ipTextField.getText());
        int portNumber = Integer.parseInt(String.valueOf(portTextField.getText()));
        Toast.makeText(this, ipAdress + portNumber , Toast.LENGTH_SHORT).show();
        // Connector.connect(ipAdress, portNumber);s
    }
}
