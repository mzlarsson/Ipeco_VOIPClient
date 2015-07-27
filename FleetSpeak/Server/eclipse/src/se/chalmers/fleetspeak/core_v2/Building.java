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

	public int addRoom(String name, boolean permanent){
		Room newRoom = new Room(name, manager, permanent);
		rooms.put(newRoom.getId(), newRoom);
		postUpdate(new Command("createdroom", newRoom.getId(), newRoom.getName()));
		logger.log(Level.INFO, "Added a room "+ newRoom.getId() +", "+ newRoom.getName());
		return newRoom.getId();
	}
	private void removeRoom(int id){
		rooms.remove(id);
		postUpdate(new Command("removedroom", id, null));
		logger.log(Level.INFO, "Removed room " + id);
	}

	public void addClient(Client c, int roomid){
		sync(c);
		rooms.get(roomid).addClient(c);
		postUpdate(new Command("addeduser", c.getInfoPacket(), roomid));
		logger.log(Level.INFO, "Added client " + c.getClientID() + " to room " + roomid);

	}

	public void moveClient(int clientid, int sourceRoom, int destinationRoom ){
		Client c = rooms.get(sourceRoom).removeClient(clientid);
		rooms.get(destinationRoom).addClient(c);
		postUpdate(new Command("moveduser", clientid, destinationRoom));
		logger.log(Level.INFO, "Moved client " + clientid + " to room " + destinationRoom);
	}

	public void postUpdate(Command c){
		rooms.forEach((id,room)-> room.postUpdate(c));
	}

	private void sync(Client c){
		rooms.forEach((id,room)-> {
			c.sendCommand(new Command("createdroom", id, room.getName()));
			room.sync(c);
		});
	}

}
