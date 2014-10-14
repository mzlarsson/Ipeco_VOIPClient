package se.chalmers.fleetspeak;

import java.util.List;

public interface RoomInterface{
			
	
	public List<Client> getUsers();
	
	public Client getUser(int i);
	
	public int getNbrOfUsers();
	
	public void addUser(Client c);
	
	public void removeUser(Client c);
	
	public int getRoomID();
}