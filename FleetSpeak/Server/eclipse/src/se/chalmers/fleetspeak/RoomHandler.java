package se.chalmers.fleetspeak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.TreeMap;
/**
 * An class for handling the rooms and client on the server side.
 * Based on the RoomHandler on the client side.
 * @author David Micha�lsson
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

	public void removeClient(Client c){
		for(Room r : rooms.keySet()){
			ArrayList<Client> clientList = rooms.get(r);
			if(clientList.contains(c)){
				clientList.remove(c);
				if(clientList.isEmpty()){
					rooms.remove(r);
				}
				break;
			}
		}
	}
	
	public void removeClient(int clientID){
		this.removeClient(findClient(clientID));
	}
	
	public void moveClient(Client c, Room r){
		this.removeClient(c);
		this.addClient(c, r);
	}
	
	public void moveClient(int clientID, int roomID){
		this.moveClient(this.findClient(clientID), this.findRoom(roomID));
	}
	
	public RoomInterface[] getRooms(){
		return rooms.keySet().toArray(new RoomInterface[rooms.keySet().size()]);
	}
	
	public Client[] getClients(RoomInterface r){
		return rooms.get(r).toArray(new Client[rooms.get(r).size()]);
	}
	
 	public Client findClient(int clientID) throws NoSuchElementException {
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
	
	public int getNbrOfClients(RoomInterface ri){
		return rooms.get(ri).size();
	}
	
	public int getNbrOfRooms(){
		return this.getRooms().length;
	}
	
	
}
