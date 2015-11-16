package se.chalmers.fleetspeak.util;

import java.util.LinkedList;

/**
 * Created by Nieo on 10/11/15.
 */
public class RoomHistory{

    private LinkedList<Integer> pastRooms;
    private int MAXSIZE = 5;

    public RoomHistory(){
        pastRooms = new LinkedList<>();
    }


    public void addRoom(Integer e){
        if(pastRooms.contains(e)){
            pastRooms.remove(e);
        }

        pastRooms.addFirst(e);
        if(pastRooms.size() > MAXSIZE){
            pastRooms.removeLast();
        }
    }

    public LinkedList<Integer> getPastRooms(){
        return (LinkedList<Integer>) pastRooms.clone();
    }


}
