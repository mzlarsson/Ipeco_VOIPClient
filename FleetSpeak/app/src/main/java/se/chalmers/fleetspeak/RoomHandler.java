package se.chalmers.fleetspeak;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * A class for keeping tracks of which room the different users are in.
 * Created by Patrik on 2014-10-07.
 */
public class RoomHandler{

    private HashMap<Room,ArrayList<User>> rooms;

    private Room defaultRoom;

    public RoomHandler() {
        rooms = new HashMap<Room,ArrayList<User>>();
        defaultRoom = new Room("Lobby",0);
    }

    public void addUser(User user, Room room) {
        if (!rooms.containsKey(room) || rooms.get(room) == null) {
            ArrayList<User> list = new ArrayList<User>();
            list.add(user);
            rooms.put(room,list);
        } else {
            ArrayList<User> list = rooms.get(room);
            if (!list.contains(user)) {
                list.add(user);
            }
        }
    }

    public void addUser(User user, int roomID) {
        addUser(user, findRoom(roomID));
    }
    public void addUser(User user){
        addUser(user,defaultRoom);
    }

    public void addToNewRoom(User user, String name){
        Room r = new Room(name);
        addUser(user, r);
    }


    public void removeUser(User user) {
        for (Room room : rooms.keySet()) {
            ArrayList<User> userList = rooms.get(room);
            if (userList.contains(user)) {
                userList.remove(user);
                Log.i(this.getClass().toString(), "user removed");
                if (userList.isEmpty()) {
                    if(room.getId() != 0)
                        rooms.remove(room);

                }
                break;
            }
        }
    }

    public void removeUser(int userID) {
        removeUser(getUser(userID));
    }

    public void moveUser(User user, Room targetRoom) {
        removeUser(user);
        addUser(user, targetRoom);
    }

    public void moveUser(int userID, int targetRoomID) {
        moveUser(getUser(userID), findRoom(targetRoomID));
    }

    public ArrayList<Room> getRooms() {
        return new ArrayList<Room>(rooms.keySet());
    }

    public ArrayList<User> getUsers(Room room) {
        if(rooms.get(room) != null)
            return rooms.get(room);
        return null;
    }

    /**
     * @param id The ID of the room.
     * @return List of users in the room with the given id
     *
     */
    public ArrayList<User>getUsers(int id) {
        return getUsers(findRoom(id));
    }
    /**
     * Finds the first user with the specified id.
     * @param id The ID of the user.
     * @return The User if found.
     */
    public User getUser(int id) throws NoSuchElementException{
        for (ArrayList<User> users : rooms.values()) {
            for (User user : users) {
                if (user.getId()==id) {
                    return user;
                }
            }
        }
        throw new NoSuchElementException("A user with ID: \"" + id + "\" doesn't exit.");
    }

    /**
     * Finds the room with the specified id.
     * @param id The ID of the room.
     * @return The Room if found.
     */
    private Room findRoom(int id)  {
        for (Room room : rooms.keySet()) {
            if (room.getId()==id) {
                return room;
            }
        }
        //throw new NoSuchElementException("A room with ID: \"" + id + "\" doesn't exit.");
        return null;
    }

    @Override
    public String toString() {
        String outPrint = "";
        for (Room room : rooms.keySet()) {
            outPrint += (room.toString()+"\n");
            for (User user : rooms.get(room)) {
                outPrint += ("\t" + user.toString() + "\n");
            }
        }
        return outPrint;
    }
}
