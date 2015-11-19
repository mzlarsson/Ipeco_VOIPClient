package se.chalmers.fleetspeak.model;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import se.chalmers.fleetspeak.audio.sound.SoundOutputController;
import se.chalmers.fleetspeak.network.TCP.TLSConnector;
import se.chalmers.fleetspeak.network.UDP.RTPHandler;
import se.chalmers.fleetspeak.network.UDP.STUNInitiator;
import se.chalmers.fleetspeak.network.UDP.UDPConnector;
import se.chalmers.fleetspeak.structure.connected.ProximityFragment;
import se.chalmers.fleetspeak.util.LocationUtil;
import se.chalmers.fleetspeak.util.MessageValues;
import se.chalmers.fleetspeak.util.RoomHistory;

/**
 * Created by Nieo on 08/03/15.
 * Updated by Patrik on 2015-11-14
 */
public class Model {
    private Building building;
    private CommandHandler commandHandler;
    private TLSConnector connector;
    private Handler callbackHandler;
    private State state;

    private RTPHandler rtpHandler;
    private SoundOutputController soundOutputController;

    private ArrayList<ProximityChangeListener> proximityChangeListeners;
    private LocationUtil locationUtil;
    private LocationUtil.LocationChangeListener locationChangeListener;
    private long lastLocationUpdate = -1;
    private final long TIME_BETWEEN_LOCATION_UPDATES = 3*60*1000;

    private String username ="";
    private String password ="";

    private int roomVersion;
    private RoomHistory roomHistory;


    protected Model(Handler callbackHandler){
        state = State.not_connected;
        building = new Building(callbackHandler);
        commandHandler = new CommandHandler();
        connector = new TLSConnector(commandHandler);
        this.callbackHandler = callbackHandler;
        proximityChangeListeners = new ArrayList<>();
        roomVersion = 0;
        roomHistory = new RoomHistory();
        //TODO load history from disk
    }
    public ArrayList<Room> getHistory(){
        ArrayList<Room> history = new ArrayList<>();
        for(Integer i: roomHistory.getPastRooms()){
            Room r = building.getRooom(i);
            if(r != null) {
                history.add(r);
            }
        }
        return history;
    }


    public void startLocationTracking(Context context){
        if(locationUtil == null && locationChangeListener == null) {
            locationUtil = LocationUtil.getInstance(context, false);
            locationChangeListener = new LocationUtil.LocationChangeListener() {
                @Override
                public void speedChanged(float speed) {
                }

                @Override
                public void locationChanged(double latitude, double longitude) {
                    updateLocation(latitude, longitude);
                }
            };
            locationUtil.addListener(locationChangeListener);
        }
    }

    public ArrayList<Room> getRooms(){
        if(state == State.authenticated)
            return building.getRooms();
        return null;
    }

    /**
     * Sends a request to update the locations of all users and attempt to find users within
     * a specific radius of a location.
     * Will request the location and distance from the ProximityChangeListener and respond
     * with the search result.
     * @param proximityChangeListener The ProximityChangeListener to receive the update.
     */
    public void requestProximityUpdate(ProximityChangeListener proximityChangeListener) {
        proximityChangeListeners.add(proximityChangeListener);
        connector.sendMessage("{\"command\":\"request_all_user_locations\"," +
                "\"userid\":\"" + building.getUserid() + "\"}");
    }

    /**
     * Finds all users within a given radius of a location and the rooms they are in.
     * Requests an update of the location on outdated users.
     * @param location The center of the circle area to search in.
     * @param distance The radius of the circle in meters.
     * @return A map with the rooms as key and a list with the users of that room within the
     * distance as the values.
     */
    private HashMap<Room,ArrayList<User>> getRoomsCloserThan(Location location, int distance) {
        if (location!=null) {
            return building.getRoomsCloserThan(location, distance);
        } else {
            return null;
        }
    }

    public ArrayList<User> getUsers(int roomid){
        if(state == State.authenticated)
            return building.getUsers(roomid);
        return null;
    }

    public String getCurrentUserAlias(){
        int clientID = building.getUserid();
        for(User u : building.getUsers(building.getCurrentRoom())){
            if(u.getId() == clientID){
                return u.getName();
            }
        }
        return null;
    }

