package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.util.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import se.chalmers.fleetspeak.util.UserInfoPacket;
import java.util.logging.Logger;

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

	private Logger logger;
	
	/**
	 * Constructor for the RoomHandler, creates the room-structure and the default lobby-
	 * Only used internally in getInstance() due to it being a singleton. 
	 */
	private RoomHandler(){
		logger = Logger.getLogger("Debug");
		logger.log(Level.FINE,"Creating a new RoomHandler");
		rooms = new HashMap<Room,ArrayList<Client>>();
		defaultRoom = new Room("Lobby", true);
		addRoom(defaultRoom, true);
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
	public boolean addClient(Client c, Room r){
		if(c != null && r != null){
			if (rooms.containsKey(r)){
				 ArrayList<Client> list = rooms.get(r);
				 if(!list.contains(c)){
					 for (Client listeners : list) {
						 listeners.requestListeningClient(c);
					 }
					 c.moveToRoom(list);
					 list.add(c);
					 return true;
				 }
			}
		}
		
		return false;
	}
	
	/**
	 * Adds a client to a room.
	 * @param c the client to be added.
	 * @param roomID the ID of the room to add the client to.
	 */
	public boolean addClient(Client c, int roomID){
		 return addClient(c, findRoom(roomID)); 
	}
	
	/**
	 * Adds a client to the default room.
	 * NOTE: Call on this only when the user connected to server, not on move actions.
	 * @param client the client to be added.
	 */
	public boolean addClient(Client client) {
		if(addClient(client, defaultRoom)){
			UserInfoPacket user = client.getInfoPacket().setRoomID(defaultRoom.getId());
			EventBus.postEvent("broadcast", new Command("addedUser", user, null), this);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Adding an empty room to the roomhandler
	 * @param room The room to add
	 * @param force If the room should be added even though its permanent value is false
	 * @return <code>true</code> if the roomhandler was changed by this action
	 */
	public boolean addRoom(Room room, boolean force){
		if(rooms.get(room) == null && (force || room.isPermanent())){
			rooms.put(room, new ArrayList<Client>());
            EventBus.postEvent("broadcast", new Command("createdRoom", room.getId(), room.getName()), this);
			return true;
		}
		
		return false;
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
			logger.log(Level.FINE,"Removing room");
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
			if(removeClient(c, false) && addClient(c, r)){
				EventBus.postEvent("broadcast", new Command("movedUser", c.getClientID(), r.getId()), this);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Moves the Client from its current room to the room specified
	 * @param clientID The ID of the Client to be moved.
	 * @param room The targeted Room.
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
	public Set<Room> getRooms(){
		return rooms.keySet();
	}
	
	/**
	 * Get all clients in a given room.
	 * @param r the room to get the clients from.
	 * @return An array with all clients in the room.
	 */
	public List<Client> getClients(Room r){
		return rooms.get(r);
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
		
		logger.log(Level.WARNING, "\tDid not find client with ID " + clientID );
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

		logger.log(Level.WARNING, "\tDid not find room with ID " + roomID);
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
