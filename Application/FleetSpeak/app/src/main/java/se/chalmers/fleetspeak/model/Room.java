package se.chalmers.fleetspeak.model;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for representing a room.
 * Created by Patrik on 2014-10-07.
 */
public class Room {
    private String name;
    private int id;

    private ConcurrentHashMap<Integer, User> users;


    public Room(String name, int id) {
        this.name = name;
        this.id = id;
        users = new ConcurrentHashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addUser(User u){
        try {
            users.put(u.getId(), u);
        }catch(NullPointerException e){
            Log.e("Room", "already moved the user here");
        }
    }

    public User removeUser(int userid){
        User u = users.get(userid);
        users.remove(userid);
        return u;
    }

    /**
     * Updates the location of a user.
     * @param userID The ID of the user to update.
     * @param latitude The latitude of the new location.
     * @param longitude The longitude of the new location.
     */
    public void updateLocation(int userID, double latitude, double longitude) {
        User u = users.get(userID);
        if (u != null) {
            users.get(userID).updateLocation(latitude, longitude);
        } else {
            Log.e("Room", "UserID:"+userID+" was not found when updating location");
        }
    }

    /**
     * Finds all users in this room within a given radius of a location.
     * Includes outdated user-locations but requests an update from the server.
     * @param location The center of the circle area to search in.
     * @param distance The radius of the circle in meters.
     * @return All users found, including outdated ones. Array is empty if no users were found.
     */
    public ArrayList<User> getUserCloserThan(Location location, int distance) {
        ArrayList<User> foundUsers = new ArrayList<>();
        for (User user : users.values()) {
            if (!user.isLocationUpdated()) {
                //TODO request update of this user's location from the server.
            }
            if (user.getDistanceTo(location) <= distance) {
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }

    public ArrayList<User> getUsers(){
        ArrayList<User> list = new ArrayList<>();
        for(Integer i: users.keySet()){
            list.add(users.get(i));
        }
        return list;
    }


    public void changeName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Room " + name + " " + id + "\n");
        for(int i: users.keySet()){
            sb.append(users.get(i).getName());
            sb.append(" ");
            sb.append(users.get(i).getId());
            sb.append("\n");
        }
        return sb.toString();

    }
}
