package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.util.Command;

public interface IRoom {

	void addClient(Client client);
	Client removeClient(int clientid);
	boolean canDelete();
	Integer getId();
	String getName();
	void setName(String name);
	public void postUpdate(Command c);
	public void sync(Client c);

}
