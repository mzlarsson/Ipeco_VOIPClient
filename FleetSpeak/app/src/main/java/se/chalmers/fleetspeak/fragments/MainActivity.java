package se.chalmers.fleetspeak.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
 * The activity that  enables the user to interact with the model
 * Created by David Gustafsson on 22/02/2015.
 */

public class MainActivity extends ActionBarActivity implements TruckStateListener{
    private FragmentManager fragmentManager;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;
    private FragmentHandler handler = new FragmentHandler();
    private Model model;


    /**
     * A handler that handles update messages from the server connection
     */
    private Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.d("updateHandler", "Got Message=" + msg.what);
            switch (msg.what){
                case MessageValues.CONNECTED:
                    showConnecting(false);
                    // If the user is able to be connected go to the join fragment
                    // where the user can choose which room to join
                    setFragment(FragmentHandler.FragmentName.JOIN);
                    break;
                case MessageValues.DISCONNECTED:
                    // If disconnected from the server return the to start fragment
                    // where user can try to connect again
                    setFragment(FragmentHandler.FragmentName.START);
                    break;
                case MessageValues.MODELCHANGED:
                    update();
                    break;
                case MessageValues.CONNECTIONFAILED:
                    handler.showConnectionErrorMessage();
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.i("MainActivity:", "Activity is created");
        super.onCreate(savedInstanceState);
        // Show the home icon im the option menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create a new Model with this activity and the updatehandler
        model = new Model(this, updateHandler);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = prefs.edit();
        // Get the theme saved in shared preferences with key "Theme" if no value is accessed returns 1
        // and set it in Utils
        Utils.setTheme(prefs.getInt("theme", 1));
        // Get the username, ip adress and the port number in shared preferences and set them in Utils
        Utils.setUsername(prefs.getString("username", "username"));
        Utils.setIpAdress(prefs.getString("ipAdress", "46.239.103.195"));
        Utils.setPortNumber(prefs.getInt("portNumber", 8867));

        // Create fragment manager that will create fragment transactions for the activity
        fragmentManager = getFragmentManager();

        // Set the activity to a listener of TruckDatarHandler enabling it to recieve updates
        // when the truck changes state
        TruckDataHandler.addListener(this);

        // Set the state of the car in Utils
        Utils.setCarmode(TruckDataHandler.getInstance().getTruckMode());

        // Set the main layout
        setContentView(R.layout.activity_main);
        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
        // Set the start fragment
        setFragment(FragmentHandler.FragmentName.START);
        setTheme(Utils.getThemeID() == R.style.Theme_Fleetspeak_light);
    }


    /**
    Set the currently showed fragment in the activity
    @param name - the fragement to be showed
     */
    public void setFragment(FragmentHandler.FragmentName name){
        Log.i("MainActivitiy:", "Start a new fragment transaction and replace " +
                "the showed fragment");
        Log.d("FragHandler", "ChatFragment ?"  + (name == FragmentHandler.FragmentName.CHAT));
        //Start up a new fragment transaction
        // let the transaction replace the current showed fragment with the fragment
        // specified in the parameter name.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content , handler.getFragment(name));
        fragmentTransaction.commit();
        Log.d("FragHandler", " setting new Fragment done");

    }

    /**
     * Restart the currently used fragment
     */
    public void restartFragment(){
        Log.i("MainActivity:", "restart and reshow the currently used fragment");
        // Get the handler to recreate the fragment the is currently used
        handler.recreateFragment(handler.getCurrentFragment());
        // Set the recreated fragment to be showed
        setFragment(handler.getCurrentFragment());
    }
    /**
    Save the currently used user settings for application to use in future runs
     */
    public void setUserSettings(){
        Log.i("MainActivity:", "save the currently used user settings");
        // Put the currently used user settings from Utils
        // in the saved preferences
        prefEdit.putString("username", Utils.getUsername());
        prefEdit.putString("ipAdress", Utils.getIpAdress());
        prefEdit.putInt("portNumber", Utils.getPort());
        prefEdit.putInt("theme", Utils.getThemeID());
        // Commit the changes
        prefEdit.commit();
    }
    @Override
    public void truckModeChanged(boolean mode){
        if(mode != Utils.getCarMode()) {
            Log.d("MainActivity:", "truck mode changed TO " + mode);
            // Save the car state in the Utils
            Utils.setCarmode(mode);
            restartFragment();
        }
    }

