package se.chalmers.fleetspeak;

import java.util.ArrayList;
import java.util.List;

public class FleetRoom implements RoomInterface {
	
	private List<Client> clients;
	
	private String id;

	public FleetRoom(String s){
		clients = new ArrayList<Client>();
		id = s;
	}
	
	
	@Override
	public void addUser(Client c) {
		try{
			clients.add(c);
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void removeUser(Client c) {
		try{
		clients.remove(c);	
		}catch(ClassCastException e){
		System.out.println(e.getMessage());	
		}catch(NullPointerException e){
		System.out.println(e.getMessage());
		}
	}
	
	@Override
	public List<Client> getUsers() {
		if(clients==null || clients.size()<=0){
			System.out.println("There are no clients in this room");
			return clients;
		}else{
			return clients;
		}
	}

	@Override
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
	
	@Override
	public int getNbrOfUsers() {
		return clients.size();	
	}

	@Override
	public String getRoomID() {
		return id;
	}
	
}
