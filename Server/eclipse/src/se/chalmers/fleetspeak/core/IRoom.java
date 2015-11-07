package se.chalmers.fleetspeak.core;

import org.json.JSONArray;


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
	/**
	 * Finds the client with the given ID and returns it, returns null if not found.
	 * @param id The ID of the client to be found.
	 * @return The client if found, null if not.
	 */
	Client findClient(int id);
	/**
	 * Puts the locations of all the clients in the room into the JSONArray as a JSONObject:
	 * {"roomid":<roomid>,"userid":<userid>,"latitude":<latitude>,"longitude":<longitude>}
	 * @param jsonarr The JSONArray to put the locations in.
	 */
	void getLocations(JSONArray jsonarr);

}
