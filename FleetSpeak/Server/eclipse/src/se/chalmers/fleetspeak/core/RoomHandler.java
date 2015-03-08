package se.chalmers.fleetspeak.core;

import java.util.ArrayList;
import java.util.HashMap;

import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log;
/**
 * A Singleton class for handling the rooms and client on the server side.
 * Based on the RoomHandler on the client side.
 * @author David Michaelsson
 * @author Patrik Haar
 * @author Matz Larsson
 */
public class RoomHandler {
	
	private static RoomHandler instance;
	
    private HashMap<Room,ArrayList<Client>> rooms;
	private Room defaultRoom;
	
	/**
	 * Constructor for the RoomHandler, creates the room-structure and the default lobby-
	 * Only used internally in getInstance() due to it being a singleton. 
	 */
	private RoomHandler(){
		Log.logDebug("Creating a new RoomHandler");
		rooms = new HashMap<Room,ArrayList<Client>>();
		defaultRoom = new Room("Lobby", true);
	}
	
	/**
	 * Get the singleton instance of the RoomHandler class.
	 * @return the active RoomHandler instance.
	 */
	public static RoomHandler getInstance() {
		if (instance == null) {
			instance = new RoomHandler();
		}
		return instance;
	}
	
	/**
	 * Adds a client to the room specified, initializes the room if newly created.
	 * @param c The Client to add to the room
	 * @param r The targeted Room
	 */
	public void addClient(Client c, Room r){
		if(c != null && r != null){
			if (!rooms.containsKey(r) || rooms.get(r) == null) {
	            ArrayList<Client> list = new ArrayList<Client>();
	            c.moveToRoom(list);
	            list.add(c);
	            rooms.put(r,list);
	            EventBus.postEvent("broadcast", new Command("createdRoom", r.getId(), r.getName()), this);
			}else{
				 ArrayList<Client> list = rooms.get(r);
				 if(!list.contains(c)){
					 for (Client listeners : list) {
						 listeners.requestListeningClient(c);
					 }
					 c.moveToRoom(list);
					 list.add(c);
				 }
			}
		}
	}
	
	/**
	 * Adds a client to a room.
	 * @param c the client to be added.
	 * @param roomID the ID of the room to add the client to.
	 */
	public void addClient(Client c, int roomID){
		this.addClient(c, findRoom(roomID)); 
	}
	
	/**
	 * Adds a client to the default room.
	 * NOTE: Call on this only when the user connected to server, not on move actions.
	 * @param client the client to be added.
	 */
	public void addClient(Client client) {
		addClient(client, defaultRoom);
		EventBus.postEvent("broadcast", new Command("addedUser", client.getClientID(), defaultRoom.getId()), this);
	}

