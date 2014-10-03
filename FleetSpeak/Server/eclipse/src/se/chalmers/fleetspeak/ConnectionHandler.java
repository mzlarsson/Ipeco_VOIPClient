package se.chalmers.fleetspeak;

import java.util.List;

public interface ConnectionHandler {
	
	public abstract void onClientConnect(List<Client> clients);
	public abstract void onClientDisconnect(List<Client> clients);
	
}
