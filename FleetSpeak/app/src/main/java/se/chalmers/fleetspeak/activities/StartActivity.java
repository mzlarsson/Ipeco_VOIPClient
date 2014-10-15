package se.chalmers.fleetspeak.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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


import se.chalmers.fleetspeak.CommandHandler;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.SocketService;
import se.chalmers.fleetspeak.TruckCommunicator;
import se.chalmers.fleetspeak.sound.SoundController;

public class StartActivity extends ActionBarActivity {

    private EditText ipTextField;
    private Context context = this;
    private EditText portTextField;
    private EditText userNameTextField;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private CheckBox savePrefs;

    private boolean isConnected;

    private SoundController soundController;

    static Messenger mService = null;
    final Messenger mMessenger = new Messenger(new CommandHandler());

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i("SERVICECONNECTION", "service started");
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
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

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
        portTextField.setText("8867");//portNumber);
        ipTextField.setText("192.168.43.147");//ipAdress);

        ipTextField.setInputType(InputType.TYPE_CLASS_TEXT);

        savePrefs = (CheckBox) findViewById(R.id.saveUserPref);


        Log.i("STARTACTIVITY", "binding service");
        startService(new Intent(this,SocketService.class));
        boolean fuck = isMyServiceRunning(SocketService.class);
        Log.i("STARTACTIVITY", "unfucked " + fuck);
        bindService(new Intent(this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
        fuck = isMyServiceRunning(SocketService.class);
        Log.i("STARTACTIVITY", "unfucked " + fuck);

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

        startConnection(ipAdress,portNumber);
        if(savePrefs.isChecked()) {
            savePreferences();
        }
      //  Intent intent = new Intent(this,JoinRoomActivity.class);
      //  startActivity(intent);
    }
    /**
     * Show Connection error message
     */
    public void showConnectionErrorMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Connection Error");
        builder.setMessage("Connection to Server failed");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog connectionError = builder.create();
        connectionError.show();
    }
    /**
     * Show dialog message the Car is running
     */
    public void showCarRunningErrorMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Car Running");
        builder.setMessage("Car is running several action are notpermited");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.cancel();
            }
        });
        AlertDialog carRunning = builder.create();
        carRunning.show();
    }
    /**
     * Enables or disable editing in textfield deping on isEditable
     * @param isEditable
     */
    public void textFieldIsEditable(boolean isEditable){
        userNameTextField.setFocusable(isEditable);
        ipTextField.setFocusable(isEditable);
        portTextField.setFocusable(isEditable);
    }
    /**
     * Saves the preferences of the User
     */
    public void savePreferences(){
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
                Message msg = Message.obtain(null, SocketService.CONNECT, port,0,ip);
                mService.send(msg);
                isConnected = true;
                int rtpPort = port+1;
                soundController = SoundController.create(this, ip, rtpPort);
            } catch (RemoteException e) {
            }
        }else{
            try {
                Message msg = Message.obtain(null, SocketService.DISCONNECT,"i cant set my name");
                isConnected = false;
                mService.send(msg);
            } catch (RemoteException e) {
            }
        }


    }
}
