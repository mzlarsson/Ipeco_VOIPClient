package se.chalmers.fleetspeak.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import se.chalmers.fleetspeak.Model;
import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.truck.TruckDataHandler;
import se.chalmers.fleetspeak.truck.TruckStateListener;
import se.chalmers.fleetspeak.util.MessageValues;
import se.chalmers.fleetspeak.util.Utils;

/**
 * Created by david_000 on 22/02/2015.
 */

public class MainActivity extends ActionBarActivity implements TruckStateListener{
    private FragmentManager fragmentManager;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private FragmentHandler handler = new FragmentHandler();
    private Model model;

    protected void onCreate(Bundle savedInstanceState){
        model = new Model(this, updateHandler);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = prefs.edit();
        // Get the theme saved in shared preferences with key "Theme" if no value is accessed returns 1

        // Set the theme of the application and username
        Utils.setUsername(prefs.getString("username", "username"));
        Utils.setIpAdress(prefs.getString("ipAdress", "46.239.103.195"));
        Utils.setPortNumber(prefs.getInt("portNumber", 8867));

        fragmentManager = getFragmentManager();
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TruckDataHandler.addListener(this);
        Utils.setCarmode(TruckDataHandler.getInstance().getTruckMode());

        setContentView(R.layout.activity_main);
        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
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
    public void setUserSettings(){
        prefEdit.putString("username", Utils.getUsername());
        prefEdit.putString("ipAdress", Utils.getIpAdress());
        prefEdit.putInt("portNumber", Utils.getPort());
        prefEdit.putInt("Theme", Utils.getThemeID());
        prefEdit.commit();
    }
    public void truckModeChanged(boolean mode){
        Utils.setCarmode(mode);
        Utils.getCarMode();
    }
    private void showConnecting(boolean b){
        findViewById(R.id.loadingPanel).setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }
    /**
     * A method to start the connection to the server with a given IP adress, port number and username.
     */
    public void startConnection(){
        String ip = Utils.getIpAdress();
        String userName = Utils.getUsername();
        int port = Utils.getPort();
        showConnecting(true);
        model.connect(ip, port);
        model.setName(userName);
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
        model.disconnect();
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
        model.moveNewRoom(RoomName);
    }
    // Move to room
    public void moveToRoom(int roomID){
        model.move(roomID);
    }
    public void muteUser(User user, boolean mute){
        //TODO
    }
    public void Update(){
        handler.update();
    }
    public ArrayList<Room> getRooms(){
        return  model.getRooms();
    }
    public ArrayList<User> getUsers(int id){
        return  model.getUsers(id);
    }

    private Handler updateHandler = new Handler(){
      @Override
      public void handleMessage(Message msg){
          Log.d("updateHandler", "Message" + msg.what);
          switch (msg.what){
              case MessageValues.CONNECTED:
                  showConnecting(false);
                  setFragment(FragmentHandler.FragmentName.JOIN);
              break;
              case MessageValues.DISCONNECTED:
                  setFragment(FragmentHandler.FragmentName.START);
              break;
          }
      }
    };
}