	/**
	 * Removes the given clients connection to its room and removes it completely if "terminate" is true
	 * @param c The Client to be removed
	 * @param terminate true if the the Client should be removed completely
	 * false to just remove the room tracking, used for switching rooms.
	 */
	public boolean removeClient(Client c, boolean terminate){
		if(c != null){
			for(Room r : rooms.keySet()){
				ArrayList<Client> clientList = rooms.get(r);
				if(clientList.contains(c)){
					clientList.remove(c);
					for (Client listeners : clientList) {
						listeners.removeListeningClient(c);
					}
					c.removeAllListeningClients();
					if (terminate) {
						c.terminate();
						EventBus.postEvent("broadcast", new Command("removedUser", c.getClientID(), null), this);
					}
					if(clientList.isEmpty() && !r.isPermanent()){
						removeRoom(r);
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Removes the given clients connection to its room and removes it completely if "terminate" is true
	 * @param clientID The ID of the Client to be removed
	 * @param terminate true if the the Client should be removed completely
	 * false to just remove the room tracking, used for switching rooms.
	 */
	public boolean removeClient(int clientID, boolean terminate){
		return removeClient(findClient(clientID), terminate);
	}

	/**
	 * Removes the client from its current room.
	 * @param clientID the ID of the client to be removed.
	 */
	public boolean removeClient(int clientID){
		return removeClient(findClient(clientID), false);
	}
	
	/**
	 * Removes a room permanently.
	 * NOTE: The room will be removed regardless of the permanent property
	 * @param room The room to remove
	 * @return <code>true</code> if the room was removed
	 */
	public boolean removeRoom(Room room){
		if(room != null){
			Log.logDebug("Removing room");
			rooms.remove(room);
			room.terminate();
			EventBus.postEvent("broadcast", new Command("removedRoom", room.getId(), null), this);
			return true;
		}
		
		return false;
	}

	/**
	 * Removes a room permanently.
	 * NOTE: The room will be removed regardless of the permanent property
	 * @param roomID The ID of the room to remove
	 * @return <code>true</code> if the room was removed
	 */
	public boolean removeRoom(int roomID){
		return removeRoom(findRoom(roomID));
	}
	
	/**
	 * Moves the Client from its current room to the room specified
	 * @param c The Client to be moved.
	 * @param r The targeted Room.
	 */
	public boolean moveClient(Client c, Room r){
		if(c!=null && r!=null){
			this.removeClient(c, false);
			this.addClient(c, r);
			EventBus.postEvent("broadcast", new Command("movedUser", c.getClientID(), r.getId()), this);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Moves the Client from its current room to the room specified
	 * @param clientID The ID of the Client to be moved.
	 * @param r The targeted Room.
	 */
	public boolean moveClient(int clientID, Room room){
		return moveClient(this.findClient(clientID), room);
	}
	
	/**
	 * Moves the Client from its current room to the room specified
	 * @param clientID The ID of the Client to be moved.
	 * @param roomID The ID of the targeted Room.
	 */
	public boolean moveClient(int clientID, int roomID){
		return moveClient(this.findClient(clientID), this.findRoom(roomID));
	}
	
	/**
	 * Get all the available rooms.
	 * @return An array with the available rooms.
	 */
	public Room[] getRooms(){
		return rooms.keySet().toArray(new Room[rooms.keySet().size()]);
	}
	
	/**
	 * Get all clients in a given room.
	 * @param r the room to get the clients from.
	 * @return An array with all clients in the room.
	 */
	public Client[] getClients(Room r){
		return rooms.get(r).toArray(new Client[rooms.get(r).size()]);
	}
	
	/**
	 * Sets the username of a client.
	 * @param clientID the ID of the client to set the name of.
	 * @param name the new name of the client.
	 */
	public boolean setUsername(int clientID, String name) {
		Client c = findClient(clientID);
		if(c != null){
			c.setName(name);
			EventBus.postEvent("broadcast", new Command("changedUsername", c.getClientID(), c.getName()), this);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets the name of a room.
	 * @param roomID the ID of the room to set the name of.
	 * @param name the new name of the room.
	 */
	public boolean setRoomName(int roomID, String name) {
		Room r = findRoom(roomID);
		if(r != null){
			r.setName(name);
			EventBus.postEvent("broadcast", new Command("changedRoomName", r.getId(), r.getName()), this);
			return true;
		}
		
		return false;
	}

	/**
	 * Finds the client with the given ID.
	 * @param clientID the ID of the client to be found.
	 * @return the client if found, null if not found.
	 */
	public Client findClient(int clientID) {
		for(ArrayList<Client> clients:rooms.values()){
			for(Client c: clients){
				if(c.getClientID()==clientID){
					return c;
				}
			}
		}
		
		Log.logError("\tDid not find client with ID </error><b>"+clientID+"</b>");
		return null;
	}

	/**
	 * Finds the room with the given ID.
	 * @param roomID the ID of the room to be found.
	 * @return the room if found, null if not found.
	 */
	public Room findRoom(int roomID) {
		for(Room room:rooms.keySet() ){
			if(room.getId()==roomID){
				return room;
			}
		}

		Log.logError("\tDid not find room with ID </error><b>"+roomID+"</b>");
		return null;
	}

	/**
	 * Closes and removes everything held by the RoomHandler.
	 */
	public void terminate() {
		for(ArrayList<Client> clients:rooms.values()){
			for(Client c: clients){
				c.terminate();
			}
		}
		rooms.clear();
	}
}
