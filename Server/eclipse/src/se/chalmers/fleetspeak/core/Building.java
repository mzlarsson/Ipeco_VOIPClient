package se.chalmers.fleetspeak.core;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.fleetspeak.util.ChangeTracker;

/**
 * A Building is an essential part of the structure holding and handling many rooms.
 * 
 * @author Erik Pihl
 * @author Patrik Haar
 */
public class Building {

	private Logger logger = Logger.getLogger("Debug");

	private ConcurrentHashMap<Integer, IRoom> rooms;

	private ChangeTracker changeTracker;

	private BuildingManager manager = (cmd, room, client) -> {
		try{
			JSONObject command = new JSONObject(cmd);
			room = rooms.get(room.getId());
			switch(command.getString("command")){
			case "moveclient":
				moveClient(command.getInt("userid"), command.getInt("currentroom"), command.getInt("destinationroom"));	//XXX With this Clients can move other clients, intended? If not use client.getClientID instead of command.getInt("userid"). Same with room.
				break;
			case "movenewroom":
				moveClient(command.getInt("userid"), command.getInt("currentroom"), addRoom(command.getString("roomname"),false));	//XXX With this Clients can move other clients, intended? If not use client.getClientID instead of command.getInt("userid"). Same with room.
				break;
			case "createroom":
				addRoom(command.getString("roomname"), false);
				break;
			case "disconnect":
				removeClient(client, room);
				break;
			case "requestchangehistory":
				LinkedList<JSONObject> changes = changeTracker.getChanges(command.getInt("fromversion"));
				while(!changes.isEmpty()){
					room.sendCommandToClient(client.getClientID(), changes.removeFirst().toString());
				}
				break;
			case "request_user_location":
				locationUpdateRequest(room, client, command.getInt("targetroomid"), command.getInt("targetuserid"));
				break;
			case "request_all_user_locations":
				syncAllLocations(client, room);
				break;
			default:
				logger.log(Level.WARNING, "Unknown command: " + cmd );
			}
		}catch(JSONException e){
			logger.log(Level.SEVERE, "failed to parse command " + cmd);
		}
	};

	public Building() {
		rooms = new ConcurrentHashMap<Integer, IRoom>();
		changeTracker = new ChangeTracker();
		//TODO this should not be static move to config;
		this.addRoom("Lobby", true);
	}
	/**
	 * Creates a new room
	 * @param name New rooms name
	 * @param permanent If true room will not be removed when empty
	 * @return
	 */
	public int addRoom(String name, boolean permanent){
		IRoom newRoom = new AudioRoom(name, manager, permanent);
		rooms.put(newRoom.getId(), newRoom);
		JSONObject json = new JSONObject();
		try{
			json.put("command", "createdroom");
			json.put("roomid", newRoom.getId());
			json.put("roomname", newRoom.getName());
		}catch(JSONException e){
			logger.log(Level.WARNING, "Could not create JSON object (for some random reason)", e);
		}
		postUpdate(json);
		logger.log(Level.INFO, "Added a room "+ newRoom.getId() +", "+ newRoom.getName());
		return newRoom.getId();
	}
	private void removeRoom(int roomid){
		IRoom room = rooms.remove(roomid);
		if(room != null){
			room.terminate();
			JSONObject json = new JSONObject();
			try{
				json.put("command", "removedroom");
				json.put("roomid", roomid);
			}catch(JSONException e){
				logger.log(Level.WARNING, "Could not create JSON object (for some random reason)", e);
			}
			postUpdate(json);
			logger.log(Level.INFO, "Removed room " + roomid);
		}
	}
	/**
	 * Adds a new client to the building
	 * @param client
	 * @param roomid Room to put client in
	 */
	public void addClient(Client client, int roomid){
		//TODO error handling if invalid roomid

		sync(client);
		rooms.get(roomid).addClient(client);
		JSONObject json = new JSONObject();
		try {
			json.put("command", "addeduser");
			json.put("userid", client.getClientID());
			json.put("username", client.getName());
			json.put("roomid", roomid);
		} catch (JSONException e) {
			logger.log(Level.WARNING, "Could not create JSON object (for some random reason)", e);
		}


		postUpdate(json);
		logger.log(Level.INFO, "Added client " + client.toString() + " to room " + roomid);

	}

	public void addReconnectedClient(Client client) {
		//TODO Not implemented yet.
	}
	
