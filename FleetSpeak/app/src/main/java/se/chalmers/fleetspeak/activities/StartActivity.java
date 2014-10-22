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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import se.chalmers.fleetspeak.CommandHandler;
import se.chalmers.fleetspeak.Commandable;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.ServerHandler;
import se.chalmers.fleetspeak.SocketService;
import se.chalmers.fleetspeak.sound.SoundController;
import se.chalmers.fleetspeak.truck.TruckDataHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.ThemeUtils;

public class StartActivity extends ActionBarActivity implements TruckStateListener, Commandable {

    private static TruckDataHandler truckDataHandler;
    private String ipText;
    private Context context = this;
    private String portText;
    private String userNameText;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private SoundController soundController;
    private boolean isConnected = false;
    static Messenger mService = null;


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i("SERVICECONNECTION", "service started");
            mService = new Messenger(service);

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
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

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = prefs.edit();
        int prefTheme = prefs.getInt("Theme", R.style.Theme_Fleetspeak_dark);
        ThemeUtils.setTheme(prefTheme);
        ThemeUtils.onCreateActivityCreateTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommandHandler.getInstance().addListener(this);
        truckDataHandler.addListener(this);
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
                ThemeUtils.setUsername(String.valueOf(userNameField.getText()));
            }
        });

        portText = prefs.getString( getString(R.string.port_number_text),"8867");
        userNameText = prefs.getString(getString(R.string.username_text), "username");
        ThemeUtils.setUsername(userNameText);
        ipText = prefs.getString(getString(R.string.ip_adress_text),"46.239.103.195");
        ipTextField.setText(ipText);
        portField.setText(portText);
        userNameField.setText(userNameText);
        final CheckBox savePrefsCheckbox = (CheckBox) findViewById(R.id.saveUserPref);
        savePrefsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (savePrefsCheckbox.isChecked()) {
                    savePreferences();
                }
            }
        });
        //Removes focus from the EditTextFields in the app
        RelativeLayout l = (RelativeLayout) findViewById(R.id.relStart_layout);
        l.requestFocus();




            Intent i =  new Intent(this, SocketService.class);

            startService(new Intent(i));
            Log.i("STARTACTIVITY", "started service");

        Log.i("STARTACTIVITY", "binding service");
        startService(new Intent(this,SocketService.class));
        boolean unfucked = isMyServiceRunning(SocketService.class);
        Log.i("STARTACTIVITY", "unfucked? " + unfucked);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        unfucked = isMyServiceRunning(SocketService.class);
        Log.i("STARTACTIVITY", "unfucked? " + unfucked);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.day_night_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.day_night_toggle) {
            // ThemeUtils.changeTheme(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause(){
        Log.i("STARTACTIVITY", "called onPause unbinding");
        super.onPause();
        CommandHandler.removeListener(this);
        if(this.isMyServiceRunning(ServiceConnection.class)) {
            unbindService(mConnection);
        }
    }
    @Override
    protected void onStop(){
        CommandHandler.removeListener(this);
        Log.i("STARTACTIVITY", "called onStop unbinding");
        super.onStop();
        if(this.isMyServiceRunning(ServiceConnection.class)) {
             unbindService(mConnection);
        }
    }
    @Override
    protected void onDestroy(){
        CommandHandler.removeListener(this);
        Log.i("STARTACTIVITY", "called onDestroy unbinding" );
        super.onDestroy();
        if(this.isMyServiceRunning(ServiceConnection.class)) {
            unbindService(mConnection);
        }
        soundController.close();
    }
    protected void onRestart(){
        super.onRestart();
    }

    public void onConnectButtonClick(View view) {
       startConnection(ipText, Integer.parseInt(portText), userNameText);


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

    /**
     * Saves the preferences of the User
     */
    public void savePreferences(){
            prefEdit.putString(getString(R.string.username_text), userNameText);
            prefEdit.putString(getString(R.string.ip_adress_text), ipText);
            prefEdit.putString(getString(R.string.port_number_text), portText);
            prefEdit.putInt("Theme",ThemeUtils.getThemeID());
            prefEdit.commit();
    }

    public void startConnection(String ip, int port, String userName){
        connecting(true);
            try {
                mService.send(Message.obtain(ServerHandler.connect(ip,port)));
                mService.send(Message.obtain(ServerHandler.setName(userName)));
                mService.send(Message.obtain(ServerHandler.getUsers()));
                isConnected = true;
				
                soundController = SoundController.create(this, ip, port);
            } catch (RemoteException e) {

        }


    }

    public void CarTrue(View view){
        truckModeChanged(true);
    }
    public void connecting(boolean b){
        findViewById(R.id.loadingPanel).setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }
    @Override
    public void truckModeChanged(boolean mode) {
        setContentView(mode? R.layout.activity_car_start: R.layout.activity_start);
        if(mode){
            ((TextView)findViewById(R.id.IpAdress)).setText(ipText);
            ((TextView)findViewById(R.id.userName)).setText(userNameText);
        }
    }

    public void showConnect(View view) {
        View view1 = findViewById(R.id.loadingPanel);
            connecting(!(view1.getVisibility() == View.VISIBLE));

    }
    public void setNoFocus(View view){
        RelativeLayout view1 = (RelativeLayout)findViewById(R.id.relStart_layout);
        view.requestFocus();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    @Override
    public void onDataUpdate(String command) {
        connecting(false);
        Log.i("STARTACTIVITY", "Im Commandhadlers bitch");
        if(command.equals("connected")){
            //Without binding the server it will crash on reconnect not sure why it works
            if(isMyServiceRunning(SocketService.class))
                bindService(new Intent(this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
              unbindService(mConnection);
            Intent intent = new Intent(this,JoinRoomActivity.class);
            startActivity(intent);
        }else if(command.equals("connection failed")){
            soundController.close();
            Log.i("STARTACTIVITY", " try again");
            showConnectionErrorMessage();
        }


    }
    public void startDemo(View view) {
        Intent intent = new Intent(this,JoinRoomActivity.class);
        startActivity(intent);
    }
}
