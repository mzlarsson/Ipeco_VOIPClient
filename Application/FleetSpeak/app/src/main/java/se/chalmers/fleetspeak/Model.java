package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import se.chalmers.fleetspeak.Network.TCP.TLSConnector;
import se.chalmers.fleetspeak.Network.UDP.RTPHandler;
import se.chalmers.fleetspeak.Network.UDP.STUNInitiator;
import se.chalmers.fleetspeak.Network.UDP.UDPConnector;
import se.chalmers.fleetspeak.audio.sound.SoundOutputController;
import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 08/03/15.
 */
public class Model {
    private Building building;
    private CommandHandler commandHandler;
    private TLSConnector connector;
    private Handler callbackHandler;
    private State state;

    private RTPHandler rtpHandler;
    private SoundOutputController soundOutputController;

    String username ="";
    String password ="";



    public Model(Handler callbackHandler){
        state = State.not_connected;
        building = new Building(callbackHandler);
        commandHandler = new CommandHandler();
        connector = new TLSConnector(commandHandler);
        this.callbackHandler = callbackHandler;
    }

    public ArrayList<Room> getRooms(){
        if(state == State.authenticated)
            return building.getRooms();
        return null;
    }
    public ArrayList<User> getUsers(int roomid){
        if(state == State.authenticated)
            return building.getUsers(roomid);
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
            state = State.not_connected;
            connector.disconnect();
            if(soundOutputController != null)
                soundOutputController.destroy();
            if(rtpHandler != null)
                rtpHandler.terminate();
            state = State.not_connected;
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
    class CommandHandler extends Handler {
        public void handleMessage(Message msg) {
            Log.d("Model", "Commandhandler " + msg.what);
            switch(msg.what) {
                case MessageValues.CONNECTED:
                    state = State.connected;
                    break;
                case MessageValues.DISCONNECTED:
                    callbackHandler.sendEmptyMessage(MessageValues.DISCONNECTED);
                    state = State.not_connected;
                    if(rtpHandler != null)
                        rtpHandler.terminate();
                    break;
                case MessageValues.CONNECTIONFAILED:

                    callbackHandler.sendEmptyMessage(MessageValues.CONNECTIONFAILED);
                    state = State.not_connected;
                    break;
                case MessageValues.UDPCONNECTOR:
                    Log.i("Commandhandler", "got a udpconnector");
                    connector.sendMessage("{\"command\":\"clientUdpTestOk\"}");
                    rtpHandler = new RTPHandler((UDPConnector)msg.obj);
                    soundOutputController = new SoundOutputController(rtpHandler);
                    break;
                case MessageValues.COMMAND:                    
                    try {
                        JSONObject json = new JSONObject((String)msg.obj);
                        Log.d("Model", "Command recieved" + json.getString("command"));
                        switch (json.getString("command").toLowerCase()) {
                            case "setinfo":
                                building.setUserid(json.getInt("userid"));
                                break;
                            case "addeduser":
                                building.addUser(json.getInt("userid"),json.getString("username"), json.getInt("roomid"));
                                break;
                            case "changedroomname":
                                building.changeRoomName(json.getInt("roomid"), json.getString("roomname"));
                            case "moveduser":
                                building.moveUser(json.getInt("userid"), json.getInt("currentroom"), json.getInt("destinationroom"));
                                break;
                            case "createdroom":
                                building.addRoom(json.getInt("roomid"), json.getString("roomname"));
                                break;
                            case "removeduser":
                                building.removeUser(json.getInt("userid"), json.getInt(""));
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
                                command.put("command", "autheticationdetails");
                                command.put("work", json.getString("work"));
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
                        }
                    } catch (JSONException e) {
                       Log.e("Model", "JSONException " + e.getMessage()); 
                    } catch (NullPointerException e){
                        Log.e("Model", "Probably failed to parse JSON " + msg.obj);
                    }
                    
                

                break;
            }

        }
    }
    private enum State{
        not_connected, connecting, connected, authenticated;
    }


}
