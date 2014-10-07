package se.chalmers.fleetspeak;

import java.io.IOException;
import java.util.List;

public interface RoomInterface{
			
	
	public List<Client> getUsers();
	
	public Client getUser(int i) throws IOException;
	
	public int getNbrOfUsers();
	
	public void addUser(Client c);
	
	public void removeUser(Client c);
	
	public String getRoomID();
}
