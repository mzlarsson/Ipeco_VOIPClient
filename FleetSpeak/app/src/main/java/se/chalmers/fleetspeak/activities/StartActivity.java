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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;


import se.chalmers.fleetspeak.CommandHandler;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.ServerHandler;
import se.chalmers.fleetspeak.SocketService;
import se.chalmers.fleetspeak.sound.SoundController;

public class StartActivity extends ActionBarActivity {

    private String ipText;
    private Context context = this;
    private String portText;
    private String userNameText;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private boolean savePrefs;

    private boolean isConnected;

    private SoundController soundController;

    static Messenger mService = null;
    Messenger mMessenger;

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

        mMessenger = new Messenger(CommandHandler.getInstance());
        CommandHandler.getInstance().addListener(this);

        final EditText ipTextField = (EditText) findViewById(R.id.ipField);

        ipTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ipText = String.valueOf(ipTextField.getText());
            }
        });
        final EditText portField = (EditText) findViewById(R.id.portField);
        portField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                portText = String.valueOf(portField.getText());
            }
        });

        final EditText userNameField = (EditText) findViewById(R.id.usernameField);
        userNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userNameText = String.valueOf(userNameField.getText());
                Log.i("StartActivity", "after text change " + userNameText);
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = prefs.edit();

        portText = prefs.getString( "portNumber","8867");
        userNameText = prefs.getString("username", "username");
        Log.i("StartActivity", "perfs.getString " + userNameText);
        ipText = prefs.getString("ipAdress","192.168.43.147");
        ipTextField.setText(ipText);
        portField.setText(portText);
        userNameField.setText(userNameText);
        Log.i("StartActivity", userNameText);
        final CheckBox savePrefsCheckbox = (CheckBox) findViewById(R.id.saveUserPref);
        savePrefsCheckbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                savePreferences(savePrefsCheckbox.isChecked());
            }
        });


        Log.i("STARTACTIVITY", "binding service");
        startService(new Intent(this,SocketService.class));
        boolean unfucked = isMyServiceRunning(SocketService.class);
        Log.i("STARTACTIVITY", "unfucked? " + unfucked);
        bindService(new Intent(this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
        unfucked = isMyServiceRunning(SocketService.class);
        Log.i("STARTACTIVITY", "unfucked? " + unfucked);

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
    @Override
    protected void onStop(){
        super.onStop();
        if(this.isMyServiceRunning(ServiceConnection.class)) {
             unbindService(mConnection);
        }
    }
    protected void onDestroy(){
        super.onDestroy();
        if(this.isMyServiceRunning(ServiceConnection.class)) {
            unbindService(mConnection);
        }
    }
    protected void onRestart(){
        super.onRestart();
    }
    public void onConnectButtonClick(View view) {
        startConnection(ipText,Integer.parseInt(portText), userNameText);
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
        builder.setMessage("Car is running several action are not permited");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.cancel();
            }
        });
        AlertDialog carRunning = builder.create();
        carRunning.show();
    }
    public void setCarMode(Boolean b){
        if(b){
            showCarRunningErrorMessage();
        }
        setContentView(b? R.layout.activity_car_start: R.layout.activity_start);
    }
    /**
     * Saves the preferences of the User
     */
    public void savePreferences(boolean b){
        if(b) {
            prefEdit.putString(getString(R.string.username_text), userNameText);
            prefEdit.putString(getString(R.string.ip_adress_text), ipText);
            prefEdit.putString(getString(R.string.port_number_text), portText);
        }
    }

    public void startConnection(String ip, int port, String userName){
        if (!isConnected) {
            try {
                mService.send(Message.obtain(ServerHandler.connect(ip,port)));
                mService.send(Message.obtain(ServerHandler.setName(userName, 0)));
                mService.send(Message.obtain(ServerHandler.getUsers()));
                isConnected = true;
                int rtpPort = port+1;
                soundController = SoundController.create(this, ip, rtpPort);
            } catch (RemoteException e) {
            }
        }else{
            try {
                Message msg = Message.obtain(null, SocketService.SETNAME,userName);

                mService.send(msg);
            } catch (RemoteException e) {
            }
        }


    }
}