    public void connect(String name, String password){
        if(state == State.not_connected){
            Log.d("Model", "Trying to connect");
            username = name;
            building.clear();
            state = State.connecting;
            connector.connect(password, 8867);
        }else{
            Log.d("Model", "Already connected or trying to connect");
        }
    }

    public void disconnect(){
        if(state == State.authenticated || state == State.connected){
            Log.i("Model", "Disconnecting");
            connector.disconnect();
            terminate();
            Log.d("Model", " set state to not connected");
        }else{
            Log.d("Model", "Not connected, cannot send disconnect");
        }
    }
    public void move(int roomid){
        if(state == State.authenticated &&
                roomid != building.getCurrentRoom()){
            Log.d("Model", "Moving to " + roomid);
            connector.sendMessage("{\"command\":\"moveclient\"," +
                    "\"userid\":\"" + building.getUserid() + "\", " +
                    "\"currentroom\":\"" + building.getCurrentRoom() + "\"," +
                    "\"destinationroom\":\"" + roomid + "\"}");
            building.moveUser(building.getUserid(), building.getCurrentRoom(), roomid);
        }
        Log.d("Model", roomid + " " + building.getCurrentRoom() );
    }
    public void moveNewRoom(String roomname){
        Log.d("Model", state.toString());
        if(state == State.authenticated) {
            Log.d("Model", "Moving to " + roomname);
            connector.sendMessage("{\"command\":\"movenewroom\"," +
                    "\"userid\":\"" + building.getUserid() + "\", " +
                    "\"currentroom\":\"" + building.getCurrentRoom() + "\"," +
                    "\"roomname\":\"" + roomname + "\"}");
        }
    }
    public void updateLocation(double latitude, double longitude){
        Log.d("Model", "Sending position update");
        if(lastLocationUpdate < 0 || System.currentTimeMillis()-lastLocationUpdate>TIME_BETWEEN_LOCATION_UPDATES) {
            connector.sendMessage("{\"command\":\"updatelocation\"," +
                    "\"userid\":\"" + building.getUserid() + "\"," +
                    "\"latitude\":\"" + latitude + "\"," +
                    "\"longitude\":\"" + longitude + "\"}");
            lastLocationUpdate = System.currentTimeMillis();
        }
    }


    public void setNewHandler(Handler handler){
        callbackHandler = handler;
        building.setHandler(handler);

    }
    public int getCurrentRoom(){
        return building.getCurrentRoom();
    }

