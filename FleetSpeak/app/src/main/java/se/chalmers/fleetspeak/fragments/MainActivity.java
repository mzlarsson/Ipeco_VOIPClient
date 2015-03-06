package se.chalmers.fleetspeak.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.ArrayList;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.ServerHandler;
import se.chalmers.fleetspeak.SocketService;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.sound.SoundController;
import se.chalmers.fleetspeak.truck.TruckDataHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.ServiceUtil;
import se.chalmers.fleetspeak.util.Utils;

/**
 * Created by david_000 on 22/02/2015.
 */

public class MainActivity extends ActionBarActivity implements TruckStateListener{
    private FragmentManager fragmentManager;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private FragmentHandler handler = new FragmentHandler();

    static Messenger mService = null;

    /**
     * A Anonymous class to control the connection to the socket service
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


    protected void onCreate(Bundle savedInstanceState){

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = prefs.edit();
        // Get the theme saved in shared preferences with key "Theme" if no value is accessed returns 1
        int prefTheme = prefs.getInt("Theme", 1);
        // Set the theme of the application and username
        Utils.setTheme(prefTheme);
        Utils.setUsername(prefs.getString("username", "username"));
        Utils.setIpAdress(prefs.getString("ipAdress", "46.239.103.195"));
        Utils.setPortNumber(prefs.getInt("portNumber", 8867));

        fragmentManager = getFragmentManager();
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TruckDataHandler.addListener(this);
        Utils.setCarmode(TruckDataHandler.getInstance().getTruckMode());

        setContentView(R.layout.activity_main);
        // Set the start fragment
        setFragment(FragmentHandler.FragmentName.START);

    }
    /*
    Switch the current fragment showned in the view
    @param fragName - the fragement to be showed
    @param carmode - if the car is in driving mode
     */
    public void setFragment(FragmentHandler.FragmentName name){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content , handler.getFragment(name));
        fragmentTransaction.commit();
    }

    public void resetFragment(){
        setFragment(handler.getCurrentFragment());
    }
    /*
    Set the setting for the user
    @param username - the username to be set
    @param ipAdress - the adress to the server
    @param portNumber - the port number to the server
     */
    public void setUserSettings(String username, String ipAdress, int portNumber){
        prefEdit.putString("username", username);
        prefEdit.putString("ipAdress", ipAdress);
        prefEdit.putInt("portNumber", portNumber);
        prefEdit.putInt("Theme", Utils.getThemeID());
        prefEdit.commit();
    }
    public void truckModeChanged(boolean mode){
        Utils.setCarmode(mode);
        Utils.getCarMode();
    }

    /**
     * A method to start the connection to the server with a given IP adress, port number and username.
     */
    public void startConnection(){
        String ip = Utils.getIpAdress();
        String userName = Utils.getUsername();
        int port = Utils.getPort();
        setFragment(FragmentHandler.FragmentName.JOIN);

    }

    protected void onPause() {
        Log.i("STARTACTIVITY", "called onPause unbinding");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("STARTACTIVITY", "called onStop unbinding");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onBackPressed() {
        handler.backPressed(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
    protected  void onResume(){
        super.onResume();
    }

    // Create and move
    public void createAndMoveRoom(String RoomName){
        setFragment(FragmentHandler.FragmentName.CHAT);
    }
    // Move to room
    public void moveToRoom(int roomID){
        handler.setRoomID(roomID);

    }

    public void muteUser(User user, boolean mute){

    }
    public void updateUsers(){

    }
    public void updateRooms(){

    }
    public ArrayList<Room> getRoom(){
        return  null;
    }
    public ArrayList<User> getUsers(){
        return  null;
    }

}
