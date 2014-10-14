package se.chalmers.fleetspeak.activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.RoomHandler;
import se.chalmers.fleetspeak.TruckCommunicator;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.activities.BookmarkActivity;
import se.chalmers.fleetspeak.activities.ChatRoomActivity;

public class StartActivity extends ActionBarActivity {
    private EditText ipTextField;
    private EditText portTextField;
    private EditText userNameTextField;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private CheckBox savePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new TruckCommunicator().execute(AutomotiveSignalId.FMS_WHEEL_BASED_SPEED, AutomotiveSignalId.FMS_SELECTED_GEAR);

        ipTextField = (EditText) findViewById(R.id.ipField);
        portTextField = (EditText) findViewById(R.id.portField);
        userNameTextField = (EditText) findViewById(R.id.usernameField);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = prefs.edit();

        String portNumber = prefs.getString("portNumber", "portNumber");
        String username = prefs.getString("username", "username");
        String ipAdress = prefs.getString("ipAdress", "ipAdress");

        userNameTextField.setText(username);
        portTextField.setText(portNumber);
        ipTextField.setText(ipAdress);
        savePrefs = (CheckBox) findViewById(R.id.saveUserPref);

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
/*
        String ipAdress = String.valueOf(ipTextField.getText());
        int portNumber = Integer.parseInt(String.valueOf(portTextField.getText()));

        //Connector.connect(ipAdress, portNumber);
        if(savePrefs.isChecked()){
            saveUsername(view);
        }*/
        Intent intent = new Intent(this,JoinRoomActivity.class);
        startActivity(intent);
    }

    public void saveUsername(View view){
        String newUsername = String.valueOf(userNameTextField.getText());
        String newIpAdress = String.valueOf(ipTextField.getText());
        String newPortNumber = String.valueOf(portTextField.getText());
        prefEdit.putString("username", newUsername);
        prefEdit.putString("ipAdress", newIpAdress );
        prefEdit.putString("portNumber", newPortNumber);
    }
}
