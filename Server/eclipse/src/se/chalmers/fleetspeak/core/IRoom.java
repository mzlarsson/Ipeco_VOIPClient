package se.chalmers.fleetspeak.core;


public interface IRoom {

	void addClient(Client client);
	Client removeClient(int clientid);
	boolean canDelete();
	Integer getId();
	String getName();
	void sendCommandToClient(int clientid, String command);
	void setName(String name);
	void sync(Client c);
	void terminate();
	void postUpdate(String c);
	Client findClient(int id);

}
