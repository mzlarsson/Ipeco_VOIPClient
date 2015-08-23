package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import se.chalmers.fleetspeak.util.MessageValues;
import se.chalmers.fleetspeak.util.UserInfoPacket;

/**
 * A class for keeping tracks of which room the different users are in.
 * Created by Patrik on 2014-10-07.
 */
public class RoomHandler{

    private HashMap<Room,ArrayList<User>> rooms;

    private User activeUser;

    private Messenger updateMessenger;


    public RoomHandler(Handler updateListener) {
        rooms = new HashMap<>();
        updateMessenger = new Messenger(updateListener);

    }

    public int getUserid() {
        return activeUser.getId();
    }

    public void setUserInfo(UserInfoPacket user) {
        activeUser = new User(user.getName(), user.getID());
    }

    /**
     * Adds an user to a room
     * @param user The user to be added
     * @param roomid The id of the destination room
     */

    public void addUser(User user, int roomid) {
        Room room = findRoom(roomid);
        rooms.get(room).add(user);
        postUpdate(MessageValues.MODELCHANGED);
    }

    /**
     * Removes a user found in the room it's residing in
     * @param user The user to be removed
     */
    public void removeUser(User user) {
        for (Room room : rooms.keySet()) {
            ArrayList<User> userList = rooms.get(room);
            if (userList.contains(user)) {
                userList.remove(user);
                Log.i(this.getClass().toString(), "user removed");
                postUpdate(MessageValues.MODELCHANGED);
                break;
            }
        }
    }


    /**
     * Removes a user from the room
     * @param userID The ID of the user to be removed
     */
    public void removeUser(int userID) {
        removeUser(getUser(userID));
    }


    /**
     * Moves a user to a specified
     * @param userid ID of the user to be moved
     * @param targetRoomID The ID of the destination room
     */
    public void moveUser(int userid, int targetRoomID) {
        User user = getUser(userid);
        removeUser(user);
        addUser(user, targetRoomID);
    }

    /**
     * Returns a list of all the rooms
     * @return An arrayList containing all the rooms
     */
    public ArrayList<Room> getRooms() {
        return new ArrayList<>(rooms.keySet());
    }

    /**
     * Returns all the users in a room
      * @param room The room to get all the users from
     * @return An ArrayList containing all the users from the room
     */
    public ArrayList<User> getUsers(Room room) {
        if(rooms.get(room) != null)
            return rooms.get(room);
        return new ArrayList<>();
    }

    /**
     * @param id The ID of the room.
     * @return List of users in the room with the given id
     *
     */
    public ArrayList<User>getUsers(int id) {
        Log.d("RoomHandeler", getUsers(findRoom(id)) + "");
        return getUsers(findRoom(id));
    }

    /**
     * Finds the first user with the specified id.
     * @param id The ID of the user.
     * @return The User if found.
     */
    public User getUser(int id) throws NoSuchElementException{
        if (id == activeUser.getId()) {
            return activeUser;
        }
        for (ArrayList<User> users : rooms.values()) {
            for (User user : users) {
                if (user.getId()==id) {
                    return user;
                }
            }
        }
        throw new NoSuchElementException("A user with ID: \"" + id + "\" doesn't exit.");
    }
    public int getCurrentRoom(){
        for(Room r : rooms.keySet()){
            for(User u : rooms.get(r)){
                if(u.getId() == activeUser.getId())
                    return r.getId();
            }
        }
        return 0;
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
        return null;
    }

    @Override
    public String toString() {
        String outPrint = "";
        for (Room room : rooms.keySet()) {
            outPrint += "Room: " + (room.toString()+"\n");
            for (User user : rooms.get(room)) {
                outPrint += "User: " + ("\t" + user.toString() + "\n");
            }
        }
        return outPrint;
    }

    public void changeUsername(int userid, String name){
        Log.d("changeusername", userid + " to " + name);
        getUser(userid).setName(name);
        postUpdate(MessageValues.MODELCHANGED);
    }


    public void addRoom(int roomid, String roomname) {
        Room room = new Room(roomname, roomid);
        ArrayList<User> list = new ArrayList<User>();
        rooms.put(room, list);
        postUpdate(MessageValues.MODELCHANGED);
    }

    public void removeRoom(int roomid) {
        rooms.remove(findRoom(roomid));
    }

    public void changeRoomName(int roomid, String roomname) {
        //findRoom(roomid).setName(roomname);
        postUpdate(MessageValues.MODELCHANGED);
    }

    private void postUpdate(int what){
        try {
            Log.d("RoomHandler", ""+ rooms);
            updateMessenger.send(Message.obtain(null, what));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void clear(){
        rooms.clear();
    }
}
