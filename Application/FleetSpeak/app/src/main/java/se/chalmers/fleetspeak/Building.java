package se.chalmers.fleetspeak;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 20/08/15.
 */
public class Building {

    private ConcurrentHashMap<Integer, Room> rooms;
    private Messenger updateMessenger;

    private int activeUserid;

    private int currentRoom;

    public Building(Messenger updateMessenger) {
        rooms = new ConcurrentHashMap<>();
        this.updateMessenger = updateMessenger;
    }

    public void addRoom(int roomid, String roomname) {
        rooms.put(roomid, new Room(roomname, roomid));
        postUpdate();
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

    public void removeUser(int roomid, int userid) {
            rooms.get(roomid).removeUser(userid);
            postUpdate();
    }

    public void moveUser(int userid, int sourceid, int destinationid) {
        if (activeUserid == userid)
            currentRoom = destinationid;
        User u = rooms.get(sourceid).removeUser(userid);
        rooms.get(destinationid).addUser(u);
        postUpdate();
    }

    public ArrayList getRooms() {
        ArrayList list = new ArrayList();
        for (Integer i : rooms.keySet()) {
            list.add(rooms.get(i));
        }
        return list;
    }

    public ArrayList getUsers(int roomid) {
        if (rooms.keySet().contains(roomid))
            return rooms.get(roomid).getUsers();
        return null;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public int getUserid() {
        return activeUserid;
    }

    public void setUserInfo(int userid) {
        activeUserid = userid;
    }

    public void changeRoomName(int roomid, String name) {
        rooms.get(roomid).changeName(name);
        postUpdate();
    }

    public void postUpdate() {
        try {
            Log.d("Building", toString());
            updateMessenger.send(Message.obtain(null, MessageValues.MODELCHANGED));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
        rooms = new ConcurrentHashMap<>();
    }
}
