package se.chalmers.fleetspeak.fragments.NewStructure.DummyModel;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import se.chalmers.fleetspeak.Model;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.User;

/**
 * Created by David Gustafsson on 2015-08-06.
 */
public class DummyModel extends Model {
    private boolean connected = false;
    private int currentRoom = 1;
    private ArrayList<User> users1 = new ArrayList<User>();
    private ArrayList<User> users2 = new ArrayList<User>();
    private ArrayList<User> users3 = new ArrayList<User>();


    private ArrayList<Room> rooms = new ArrayList<Room>();
    public DummyModel(Context context, Handler callbackHandler) {
        super(context, callbackHandler);
        Room room1 = new Room("First", 1);
        Room room2 = new Room("Second", 2);
        users1.add(new User("Henrik", 1));

        users1.add(new User("Bob", 2));
        users2.add(new User("Anna", 3));
        users2.add(new User("Dave", 4));

        users3.add(new User("Default", 6));
        rooms.add(room1);
        rooms.add(room2);
    }

    @Override
    public void connect(String ip, int port) {
        connected = true;
        Log.d("DummyModel", "Connection established");
    }

    @Override
    public ArrayList<Room> getRooms() {
        Log.d("DummyModel", "Room called");
        return rooms;
    }

    @Override
    public int getCurrentRoom() {
        if(connected) {
            return currentRoom;
        }
        else{
            Log.d("DummyModel", "notConnected getCurrentRoom");
            return -1;
        }
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public void move(int roomid) {
        if(connected)
        for (Room r: rooms){
            if(r.getId() == roomid){
                currentRoom = rooms.indexOf(r);
            }
        }
    }

    @Override
    public void moveNewRoom(String roomname) {
        if(connected) {
            Room newRoom = new Room("roomname" + rooms.size() + 1, 40 + rooms.size());
            rooms.add(newRoom);
            currentRoom = rooms.indexOf(newRoom);
        }
    }

    @Override
    public ArrayList<User> getUsers(int roomid) {
       if(connected) {
           if (roomid == 1) {
               return users1;
           } else if (roomid == 2) {
               return users2;
           } else {
               return users3;
           }
       }
        return null;
    }

    public boolean isConnected() {
        return connected;
    }

}
