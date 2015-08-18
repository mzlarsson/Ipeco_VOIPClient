package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import se.chalmers.fleetspeak.Network.TLSConnector;
import se.chalmers.fleetspeak.audio.sound.SoundController;
import se.chalmers.fleetspeak.audio.sound.SoundHandler;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.MessageValues;
import se.chalmers.fleetspeak.util.UserInfoPacket;

/**
 * Created by Nieo on 08/03/15.
 */
public class Model {
    private RoomHandler roomHandler;
    private CommandHandler commandHandler;
    private TLSConnector connector;
    private Handler callbackHandler;
    private String remoteIP;

    private SoundController soundController;
    private State state;

    private SoundHandler soundHandler;

    public Model(Handler callbackHandler){
        state = State.not_connected;
        roomHandler = new RoomHandler(callbackHandler);
        commandHandler = new CommandHandler();
        connector = new TLSConnector(commandHandler);
        this.callbackHandler = callbackHandler;

        soundHandler = new SoundHandler();
        Thread t = new Thread(soundHandler,"SoundHandler");
        t.start();
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
            connector.connect(ip, port);
        }else{
            Log.d("Model", "Already connected or trying to connect");
        }
    }

    public void disconnect(){
        if(state == State.authorized || state == State.connected){
            state = State.not_connected;
            connector.disconnect();
        }else{
            Log.d("Model","Not connected, cannot send disconnect");
        }

    }
    public void setName(String name){
        connector.sendMessage(new Command("setname", name, null));
    }
    public void move(int roomid){
        if(roomid != roomHandler.getCurrentRoom()){
            roomHandler.moveUser(roomHandler.getUserid(), roomid);
            connector.sendMessage(new Command("move", roomid,null));
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
                    break;
                case "connectionfailed":
                    state = State.not_connected;
                    break;
                case "disconnected":
                    state = State.not_connected;
                    roomHandler.clear();
                    break;
                case "setinfo":
                    roomHandler.setUserInfo((UserInfoPacket) command.getKey());
                    break;
                case "addeduser":
                    UserInfoPacket user = (UserInfoPacket)command.getKey();
                    int roomid = (int)command.getValue();
                    roomHandler.addUser(new User(user.getName(), user.getID()), roomid);
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
                    //int port = soundController.addStream((Integer) command.getKey());
                    //try {
                    //    msg.replyTo.send(Message.obtain(null, MessageValues.SETSOUNDPORT, port, 0, command.getKey()));
                    //} catch (RemoteException e) {
                    //    e.printStackTrace();
                    //}

                    break;
                case "usesoundport":
                    //soundController = new SoundController(context, remoteIP, (Integer) command.getKey());
                    break;
                case "sendauthenticationdetails":

                        connector.sendMessage(new Command("authenticationdetails", "v", null)); //TODO "v" should be the given username.
                       //msg.replyTo.send(Message.obtain(null, MessageValues.AUTHENTICATIONDETAILS, Utils.getUsername()));

                    break;
                case "authorizationresult":
                    if ((boolean) command.getKey()) { // true if successfully authorized
                        state = State.authorized;
                        try {
                            new Messenger(callbackHandler).send(Message.obtain(null, MessageValues.AUTHORIZED));
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

            //Log.d("CommandHandler", "Rooms " + roomHandler.toString());


        }
    }
    private enum State{
        not_connected, connecting, connected, authorized;
    }


}
