package se.chalmers.fleetspeak.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class Building {

	private Logger logger = Logger.getLogger("Debug");

	private ConcurrentHashMap<Integer, IRoom> rooms;

	private BuildingManager manager = (cmd, id) -> {
		try{
			JSONObject command = new JSONObject(cmd);
			switch(command.getString("command")){
			case "moveclient":
				moveClient(command.getInt("userid"), command.getInt("currentroom"), command.getInt("destinationroom"));
				break;
			case "movenewroom":
				moveClient(command.getInt("userid"), command.getInt("currentroom"), addRoom(command.getString("roomname"),false));
				break;
			case "createroom":
				addRoom(command.getString("roomname"), false);
				break;
			case "disconnect":
				removeClient(command.getInt("userid"),id);
				break;
			default:
				logger.log(Level.WARNING, "Unknown command: " + cmd );
			}
		}catch(JSONException e){
			logger.log(Level.WARNING, "failed to parse command " + cmd);
		}
	};

	public Building() {
		super();
		rooms = new ConcurrentHashMap<Integer, IRoom>();

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
		postUpdate("{\"command\":\"createdroom\","
				+ "\"roomid\":" + newRoom.getId() + ","
				+ "\"roomname\":\"" + newRoom.getName() + "\"}");
		logger.log(Level.INFO, "Added a room "+ newRoom.getId() +", "+ newRoom.getName());
		return newRoom.getId();
	}
	private void removeRoom(int roomid){
		IRoom room = rooms.remove(roomid);
		if(room != null){
			room.terminate();
			postUpdate("{\"command\":\"removedroom\","
					+ "\"roomid\":" + roomid + "}");
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

		rooms.get(roomid).addClient(client);
		JSONObject json = new JSONObject();
		try {
			json.put("command", "addeduser");
			json.put("userid", client.getClientID());
			json.put("username", client.getName());
			json.put("roomid", roomid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		postUpdate(json.toString());
		logger.log(Level.INFO, "Added client " + client.toString() + " to room " + roomid);
		sync(client);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				postUpdate(json.toString());
				logger.log(Level.INFO, "Moved client " + c.toString() + " to room " + destinationRoom);
				if(r.canDelete()){
					removeRoom(sourceRoom);
				}
			}
		}
	}

	public void removeClient(int clientid, int roomid){
		Client c = rooms.get(roomid).removeClient(clientid);
		if(c != null){
			c.terminate();
			JSONObject json = new JSONObject();
			try{
				json.put("command", "removeduser");
				json.put("userid", clientid);
				json.put("roomid", roomid);
			}catch(JSONException e){
				e.printStackTrace();
			}
			postUpdate(json.toString());
			logger.log(Level.INFO, "Removed client id: " + clientid + " Alias: " + c.getName());
		}
	}

	/**
	 * Sends a command to all Clients in the Building
	 * @param c Command to send
	 */
	public void postUpdate(String c){
		//TODO This should be done in a separate thread to improve responsiveness
		rooms.forEach((id,room)-> room.postUpdate(c));
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			c.sendCommand(json.toString());
			room.sync(c);
		});
	}

}
