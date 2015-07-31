package se.chalmers.fleetspeak.core_v2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.util.Command;

public class Building {

	private Logger logger = Logger.getLogger("Debug");

	private ConcurrentHashMap<Integer, Room> rooms;

	private BuildingManager manager = (cmd, id) -> {

		switch(cmd.getCommand()){
		case "moveclient":
			moveClient((int)cmd.getKey(), id, (int)cmd.getValue());
			break;
		case "movenewroom":
			moveClient((int)cmd.getKey(), id, addRoom((String)cmd.getValue(), false));
			break;
		case "createroom":
			addRoom((String)cmd.getValue(), false);
			break;
		case "roomempty":
			removeRoom(id);
			break;
		default:
			logger.log(Level.WARNING, "Unknown command: " + cmd.getCommand() );
		}
	};

	public Building() {
		super();
		rooms = new ConcurrentHashMap<Integer, Room>();

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
		Room newRoom = new Room(name, manager, permanent);
		rooms.put(newRoom.getId(), newRoom);
		postUpdate(new Command("createdroom", newRoom.getId(), newRoom.getName()));
		logger.log(Level.INFO, "Added a room "+ newRoom.getId() +", "+ newRoom.getName());
		return newRoom.getId();
	}
	private void removeRoom(int roomid){
		rooms.remove(roomid);
		postUpdate(new Command("removedroom", roomid, null));
		logger.log(Level.INFO, "Removed room " + roomid);
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
		postUpdate(new Command("addeduser", client.getInfoPacket(), roomid));
		logger.log(Level.INFO, "Added client " + client.getClientID() + " to room " + roomid);

	}
	/**
	 * Move a Client between rooms
	 * @param clientid Client to move
	 * @param sourceRoom Current room of Client
	 * @param destinationRoom Destination room for Client
	 */
	public void moveClient(int clientid, int sourceRoom, int destinationRoom ){
		Client c = rooms.get(sourceRoom).removeClient(clientid);
		rooms.get(destinationRoom).addClient(c);
		postUpdate(new Command("moveduser", clientid, destinationRoom));
		logger.log(Level.INFO, "Moved client " + clientid + " to room " + destinationRoom);
	}
	/**
	 * Sends a command to all Clients in the Building
	 * @param c Command to send
	 */
	public void postUpdate(Command c){
		//TODO This should be done in a separate thread to improve responsiveness
		rooms.forEach((id,room)-> room.postUpdate(c));
	}

	private void sync(Client c){
		rooms.forEach((id,room)-> {
			c.sendCommand(new Command("createdroom", id, room.getName()));
			room.sync(c);
		});
	}

}