	/**
	 * Move a Client between rooms
	 * @param clientid Client to move
	 * @param sourceRoom Current room of Client
	 * @param destinationRoom Destination room for Client
	 */
	public void moveClient(int clientid, int sourceRoom, int destinationRoom ){
		IRoom r = rooms.get(sourceRoom);
		if(r != null){
			Client c = r.removeClient(clientid);
			if(c != null){
				rooms.get(destinationRoom).addClient(c);
				JSONObject json = new JSONObject();
				try {
					json.put("command", "moveduser");
					json.put("userid", clientid);
					json.put("currentroom", sourceRoom);
					json.put("destinationroom", destinationRoom);
				} catch (JSONException e) {
					logger.log(Level.WARNING, "Could not create JSON object (for some random reason)", e);
				}
				postUpdate(json);
				logger.log(Level.INFO, "Moved client " + c.toString() + " to room " + destinationRoom);
				if(r.canDelete()){
					removeRoom(sourceRoom);
				}
			}
		}
	}

	public void removeClient(Client client, IRoom room){
		Client c = room.removeClient(client.getClientID());
		if(c != null){
			c.terminate();
			JSONObject json = new JSONObject();
			try{
				json.put("command", "removeduser");
				json.put("userid", client.getClientID());
				json.put("roomid", room.getId());
			}catch(JSONException e){
				logger.log(Level.WARNING, "Could not create JSON object (for some random reason)", e);
			}
			postUpdate(json);
			logger.log(Level.INFO, "Removed client id: " + client.getClientID() + " Alias: " + c.getName());

			if(room.canDelete()){
				IRoom removedRoom = rooms.remove(room.getId());
				JSONObject roomJson = new JSONObject();
				try{
					roomJson.put("command", "removedroom");
					roomJson.put("roomid", room.getId());
				}catch(JSONException e){
					logger.log(Level.WARNING, "Could not create JSON object (for some random reason)", e);
				}
				postUpdate(roomJson);
				logger.log(Level.INFO, "Removed room id: " + removedRoom.getId() + " name: " + removedRoom.getName());
			}
		}
	}
	
	/**
	 * Gets the client with the given clientID in the room with the given roomID, returns null if not found.
	 * @param roomOD The ID of the room to look for the client.
	 * @param clientID The ID of the client.
	 * @return The client if found, null if not.
	 */
	private Client getClient(int roomID, int clientID) {
		Client client = null;
		IRoom room = rooms.get(roomID);
		if (room != null) {
			client = room.findClient(clientID);
		}
		return client;
	}
	
	/**
	 * Finds and returns the client with the given ID, returns null if not found.
	 * @param id The ID of the client to be found.
	 * @return The client if found, null if not.
	 */
	protected Client findClient(int id) {
		Client tmp = null;
		for(IRoom room : rooms.values()) {
			tmp = room.findClient(id);
			if (tmp!=null) {
				return tmp;
			}
		}
		return null;
	}
	
	/**
	 * Updates the requesting client with location of the target client.
	 * @param reqRoom The room of the client requesting the update.
	 * @param reqClient The client requesting the update.
	 * @param roomID The room ID of the room with the target client.
	 * @param clientID The ID of the target client with the requested position.
	 */
	private void locationUpdateRequest(IRoom reqRoom, Client reqClient, int roomID, int clientID) {
		Client client = getClient(roomID, clientID);
		if (client != null) {
			JSONObject json = new JSONObject();
			try {
				json.put("command", "user_location_update");
				json.put("userid", clientID);
				json.put("roomid", roomID);
				json.put("latitude", client.getLocation().getLatitude());
				json.put("longitude", client.getLocation().getLongitude());
			} catch (JSONException e) {
				logger.log(Level.SEVERE, "Error while parsing new location command", e);
			}
			reqRoom.sendCommandToClient(clientID, json.toString());
		}
	}
	
	/**
	 * Sends a command to all Clients in the Building
	 * @param c Command to send
	 */
	public void postUpdate(JSONObject c){
		JSONObject command = changeTracker.addEntry(c);
		//TODO This should be done in a separate thread to improve responsiveness
		rooms.forEach((id,room)-> room.postUpdate(command.toString()));
	}

	private void sync(Client c){
		//TODO improve this
		logger.log(Level.FINE, "Syncing client " + c.getName());
		rooms.forEach((id,room)-> {
			JSONObject json = new JSONObject();
			try {
				json.put("command", "createdroom");
				json.put("roomid", id);
				json.put("roomname", room.getName());
				c.sendCommand(json.toString());
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not create JSON object (for some random reason)", e);
			}
			room.sync(c);
		});
	}

	/**
	 * Sends the location off all Clients to the Client that requested it.
	 * @param client The Client who sent the request.
	 * @param room The room of the requesting Client.
	 */
	private void syncAllLocations(Client client, IRoom room) {
		JSONObject json = new JSONObject();
		JSONArray jsonarr = new JSONArray();
		rooms.forEach((id,r)-> {
			r.getLocations(jsonarr);
		});
		try {
			json.put("command", "all_locations_update");
			json.put("locations", jsonarr);
			room.sendCommandToClient(client.getClientID(), json.toString());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while gathering locations", e);
		}
	}
}
