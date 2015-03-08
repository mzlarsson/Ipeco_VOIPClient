package se.chalmers.fleetspeak;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

import se.chalmers.fleetspeak.sound.SoundController;

/**
 * Created by Nieo on 08/03/15.
 */
public class Model {
    private RoomHandler roomHandler;
    private CommandHandler commandHandler;
    private Connector connector;
    private SoundController soundController;
    private Handler callbackHandler;


    public Model(final Context context){
        roomHandler = new RoomHandler();
        commandHandler = new CommandHandler(roomHandler);
        connector = new Connector(commandHandler);
        callbackHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Connector.CONNECTED:
                        //TODO send message to Activity that connection successful
                        soundController = new SoundController(context, (String)msg.obj, msg.arg1);
                    break;
                    case Connector.DISCONNECTED:
                        //TODO send message to Activity that user disconnected
                        soundController.close();
                    break;
                    default:
                        Log.d("Model", "No command with id" + msg.what);
                    break;
                }
            }
        };
    }

    public ArrayList<Room> getRooms(){
        return roomHandler.getRooms();
    }
    public ArrayList<User> getUsers(int roomid){
        return roomHandler.getUsers(roomid);
    }

    public void connect(String ip, int port){
        connector.connect(callbackHandler, ip, port);
    }

    public void disconnect(){
        connector.discconect(callbackHandler);
    }
    public void setName(String name){
        connector.setName(callbackHandler, name);
    }
    public void move(int roomid){
        connector.move(callbackHandler, roomid);
    }
    public void moveNewRoom(String roomname){
        connector.moveNewRoom(callbackHandler, roomname);
    }



}
