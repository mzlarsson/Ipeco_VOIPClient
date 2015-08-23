package se.chalmers.fleetspeak;

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
        users = new ConcurrentHashMap();
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
            Log.e("Room", "already moved the user from here");
        }
    }

    public User removeUser(int userid){
        User u = users.get(userid);
        users.remove(userid);
        return u;

    }

    public ArrayList getUsers(){
        ArrayList list = new ArrayList();
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
            sb.append(users.get(i).getName() + " " + users.get(i).getId() + "\n");
        }
        return sb.toString();

    }
}
