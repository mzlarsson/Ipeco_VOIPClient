package se.chalmers.fleetspeak.model;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 20/08/15.
 * Updated by Patrik on 2015-11-14.
 */
public class Building {

    private ConcurrentHashMap<Integer, Room> rooms;
    private Handler handler;

    private int activeUserid;
    private int currentRoom;

    protected Building(Handler handler) {
        rooms = new ConcurrentHashMap<>();
        this.handler = handler;
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    public void addRoom(int roomid, String roomname) {
        rooms.put(roomid, new Room(roomname, roomid));
        postUpdate();
    }
    public Room getRooom(int roomid){
        if(rooms.containsKey(roomid)){
            return rooms.get(roomid);
        }else{
            return null;
        }
    }

    public void removeRoom(int roomid) {
        rooms.remove(roomid);
        postUpdate();
    }

    public void addUser(int userid, String name, int roomid) {
        if (userid == activeUserid)
            currentRoom = roomid;
        Room r = rooms.get(roomid);
        r.addUser(new User(name, userid));
        postUpdate();
    }

    public void removeUser(int userid, int roomid) {
        Room r = rooms.get(roomid);
        if(r != null) {
            r.removeUser(userid);
            postUpdate();
        }
    }

    public void moveUser(int userid, int sourceid, int destinationid) {
        Log.d("Building", "moveuser " + userid + ":" + sourceid + ":" + destinationid);
        if (activeUserid == userid) {
            currentRoom = destinationid;
        Log.d("Building", "updateing current room to " + currentRoom);
        }
        User u = rooms.get(sourceid).removeUser(userid);
        rooms.get(destinationid).addUser(u);
        postUpdate();
    }

    /**
     * Updates the location of a user.
     * @param roomID The ID of the room the user is in.
     * @param userID The ID of the user to update.
     * @param latitude The latitude of the new location.
     * @param longitude The longitude of the new location.
     */
    public void updateLocation(int roomID, int userID, double latitude, double longitude) {
        Room r = rooms.get(roomID);
        if (r != null) {
            rooms.get(roomID).updateLocation(userID, latitude, longitude);
        } else {
            Log.e("Building", "RoomID:"+roomID+" was not found when updating location");
        }
    }

    public ArrayList<Room> getRooms() {
        ArrayList<Room> list = new ArrayList<>();
        for (Integer i : rooms.keySet()) {
            list.add(rooms.get(i));
        }
        return list;
    }

    /**
     * Finds all rooms with a user within a given radius of a location.
     * Requests an update of the location on outdated users.
     * @param location The center of the circle area to search in.
     * @param distance The radius of the circle in meters.
     * @return All rooms found, array is empty if no rooms were found.
     */
    public ArrayList<Room> getRoomsCloserThan(Location location, int distance) {
        ArrayList<Room> foundRooms = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (!room.getUserCloserThan(location, distance).isEmpty()) {
                foundRooms.add(room);
            }
        }
        return foundRooms;
    }

    /**
     * Finds all users in this room within a given radius of a location.
     * Includes outdated user-locations but requests an update from the server.
     * @param location The center of the circle area to search in.
     * @param distance The radius of the circle in meters.
     * @return All users found, including outdated ones. Array is empty if no users were found.
     */
    public ArrayList<User> getUsersCloserThan(Location location, int distance) {
        ArrayList<User> foundUsers = new ArrayList<>();
        for (Room room : rooms.values()) {
            foundUsers.addAll(room.getUserCloserThan(location, distance));
        }
        return foundUsers;
    }

    public ArrayList<User> getUsers(int roomid) {
        if (rooms.keySet().contains(roomid)) {
            return rooms.get(roomid).getUsers();
        }

        return null;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public int getUserid() {
        return activeUserid;
    }

    public void setUserid(int userid) {
        activeUserid = userid;
    }

    public void changeRoomName(int roomid, String name) {
        rooms.get(roomid).changeName(name);
        postUpdate();
    }

    public void postUpdate() {
        Log.d("Building", toString());
        handler.sendEmptyMessage(MessageValues.MODELCHANGED);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Current Builidng state\n");
        for (int i : rooms.keySet()) {
            sb.append(rooms.get(i).toString());
        }
        return sb.toString();
    }

    public void clear() {
        rooms.clear();
    }
}
