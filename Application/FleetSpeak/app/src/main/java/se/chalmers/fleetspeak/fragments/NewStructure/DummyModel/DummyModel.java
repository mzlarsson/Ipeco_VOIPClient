package se.chalmers.fleetspeak.fragments.NewStructure.DummyModel;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

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
    public DummyModel( Handler callbackHandler) {
        super(callbackHandler);
        Room room1 = new Room("First", 0);
        Room room2 = new Room("Second", 1);
        users1.add(new User("Henrik", 1));

        users1.add(new User("Bob", 2));
        users2.add(new User("Anna", 3));
        users2.add(new User("Dave", 4));

        users3.add(new User("Default", 6));
        rooms.add(room1);
        rooms.add(room2);
    }

    @Override
    public void connect(String ip, String pwd) {
        connected = true;
        Log.d("DummyModel", "Connection established");
    }

    @Override
    public ArrayList<Room> getRooms() {
        if(connected) {
            Log.d("DummyModel", "Room called");
            return rooms;
        }else {
            return null;
        }
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
            Log.d("DummyModel", " new room and move");
            Room newRoom = new Room("roomname" + rooms.size() + 1, 40 + rooms.size());
            rooms.add(newRoom);
            currentRoom = rooms.indexOf(newRoom);
        }
    }

    @Override
    public ArrayList<User> getUsers(int roomid) {
       if(connected) {
           if (roomid == 0) {
               return users1;
           } else if (roomid == 1) {
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

    public void createAndMoveRoom() {

    }
}
