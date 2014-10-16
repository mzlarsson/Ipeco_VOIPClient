package se.chalmers.fleetspeak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
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
		rooms = new HashMap<Room,ArrayList<Client>>();
		defaultRoom = new Room("Default");
	}
	
	public void addClient(Client c, Room r){
		if (!rooms.containsKey(r) || rooms.get(r) == null) {
            ArrayList<Client> list = new ArrayList<Client>();
            list.add(c);
            rooms.put(r,list);
		}else{
			 ArrayList<Client> list = rooms.get(r);
			 if(!list.contains(c)){
				 list.add(c); 
			 }
		}   
	}
	
	public void addClient(Client c, int roomID){
		this.addClient(c, findRoom(roomID)); 
	}
	
	public void addClient(Client client) {
		addClient(client, defaultRoom);
	}

	public void removeClient(Client c, boolean terminate){
		for(Room r : rooms.keySet()){
			ArrayList<Client> clientList = rooms.get(r);
			if(clientList.contains(c)){
				clientList.remove(c);
				if (terminate) {
					c.terminate();
				}
				if(clientList.isEmpty()){
					rooms.remove(r);
				}
				break;
			}
		}
	}
	
	public void removeClient(int clientID, boolean terminate){
		this.removeClient(getClient(clientID), terminate);
	}

	public void removeClient(int clientID){
		this.removeClient(getClient(clientID), false);
	}
	
	public void moveClient(Client c, Room r){
		this.removeClient(c, false);
		this.addClient(c, r);
	}
	
	public void moveClient(int clientID, Room room){
		this.moveClient(this.getClient(clientID), room);
	}
	
	public void moveClient(int clientID, int roomID){
		this.moveClient(this.getClient(clientID), this.findRoom(roomID));
	}
	
	public Room[] getRooms(){
		return rooms.keySet().toArray(new Room[rooms.keySet().size()]);
	}
	
	public Client[] getClients(Room r){
		return rooms.get(r).toArray(new Client[rooms.get(r).size()]);
	}
	
 	public Client getClient(int clientID) throws NoSuchElementException {
		for(ArrayList<Client> clients:rooms.values()){
			for(Client c: clients){
				if(c.getClientID()==clientID){
					return c;
				}
			}
		}
		throw new NoSuchElementException("A client with ID: \"" + clientID + "\" doesn't exit.");
	}

	private Room findRoom(int roomID) throws NoSuchElementException {
		for(Room room:rooms.keySet() ){
			if(room.getId()==roomID){
				return room;
			}
		}
		throw new NoSuchElementException("A user with ID: \"" + roomID + "\" doesn't exit.");
	}

	public void terminate() {
		for(ArrayList<Client> clients:rooms.values()){
			for(Client c: clients){
				c.terminate();
			}
		}
	}
	
	public int getNbrOfClients(Room r){
		return rooms.get(r).size();
	}
	
	public int getNbrOfRooms(){
		return this.getRooms().length;
	}

	public String getRoomInfo() {
		String info = rooms.keySet().isEmpty()?"No clients connected.":"";
		for (Room room : getRooms()) {
			info += room.toString() + "\n";
			for (Client client : getClients(room)) {
				info += "\t" + client.toString() + "\n";
			}
		}
		return info;
	}
	
}
