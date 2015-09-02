package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import se.chalmers.fleetspeak.Network.TCP.TLSConnector;
import se.chalmers.fleetspeak.Network.UDP.RTPHandler;
import se.chalmers.fleetspeak.Network.UDP.STUNInitiator;
import se.chalmers.fleetspeak.Network.UDP.UDPConnector;
import se.chalmers.fleetspeak.audio.sound.SoundOutputController;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.MessageValues;
import se.chalmers.fleetspeak.util.UserInfoPacket;

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

    public Model(Handler callbackHandler){
        state = State.not_connected;
        building = new Building(new Messenger(callbackHandler));
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
            soundOutputController.destroy();
            rtpHandler.terminate();
        }else{
            Log.d("Model", "Not connected, cannot send disconnect");
        }

    }
    public void move(int roomid){
        if(state == State.authenticated &&
                roomid != building.getCurrentRoom()){
            Log.d("Model", "Moving to " + roomid);
            building.moveUser(building.getUserid(), building.getCurrentRoom(),roomid);
            connector.sendMessage(new Command("move", building.getCurrentRoom(),roomid));
        }
        Log.d("Model", roomid + " " + building.getCurrentRoom() );
    }
    public void moveNewRoom(String roomname){
        Log.d("Model", state.toString());
        if(state == State.authenticated) {
            Log.d("Model", "Moving to " + roomname);
            connector.sendMessage(new Command("movenewroom", roomname, null));
        }
    }
    public int getCurrentRoom(){
        return building.getCurrentRoom();
    }

    class CommandHandler extends Handler {
        public void handleMessage(Message msg) {

            switch(msg.what) {
                case MessageValues.CONNECTED:
                    state = State.connected;
                    break;
                case MessageValues.DISCONNECTED://TODO send message to gui
                    state = State.not_connected;
                    rtpHandler.terminate();
                    break;
                case MessageValues.CONNECTIONFAILED://TODO send message to gui
                    state = State.not_connected;
                    break;
                case MessageValues.UDPCONNECTOR:
                    Log.i("Commandhandler", "got a udpconnector");
                    connector.sendMessage(new Command("clientUdpTestOk", null, null));
                    rtpHandler = new RTPHandler((UDPConnector)msg.obj);
                    soundOutputController = new SoundOutputController(rtpHandler);
                    break;
                case MessageValues.COMMAND:

                Command command = (Command) msg.obj;
                String sCommand = command.getCommand();
                Log.i("Commandhandler", "Got the command " + sCommand + "(" + command.getKey() + "," + command.getValue() + ")");

                switch (sCommand.toLowerCase()) {
                    case "setinfo":
                        building.setUserInfo(((UserInfoPacket) command.getKey()).getID());
                        break;
                    case "addeduser":
                        UserInfoPacket user = (UserInfoPacket) command.getKey();
                        int roomid = (int) command.getValue();
                        building.addUser(user.getID(), user.getName(), roomid);
                        break;
                    case "changedroomname":
                        building.changeRoomName((Integer) command.getKey(), (String) command.getValue());
                    case "moveduser":
                        String[] rooms = ((String) command.getValue()).split(",");
                        building.moveUser((Integer) command.getKey(), Integer.parseInt(rooms[0]), Integer.parseInt(rooms[1]));
                        break;
                    case "createdroom":
                        building.addRoom((Integer) command.getKey(), (String) command.getValue());
                        break;
                    case "removeduser":
                        building.removeUser((Integer) command.getKey(), (Integer) command.getValue());
                        break;
                    case "removedroom":
                        building.removeRoom((Integer) command.getKey());
                        break;
                    case "initiatesoundport":
                        new STUNInitiator(connector.getIP(),(int)command.getKey(),(byte)command.getValue(), commandHandler);
                        break;
                    case "sendauthenticationdetails":
                        Log.d("auth", username);
                        connector.sendMessage(new Command("authenticationdetails", username, null));
                        break;
                    case "authenticationresult":
                        if ((boolean) command.getKey()) { // true if successfully authenticated
                            state = State.authenticated;
                            try {
                                new Messenger(callbackHandler).send(Message.obtain(null, MessageValues.AUTHENTICATED));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } else {
                            state = State.not_connected;
                            try {
                                new Messenger(callbackHandler).send(Message.obtain(null, MessageValues.AUTHENTICATIONFAILED, command.getValue())); // .getValue contains the reason for rejection
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }

                break;
            }

        }
    }
    private enum State{
        not_connected, connecting, connected, authenticated;
    }


}
