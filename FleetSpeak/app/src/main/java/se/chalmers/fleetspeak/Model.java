package se.chalmers.fleetspeak;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by Nieo on 08/03/15.
 */
public class Model {
    private RoomHandler roomHandler;
    private CommandHandler commandHandler;
    private Connector connector;
    private Handler callbackHandler;


    public Model(final Context context, Handler callbackHandler){
        roomHandler = new RoomHandler(callbackHandler);
        commandHandler = new CommandHandler(roomHandler, context);
        connector = new Connector(commandHandler);
        this.callbackHandler = callbackHandler;
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
        connector.disconnect(callbackHandler);
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
    public int getCurrentRoom(){
        return roomHandler.getCurrentRoom();
    }


}