    /**
     * Show or hide the loadingpanel depending on in parameter
     * @param showConnecting- if the loadingpanel is to be showed
     */
    private void showConnecting(boolean showConnecting){
        // Set the loadingpanel view visible or invisible depending on showConnecting
        findViewById(R.id.loadingPanel).setVisibility(showConnecting ? View.VISIBLE : View.INVISIBLE);
    }
    /**
     * Start a connection request to the server with the currently used ip adress, username and port number.
     */
    public void startConnection(){
        Log.i("MainActivity:", "start a connection request to the server");
        // Get the user setting from Utils class
        String ip = Utils.getIpAdress();
        String userName = Utils.getUsername();
        int port = Utils.getPort();
        // Show the loadingPanel panel
        showConnecting(true);
        // Send the model a connection request with the ip adress and port number from Utils
        model.connect(ip, port);
        // Set the username the model shall use from with the name from Utils
        model.setName(userName);
    }
    @Override
    protected void onPause() {
        Log.i("MainActivity:", "pausing activity");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("MainActivity:", "resuming activity");
        super.onStop();
        model.disconnect();
    }
    @Override
    protected void onDestroy() {
        Log.i("MainActivity:", "destroying activity");
        super.onDestroy();
        model.disconnect();
    }
    @Override
    public void onBackPressed() {
        Log.i("MainActivity:", "Back is pressed");
        // Send backpressed action to handler with this activity as parameter
        // as backpressed logic changes depending on the currently showed used
        // fragement
        handler.backPressed(this);
    }
    @Override
    protected void onRestart() {
        Log.i("MainActivity:", "restarting activity");
        super.onRestart();
    }
    @Override
    protected  void onResume(){
        Log.i("MainActivity:", "resuming activity");
        super.onResume();
    }

    /**
     * Set the activity to represent a new room with the RoomName as title
     * @param RoomName - the name of the new room
     */
    public void createAndMoveRoom(String RoomName){
        Log.i("MainActivity:", "move to new room");
        model.moveNewRoom(RoomName);
        setFragment(FragmentHandler.FragmentName.CHAT);
    }

    /**
     * Set the activity to represent a existing room with the id roomID
     * @param roomID - the id of the room to be moved to
     */
    public void moveToRoom(int roomID){
        Log.i("MainActivity:", "move to existing room");
        model.move(roomID);
        setFragment(FragmentHandler.FragmentName.CHAT);
    }

    /**
     * Set the mute status of a user
     * @param user - the user which mute status is to be set
     * @param mute - if the user is to be muted
     */
    public void muteUser(User user, boolean mute){
        Log.d("MainActivity:", "Muting user " + user.getName() + " "+ mute);
        //TODO
    }

    /**
     * Set if the push to talk is active for the user
     */
    public void pushToTalk(){
        //TODO
        model.pushToTalk();
        handler.update(FragmentHandler.FragmentName.CHAT);
    }

    /**
     * Return the current status if talk is active
     * @return - if talk mode is active
     */
    public boolean isTalkActive(){
        return model.isTalking();
    }

    /**
     * Request a update of the information showed in the currently used fragment
     */
    private void update(){
        Log.d("MainActivity:", "update request received");
        Log.i("MainActivity:", "update request sent");
        handler.update(handler.getCurrentFragment());
    }

    /**
     * Get the rooms of the model
     * @return ArrayList<Room> - the rooms of the model
     */
    public ArrayList<Room> getRooms(){
        return  model.getRooms();
    }

    /**
     * Get the users of a room
     * @param roomID - the id of the room
     * @return ArrayList<User> - the users of the room
     */
    public ArrayList<User> getUsers(int roomID){
        return  model.getUsers(roomID);
    }

    /**
     * Get the roomID of the room which the activity currently is representing
     * @return  int - the roomID the activity is currently representing
     */
    public int getCurrentRoom(){
        return model.getCurrentRoom();
    }

    /**
     * Change the activity to match the currently used theme which is either
     * light or dark.
     * @param isLightTheme - if the theme currently used is the light theme
     */
    public void setTheme(boolean isLightTheme){
        Log.d("MainActivity:", "Theme is changed" + " new theme is light=" + isLightTheme);
        // Create a new spannable string which is contains to the title of the activity
        SpannableString str = new SpannableString(getTitle());
        if(isLightTheme) {
            // Set the color of the spannable string to black if light theme is currently used
            str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str.length(), 0);
        }else{
            // Set the color of the spannable string to white if dark theme is used instead
            str.setSpan(new ForegroundColorSpan(Color.WHITE), 0 , str.length(), 0);
       }
        // Set the spannable string to the title of the activity
        setTitle(str);
    }

    /**
     * Start a disconnect request to the server
     */
    public void disconnect(){
        model.disconnect();
    }

    public void requestAssistance(int i){
        //TODO Request assitance should send commands to server
        switch (i){
            case 0:
            break;
            case 1:
            break;
            case 2:
            break;
            case 3:
            break;
            default:
        }

    }
    public void buildAlertDialog(){

    }
}
