package se.chalmers.fleetspeak;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import se.chalmers.fleetspeak.sound.SoundController;

public class StartActivity extends ActionBarActivity {

    private EditText ipTextField;
    private EditText portTextField;
    private EditText userNameTextField;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private CheckBox savePrefs;

    private boolean isConnected;

    private SoundController soundController;

    Messenger mService = null;
    final Messenger mMessenger = new Messenger(new CommandHandler());

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, SocketService.SETMESSENGER, mMessenger);
                mService.send(msg);
            }catch (RemoteException e){

            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            soundController.close();
            Log.i("SERVICECONNECTION", "Disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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

        ipTextField.setInputType(InputType.TYPE_CLASS_PHONE);

        savePrefs = (CheckBox) findViewById(R.id.saveUserPref);

        bindService(new Intent(this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
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

        //Connector.connect(ipAdress, portNumber);
        if(savePrefs.isChecked()){
            saveUsername(view);
        }

        startConnection(ipAdress, portNumber);

        Intent intent = new Intent(this,ChatRoomActivity.class);
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

    public void startConnection(String ip, int port){
        if (!isConnected) {
            try {
                Message msg = Message.obtain(null, SocketService.CONNECT, ip);
                msg.replyTo = mMessenger;
                mService.send(msg);
                isConnected = true;
            } catch (RemoteException e) {
            }
        }else{
            Log.i("Hej","hej");
            try {
                Message msg = Message.obtain(null, SocketService.SENDDATA);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            }
        }

        int rtpPort = port+1;
        soundController = SoundController.create(this, ip, rtpPort);
    }
}
