package se.chalmers.fleetspeak.activities;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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
import se.chalmers.fleetspeak.util.ServiceUtil;
import se.chalmers.fleetspeak.util.Utils;

/**
 * A Activity that shows the starting options of the application and guides the user to connect to the server.
 */
public class StartActivity extends ActionBarActivity implements TruckStateListener, Commandable {

    private String ipText;
    private Context context = this;
    private String portText;
    private String userNameText;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private boolean isConnected = false;
    static Messenger mService = null;
    private Menu menu;

    /**
     * Start up the connection service of the application
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i("SERVICECONNECTION", "service connected to StartActivity");
            mService = new Messenger(service);

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            Log.i("SERVICECONNECTION", "Disconnected");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Access the shared preferences for the activity and creates a editor
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = prefs.edit();

        // Get the theme saved in shared preferences with key "Theme" if no value is accessed returns 1
        int prefTheme = prefs.getInt("Theme", 1);
        // Set the theme of the application
        Utils.setTheme(prefTheme);
        Utils.onCreateActivityCreateTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CommandHandler.getInstance().addListener(this);
        TruckDataHandler.addListener(this);

        ipText = prefs.getString(getString(R.string.ip_adress_text), "46.239.103.195");
        portText = prefs.getString(getString(R.string.port_number_text), "8867");
        userNameText = prefs.getString(getString(R.string.username_text), "username");

        // Set up the view of the layout and object in the Activity
        truckModeChanged(TruckDataHandler.getInstance().getTruckMode());

        Log.i("STARTACTIVITY", "started service");
        Intent i = new Intent(this, SocketService.class);
        startService(i);
        Log.i("STARTACTIVITY", "binding service");
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.day_night_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.day_night_toggle) {
            Utils.changeTheme(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Log.i("STARTACTIVITY", "called onPause unbinding");
        super.onPause();
        CommandHandler.removeListener(this);
        if (ServiceUtil.isMyServiceRunning(this, ServiceConnection.class)) {
            unbindService(mConnection);
        }
    }

    @Override
    protected void onStop() {
        CommandHandler.removeListener(this);
        Log.i("STARTACTIVITY", "called onStop unbinding");
        super.onStop();
        if (ServiceUtil.isMyServiceRunning(this, ServiceConnection.class)) {
            unbindService(mConnection);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("STARTACTIVITY", "called onDestroy unbinding");
        CommandHandler.removeListener(this);
        unbindService(mConnection);
        ServiceUtil.close(this);
        super.onDestroy();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * A method that preforms suitable action with Connect Button is clicked
     *
     * @param view
     */
    public void onConnectButtonClick(View view) {
        if (!TruckDataHandler.getInstance().getTruckMode()) {
            CheckBox checkBox = (CheckBox) findViewById(R.id.saveUserPref);
            if (checkBox.isChecked()) {
                savePreferences();
            }
        }
        startConnection(ipText, Integer.parseInt(portText), userNameText);
    }

    /**
     * A method that creates and shows a connection error message
     */
    public void showConnectionErrorMessage() {
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
     * A method that saves the data from the editable text fields and the theme.
     */
    public void savePreferences() {
        EditText et = (EditText) findViewById(R.id.ipField);
        EditText et2 = (EditText) findViewById(R.id.portField);
        EditText et3 = (EditText) findViewById(R.id.usernameField);
        prefEdit.putString(getString(R.string.username_text), String.valueOf(et3.getText()));
        prefEdit.putString(getString(R.string.ip_adress_text), String.valueOf(et.getText()));
        prefEdit.putString(getString(R.string.port_number_text), String.valueOf(et2.getText()));
        prefEdit.putInt("Theme", Utils.getThemeID());
        prefEdit.commit();
    }

    /**
     * A method to start the connection to the server with a given IP adress, port number and username.
     *
     * @param ip       - the IP Adress
     * @param port-    the port number
     * @param userName - the username
     */
    public void startConnection(String ip, int port, String userName) {
        showConnecting(true);

        try {
            mService.send(Message.obtain(ServerHandler.connect(ip, port)));
            mService.send(Message.obtain(ServerHandler.setName(userName)));
            mService.send(Message.obtain(ServerHandler.getUsers()));
            isConnected = true;
            SoundController.create(this, ip, port);
        } catch (RemoteException e) {
        }
    }

    /**
     * Sets the showConnecting view viability
     *
     * @param b -
     */
    public void showConnecting(boolean b) {
        findViewById(R.id.loadingPanel).setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void truckModeChanged(boolean mode) {
        setContentView(mode ? R.layout.activity_car_start : R.layout.activity_start);
        if (menu != null) {
            MenuItem item = menu.findItem(R.id.day_night_toggle);
            item.setVisible(!mode);
        }
        if (mode) {
            ((TextView) findViewById(R.id.IpAdress)).setText(ipText);
            ((TextView) findViewById(R.id.userName)).setText(userNameText);
        } else {
            View view = findViewById(R.id.relStart_layout);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    setNoFocus();
                    return false;
                }
            });
        }
        final EditText ipTextField = (EditText) findViewById(R.id.ipField);
        final EditText portField = (EditText) findViewById(R.id.portField);
        final EditText userNameField = (EditText) findViewById(R.id.usernameField);
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
            }
        });
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
        Utils.setUsername(userNameText);
        ipTextField.setText(ipText);
        portField.setText(portText);
        userNameField.setText(userNameText);
    }

    /**
     * A method to remove focus from a object in the application
     */
    public void setNoFocus(){
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        RelativeLayout view1 = (RelativeLayout)findViewById(R.id.relStart_layout);
        view1.requestFocus();
    }

    @Override
    public void onDataUpdate(String command) {
        showConnecting(false);
        if(command.equals("connected")){
            //Without binding the server it will crash on reconnect not sure why it works
            if(ServiceUtil.isMyServiceRunning(this, SocketService.class)) {
                bindService(new Intent(this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
            }

            unbindService(mConnection);
            Intent intent = new Intent(this,JoinRoomActivity.class);
            startActivity(intent);
        }else if(command.equals("connection failed")){
            SoundController.close();
            Log.i("STARTACTIVITY", " try again");
            showConnectionErrorMessage();
        }


    }
}
