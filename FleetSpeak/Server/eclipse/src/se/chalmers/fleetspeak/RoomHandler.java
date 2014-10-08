package se.chalmers.fleetspeak;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeMap;
/**
 * An class for handling the rooms and client on the server side.
 * Based on the RoomHandler on the client side.
 * @author David Michaëlsson
 *
 */
public class RoomHandler {
	
    private TreeMap<RoomInterface,ArrayList<Client>> rooms;
	
	public RoomHandler(){
		rooms = new TreeMap<RoomInterface,ArrayList<Client>>();
	}
	
	public void addClient(Client c, RoomInterface r){
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
		this.addClient(c,this.findRoom(roomID));
	}
	
	public void removeClient(Client c){
		for(RoomInterface r : rooms.keySet()){
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
	
	public void moveClient(Client c, RoomInterface r){
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
	
 	private Client findClient(int clientID) throws NoSuchElementException {
		for(ArrayList<Client> clients:rooms.values()){
			for(Client c: clients){
				if(c.getClientID()==clientID){
					return c;
				}
			}
		}
		throw new NoSuchElementException("A client with ID: \"" + clientID + "\" doesn't exit.");
	}

	private RoomInterface findRoom(int roomID) throws NoSuchElementException {
		for(RoomInterface room:rooms.keySet() ){
			if(room.getRoomID()==roomID){
				return room;
			}
		}
		throw new NoSuchElementException("A user with ID: \"" + roomID + "\" doesn't exit.");
	}

	
	
	
}
