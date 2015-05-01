package se.chalmers.fleetspeak;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import se.chalmers.fleetspeak.Rooms.Room;
import se.chalmers.fleetspeak.Rooms.RoomHandler;
import se.chalmers.fleetspeak.Rooms.User;
import se.chalmers.fleetspeak.TCP.Connector;
import se.chalmers.fleetspeak.TCP.IConnector;
import se.chalmers.fleetspeak.sound.SoundController;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 08/03/15.
 */
public class Model {
    private RoomHandler roomHandler;
    private CommandHandler commandHandler;
    private IConnector connector;
    private Messenger guiMessenger;
    private String remoteIP;
    private Context context;
    private SoundController soundController;
    private State state;

    public Model(final Context context, Handler callbackHandler){
        state = State.not_connected;
        this.context = context;
        roomHandler = new RoomHandler(callbackHandler);
        commandHandler = new CommandHandler();
        connector = new Connector(commandHandler);
        guiMessenger = new Messenger(callbackHandler);
    }

    public ArrayList<Room> getRooms(){
        return roomHandler.getRooms();
    }
    public ArrayList<User> getUsers(int roomid){
        return roomHandler.getUsers(roomid);
    }

    public void connect(String ip, int port){
        if(state == State.not_connected){
            roomHandler.clear();
            state = State.connecting;
            connector.connect( ip, port);
        }else{
            Log.d("Model", "Already connected or trying to connect");
        }
    }

    public void disconnect(){
        if(state == State.connected){
            state = State.not_connected;
            connector.disconnect();
        }else{
            Log.d("Model","Not connected, cannot send setName");
        }

    }
    public void setName(String name){
        if(state == State.connected)
            connector.sendMessage(new Command("setname", name, null));
    }
    public void move(int roomid){
        if(roomid != roomHandler.getCurrentRoom()){
            roomHandler.moveUser(roomHandler.getUserid(), roomid);
            connector.sendMessage(new Command("move", roomid, null));
        }
    }
    public void moveNewRoom(String roomname){
        connector.sendMessage(new Command("movenewroom", roomname, null));
    }
    public int getCurrentRoom(){
        return roomHandler.getCurrentRoom();
    }
    public void pushToTalk(){
        if(soundController != null)
            soundController.pushToTalk();

    }
    public void muteUser(User user){
        Log.d("Model", "muteUser not implemented ");
    }
    public boolean isTalking(){
        if(soundController != null)
            return soundController.isTalking();
        return false;
    }

    public void getAssistance(int who){
        //TODO signal server
        Log.i("Model", "getAssistance is not implemented");
    }

    class CommandHandler extends Handler {
        public void handleMessage(Message msg) {

            Command command = (Command) msg.obj;
            String sCommand = command.getCommand();
            Log.i("Commandhandler", "Got the command " + sCommand + "(" + command.getKey() + "," + command.getValue() + ")");

            switch (sCommand.toLowerCase()){
                case "connected":
                    remoteIP = (String) command.getKey();
                    state = State.connected;
                    postUpdate(MessageValues.CONNECTED);
                    break;
                case "connectionfailed":
                    state = State.not_connected;
                    postUpdate(MessageValues.CONNECTIONFAILED);
                    break;
                case "disconnected":
                    state = State.not_connected;
                    roomHandler.clear();
                    postUpdate(MessageValues.DISCONNECTED);
                    break;
                case "setid":
                    roomHandler.setUserid((Integer) command.getKey());
                    break;
                case "addeduser":
                    roomHandler.addUser(new User((Integer)command.getKey()), (Integer) command.getValue());
                    break;
                case "changedusername":
                    roomHandler.changeUsername((Integer) command.getKey(), (String) command.getValue());
                    break;
                case "changedroomname":
                    roomHandler.changeRoomName((Integer) command.getKey(), (String) command.getValue());
                case "moveduser":
                    roomHandler.moveUser((Integer) command.getKey(), (Integer) command.getValue());
                    break;
                case "createdroom":
                    roomHandler.addRoom((Integer) command.getKey(), (String) command.getValue());
                    break;
                case "removeduser":
                    roomHandler.removeUser((Integer) command.getKey());
                    break;
                case "removedroom":
                    roomHandler.removeRoom((Integer) command.getKey());
                    break;
                case "requestsoundport":
                    int port = soundController.addStream((Integer) command.getKey());
                    connector.sendMessage(new Command("setsoundport", command.getKey(), port));
                    break;
                case "usesoundport":
                    soundController = new SoundController(context, remoteIP, (Integer) command.getKey());
                    break;
            }

            //Log.d("CommandHandler", "Rooms " + roomHandler.toString());




        }
        private void postUpdate(int what){
            try{
                guiMessenger.send(Message.obtain(null, what));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private enum State{
        not_connected, connecting, connected
    }


}
