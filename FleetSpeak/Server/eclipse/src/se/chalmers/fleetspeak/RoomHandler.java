package se.chalmers.fleetspeak;

import java.util.ArrayList;
import java.util.HashMap;

import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log;
/**
 * An class for handling the rooms and client on the server side.
 * Based on the RoomHandler on the client side.
 * @author David Michaelsson
 *
 */
public class RoomHandler {
	
    private HashMap<Room,ArrayList<Client>> rooms;
	private Room defaultRoom;
	
	public RoomHandler(){
		Log.logDebug("Creating a new RoomHandler");
		rooms = new HashMap<Room,ArrayList<Client>>();
		defaultRoom = new Room("Lobby", 0);
	}
	
	public void addClient(Client c, Room r){
		if(c != null && r != null){
			if (!rooms.containsKey(r) || rooms.get(r) == null) {
	            ArrayList<Client> list = new ArrayList<Client>();
	            list.add(c);
	            c.moveToRoom(r.getId());
	            rooms.put(r,list);
			}else{
				 ArrayList<Client> list = rooms.get(r);
				 if(!list.contains(c)){
					 list.add(c);
					 c.moveToRoom(r.getId());
				 }
			}
			
			changeEvent();
		}
	}
	
	public void addClient(Client c, int roomID){
		this.addClient(c, findRoom(roomID)); 
	}
	
	public void addClient(Client client) {
		addClient(client, defaultRoom);
	}

	public void removeClient(Client c, boolean terminate){
		if(c != null){
			for(Room r : rooms.keySet()){
				ArrayList<Client> clientList = rooms.get(r);
				if(clientList.contains(c)){
					clientList.remove(c);
					if (terminate) {
						c.terminate();
					}
					if(clientList.isEmpty() && r.getId() != 0){
						Log.logDebug("Removing room");
						rooms.remove(r);
					}
					break;
				}
			}
			changeEvent();
		}
	}
	
	public void removeClient(int clientID, boolean terminate){
		this.removeClient(findClient(clientID), terminate);
	}

	public void removeClient(int clientID){
		this.removeClient(findClient(clientID), false);
	}
	
	public void moveClient(Client c, Room r){
		this.removeClient(c, false);
		this.addClient(c, r);
	}
	
	public void moveClient(int clientID, Room room){
		this.moveClient(this.findClient(clientID), room);
	}
	
	public void moveClient(int clientID, int roomID){
		this.moveClient(this.findClient(clientID), this.findRoom(roomID));
	}
	
	public Room[] getRooms(){
		return rooms.keySet().toArray(new Room[rooms.keySet().size()]);
	}
	
	public Client[] getClients(Room r){
		return rooms.get(r).toArray(new Client[rooms.get(r).size()]);
	}
	
	public void setUsername(int clientID, String name) {
		Client c = findClient(clientID);
		if(c != null){
			c.setName(name);
			changeEvent();
		}
	}
	
	public void setRoomName(int roomID, String name) {
		Room r = findRoom(roomID);
		if(r != null){
			r.setName(name);
			changeEvent();
		}
	}

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

	private Room findRoom(int roomID) {
		for(Room room:rooms.keySet() ){
			if(room.getId()==roomID){
				return room;
			}
		}

		Log.logError("\tDid not find room with ID </error><b>"+roomID+"</b>");
		return null;
	}

	public void terminate() {
		for(ArrayList<Client> clients:rooms.values()){
			for(Client c: clients){
				c.terminate();
			}
		}
		rooms.clear();
		changeEvent();
	}
	
	public int getNbrOfClients(Room r){
		return rooms.get(r).size();
	}
	
	public int getNbrOfRooms(){
		return this.getRooms().length;
	}

	public String getRoomInfo() {
		StringBuilder info = new StringBuilder();
		info.append(rooms.keySet().isEmpty()?"No clients connected.":"");
		for (Room room : getRooms()) {
			info.append(room.toString() + "\n");
			for (Client client : getClients(room)) {
				info.append("\t" + client.toString() + "\n");
			}
		}
		return info.toString();
	}
	
	public String getHTMLRoomInfo() {
		StringBuilder info = new StringBuilder();
		info.append("<html>" + (rooms.keySet().isEmpty()?"No clients connected.":""));
		for (Room room : getRooms()) {
			info.append(room.getName() + " (" + room.getId() + ")<br>");
			for (Client client : getClients(room)) {
				info.append("&nbsp;&nbsp;&nbsp;&nbsp;" + client.getName() + " (" + client.getClientID() + ")<br>");
			}
		}
		info.append("</html>");
		return info.toString();
	}
	
	public void changeEvent() {
		EventBus.getInstance().fireEvent(new EventBusEvent("ServerGUI", new Command("roomsChanged", null, getHTMLRoomInfo()), this));
	}
}