    public boolean isAuthenticated(){
        Log.d("Model", " State is autenticacted = " + ((state ==State.authenticated)));
        return (state ==State.authenticated);
    }
    private void terminate(){
        state = State.not_connected;
        if(rtpHandler != null)
            rtpHandler.terminate();
        rtpHandler = null;
        if(soundOutputController != null)
            soundOutputController.destroy();
        soundOutputController = null;
        if(locationUtil != null && locationChangeListener != null){
            locationUtil.removeListener(locationChangeListener);
        }
        //TODO save roomhistory to disk
    }
    class CommandHandler extends Handler {
        public void handleMessage(Message msg) {
            Log.d("Model", "Commandhandler, messagecode " + msg.what);
            switch(msg.what) {
                case MessageValues.CONNECTED:
                    state = State.connected;
                    break;
                case MessageValues.DISCONNECTED:
                    callbackHandler.sendEmptyMessage(MessageValues.DISCONNECTED);
                    terminate();
                    break;
                case MessageValues.CONNECTIONFAILED:

                    callbackHandler.sendEmptyMessage(MessageValues.CONNECTIONFAILED);
                    state = State.not_connected;
                    break;
                case MessageValues.UDPCONNECTOR:
                    Log.i("Commandhandler", "got a udpconnector");
                    connector.sendMessage("{\"command\":\"clientudptestok\"}");
                    rtpHandler = new RTPHandler((UDPConnector)msg.obj);
                    soundOutputController = new SoundOutputController(rtpHandler);
                    break;
                case MessageValues.COMMAND:                    
                    try {
                        JSONObject json = new JSONObject((String)msg.obj);
                        try{
                            int v = json.getInt("structurestate");
                            Log.d("CommandHandler", v + " structurestate");
                            if(roomVersion != 0 && v - 1 > roomVersion){
                                requestSync();
                                break;
                            }else{
                                roomVersion = v;
                            }
                        }catch(JSONException e){

                        }

                        Log.d("Model", "Command recieved" + json.getString("command"));
                        switch (json.getString("command").toLowerCase()) {
                            case "setinfo":
                                building.setUserid(json.getInt("userid"));
                                break;
                            case "addeduser":
                                Log.d("commandhandler", json.toString());
                                building.addUser(json.getInt("userid"), json.getString("username"), json.getInt("roomid"));
                                break;
                            case "changedroomname":
                                building.changeRoomName(json.getInt("roomid"), json.getString("roomname"));
                            case "moveduser":
                                building.moveUser(json.getInt("userid"), json.getInt("currentroom"), json.getInt("destinationroom"));
                                if(json.getInt("userid") == building.getUserid()){ //Update history if local user is moved
                                    roomHistory.addRoom(json.getInt("currentroom"));
                                }
                                break;
                            case "createdroom":
                                building.addRoom(json.getInt("roomid"), json.getString("roomname"));
                                break;
                            case "removeduser":
                                building.removeUser(json.getInt("userid"), json.getInt("roomid"));
                                break;
                            case "removedroom":
                                building.removeRoom(json.getInt("roomid"));
                                break;
                            case "initiatesoundport":
                                byte b = Byte.parseByte(json.getString("controlcode"));
                                new STUNInitiator(connector.getIP(),json.getInt("port"), b, commandHandler);
                                break;
                            case "sendauthenticationdetails":
                                Log.d("auth", username);
                                JSONObject command = new JSONObject();
                                command.put("command", "authenticationdetails");
                                command.put("username", username);
                                command.put("password", password);
                                command.put("clienttype", "android");
                                
                                connector.sendMessage(command.toString());
                                break;
                            case "authenticationresult":
                                if (json.getBoolean("result")) { // true if successfully authenticated
                                    state = State.authenticated;
                                    callbackHandler.sendEmptyMessage(MessageValues.AUTHENTICATED);

                                } else {
                                    state = State.not_connected;
                                    callbackHandler.sendMessage(Message.obtain(null, MessageValues.AUTHENTICATIONFAILED, json.getString("rejection"))); // .getValue contains the reason for rejection
                                }
                                break;
                            case "all_locations_update":
                                updateAllUserLocations(json.getJSONArray("locations"));
                                break;
                        }
                    } catch (JSONException e) {
                       Log.e("Model", "JSONException " + e.getMessage()); 
                    } catch (NullPointerException e){
                        Log.e("CommandHandler", String.valueOf(e.getMessage()));
                        e.printStackTrace();
                    }
                    
                

                break;
            }

        }
    }

    private void requestSync(){
        try {
            JSONObject request = new JSONObject();
            request.put("command", "requestchangehistory");
            request.put("fromversion", roomVersion);
            request.put("userid", building.getUserid());
            request.put("roomid", building.getCurrentRoom());
            connector.sendMessage(request.toString());
        }catch(JSONException e){
            Log.e("Model", "failed to send requestSync command because of JSON");
        }
    }

    /**
     * Updates all the given user's locations.
     * @param userarr A JSONArray containing the new information of the users.
     */
    private void updateAllUserLocations(JSONArray userarr) {
        try {
            for (int i=0; i<userarr.length(); i++) {
                updateUserLocation(userarr.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (final ProximityChangeListener pcl : proximityChangeListeners) {
            Location loc = pcl.getRequestedLocation();
            final HashMap<Room, ArrayList<User>> response = (loc==null?null:getRoomsCloserThan(loc, pcl.getRequestedDistance()));
            pcl.roomProximityUpdate(response);
        }
        proximityChangeListeners.clear();
    }

    /**
     * Updates the location of the given user.
     * @param json The JSONObject containing the new information of the user.
     */
    private void updateUserLocation(JSONObject json) {
        try {
            building.updateLocation(json.getInt("userid"),
                    json.getInt("roomid"),
                    json.getDouble("latitude"),
                    json.getDouble("longitude"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private enum State{
        not_connected, connecting, connected, authenticated;
    }


}
