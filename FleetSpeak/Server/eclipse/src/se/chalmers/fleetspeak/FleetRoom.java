package se.chalmers.fleetspeak;

import java.util.ArrayList;
import java.util.List;

public class FleetRoom implements RoomInterface {
	
	private List<Client> clients;
	
	private int id;

	public FleetRoom(int i){
		clients = new ArrayList<Client>();
		id = i;
	}
	
	
	public void addUser(Client c) {
		try{
			clients.add(c);
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void removeUser(Client c) {
		try{
		clients.remove(c);	
		}catch(ClassCastException e){
		System.out.println(e.getMessage());	
		}catch(NullPointerException e){
		System.out.println(e.getMessage());
		}
	}
	
	public List<Client> getUsers() {
		if(clients==null || clients.size()<=0){
			System.out.println("There are no clients in this room");
			return clients;
		}else{
			return clients;
		}
	}

	public Client getUser(int i){
		Client c;
		try{
		c = clients.get(i);
		}catch(IndexOutOfBoundsException e){
			System.out.println(e.getMessage());
		c = clients.get(0);
		}catch(NullPointerException e){
			System.out.println(e.toString());
		}
		return clients.get(i);
	}
	
	public int getNbrOfUsers() {
		return clients.size();	
	}

	@Override
	public int getRoomID() {
		return id;
	}
	
}
