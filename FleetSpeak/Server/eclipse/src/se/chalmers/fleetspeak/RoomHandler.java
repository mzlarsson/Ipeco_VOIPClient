package se.chalmers.fleetspeak;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
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

	public RoomInterface findRoom(int roomID) throws NoSuchElementException {
		for(RoomInterface room:rooms.keySet() ){
			if(room.getRoomID()==roomID){
				return room;
			}
		}
		throw new NoSuchElementException("A user with ID: \"" + roomID + "\" doesn't exit.");
	}
	
	private int generateRoomID(){
		int t = 0;
		
		if(!(this.getRooms()== null)){
		for(int i: this.getRoomIDs()){
			if(t == i){
				t++;
			}	
		}
		}
		return t;
	}
	
	public int[] getRoomIDs(){
		int[] t;
		
		if(this.getRooms() == null){
		t = new int[this.getNbrOfRooms()];
		int i = 0;
		for (RoomInterface ri : this.getRooms()) {
			t[i] = ri.getRoomID();
			i++;
		}
		}else{
		t = new int[0];
		}
		return t;
	}
	
	public int getNbrOfClients(RoomInterface ri){
		return rooms.get(ri).size();
	}
	
	public int getNbrOfRooms(){
		return this.getRooms().length;
	}
	
	
}
