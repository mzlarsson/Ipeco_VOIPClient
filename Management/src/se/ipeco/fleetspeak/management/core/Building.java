package se.ipeco.fleetspeak.management.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import se.ipeco.fleetspeak.management.connection.CommandHandler;

public class Building implements CommandHandler{
	
	private static Building building;
	
	private Logger logger = Logger.getLogger("Debug");
	
	private HashMap<Integer, Room> rooms;
	private List<BuildingChangeListener> listeners;
	
	private Building(int id){
		logger.info("Starting Building for [ID "+id+"]");
		rooms = new HashMap<Integer, Room>();
		listeners = new ArrayList<BuildingChangeListener>();
	}

	@Override
	public void commandReceived(String json){
		logger.info("Building got command: "+json);
		if(json == null){
			logger.warning("Got empty command, ignored.");
			return;
		}
		
		try {
			JSONObject obj = new JSONObject(json);
			switch(obj.getString("command").toLowerCase()){
				case "addeduser":		addUser(obj.getInt("userid"), obj.getString("username"), obj.getInt("roomid"));break;
				case "removeduser":		removeUser(obj.getInt("userid"), obj.getInt("roomid"));break;
				case "createdroom":		addRoom(obj.getInt("roomid"), obj.getString("roomname"));break;
				case "removedroom":		removeRoom(obj.getInt("roomid"));break;
				case "changedroomname":	changeRoomName(obj.getInt("roomid"), obj.getString("roomname"));break;
				case "moveduser":		moveUser(obj.getInt("userid"), obj.getInt("currentroom"), obj.getInt("destinationroom"));break;
				case "lostconnection":	lostConnection();break;
			}
		} catch (JSONException e) {
			logger.warning("Caught json error: [JSONException] "+e.getMessage());
		}
	}
	
	public void addBuildingChangeListener(BuildingChangeListener listener){
		if(listener != null){
			listeners.add(listener);
			//Sync all old values
			for(Room room : rooms.values()){
				listener.addedRoom(room);
			}
		}
	}

	private void addRoom(int roomID, String roomName) {
		Room r = new Room(roomID, roomName);
		rooms.put(roomID, r);
		
		for(BuildingChangeListener listener : listeners){
			listener.addedRoom(r);
		}
	}

	private Room getRoom(int roomID){
		return rooms.get(roomID);
	}

	private void removeRoom(int roomID) {
		Room r = rooms.remove(roomID);
		if(r != null){
			for(BuildingChangeListener listener : listeners){
				listener.removedRoom(r);
			}
		}
	}

	private void changeRoomName(int roomID, String roomName) {
		Room room = getRoom(roomID);
		if(room != null){
			room.setName(roomName);
		}else{
			logger.warning("Tried to rename non-existant room");
		}
	}

	private void addUser(int userID, String username, int roomID) {
		User user = new User(userID, username);
		Room r = getRoom(roomID);
		if(r != null){
			r.addUser(user);
		}
	}

	private void removeUser(int userID, int currentRoomID) {
		Room r = getRoom(currentRoomID);
		logger.info("Removing user");
		if(r != null){
			r.removeUser(userID);
		}
	}
	
	private void moveUser(int userID, int currentRoomID, int destinationRoomID) {
		Room oldRoom = getRoom(currentRoomID);
		Room newRoom = getRoom(destinationRoomID);
		if(oldRoom != null && newRoom != null){
			User user = oldRoom.removeUser(userID);
			if(user != null){
				newRoom.addUser(user);
			}
		}
	}
	
	private void lostConnection(){
		for(BuildingChangeListener listener : listeners){
			listener.lostConnection();
		}
	}
	
	public static Building getInstance(int id){
		if(building == null){
			building = new Building(id);
		}
		
		return building;
	}
	
	public static boolean hasRunningBuilding(){
		return building != null;
	}
	
	public static Building getRunningBuilding(){
		return building;
	}

	public static void terminate(){
		if(building != null){
			building.listeners.clear();
			building = null;
		}
	}
	
	
	public interface BuildingChangeListener{
		public void addedRoom(Room r);
		public void removedRoom(Room r);
		public void lostConnection();
	}
}
