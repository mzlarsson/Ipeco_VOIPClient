package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.util.IDFactory;
import se.chalmers.fleetspeak.util.Log;

/**
 * A class for representing a room.
 * Created by Patrik on 2014-10-07.
 */
public class Room {
    private String name;
    private int id;

    public Room(String name) {
        this(name, IDFactory.getInstance().getID());
    }
    public Room(String name, int id) {
    	Log.logDebug("Creating room");
    	this.name = name;
    	this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
        	return true;
        }
        
        if (o == null || getClass() != o.getClass()){
        	return false;
        }

        Room room = (Room) o;

        if (id != room.id){
        	return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    /**
     * Frees all resources used by the room.
     */
    public void terminate() {
		IDFactory.getInstance().freeID(id);
    }
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Room: name=" + name + ", id=" + id;
	}

}
