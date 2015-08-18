package se.chalmers.fleetspeak.util;

import java.io.Serializable;

/**
 * A class holding the information of a user for communication between server and client.
 * @author Patrik
 * @version 1.0
 * 
 */
public class UserInfoPacket implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int id, roomID;
	private String name;

	/**
	 * Construct a UserInfoPacket with the information of a user.
	 * @param id The ID of the user.
	 * @param name The name of the user.
	 */
    public UserInfoPacket(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * The ID of the user.
     * @return The ID of the user.
     */
    public int getID() {
        return id;
    }

    /**
	 * The name of the user.
	 * @return The name of the user.
	 */
	public String getName() {
	    return name;
	}

	/**
	 * The ID of the room the user is currently in.
	 * @return The ID of the room the user is currently in.
	 */
	public int getRoomID() {
	    return roomID;
	}

	/**
	 * Set the ID of the user.
	 * @param id the ID to set.
	 * @return The UserUnfoPacket with the new set ID.
	 */
	public UserInfoPacket setID(int id) {
		this.id = id;
		return this;
	}

	/**
	 * Set the name of the user.
	 * @param name the name to set.
	 * @return The UserUnfoPacket with the new set name.
	 */
	public UserInfoPacket setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Set the ID of the room the user is currently in.
	 * @param roomID the roomID to set.
	 * @return The UserUnfoPacket with the new set roomID.
	 */
	public UserInfoPacket setRoomID(int roomID) {
		this.roomID = roomID;
		return this;
	}
}