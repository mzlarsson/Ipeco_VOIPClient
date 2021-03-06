package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.fleetspeak.util.IDFactory;


public class Room implements CommandHandler, IRoom{
	private Logger logger = Logger.getLogger("Debug");

	private ConcurrentHashMap<Integer, Client> clients;

	private int id;
	private String name;
	private boolean permanent;

	private BuildingManager buildingManager;


	public Room(String name, BuildingManager buildingManager, boolean permanent) {
		super();
		this.name = name;
		this.buildingManager = buildingManager;
		clients = new ConcurrentHashMap<Integer, Client>();
		id = IDFactory.getInstance().getID();
		this.permanent = permanent;
	}

	@Override
	public void addClient(Client client){
		clients.put(client.getClientID(), client);
		client.setCommandHandler(this);
		logger.log(Level.FINER, "Room:" + id + " Added user with id: " + client.getClientID());
	}
	/**
	 * Removes a Client from the Room and returns that client
	 * @param clientid Client to remove
	 * @return Client with clientid
	 */
	@Override
	public Client removeClient(int clientid){
		logger.log(Level.FINER, "Room:" + id + " Removing user with id: " + clientid);
		return clients.remove(clientid);
	}

	@Override
	public boolean canDelete(){
		return clients.isEmpty() && !permanent;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Sends a command to every Client in the room
	 * @param c Command to send
	 */
	@Override
	public void postUpdate(String c){
		for (Client client : clients.values()) {
			try {
				client.sendCommand(c);
			} catch (IOException e) {
				JSONObject json = new JSONObject();
				try {
					json.put("command", "disconnect");
					json.put("userid", client.getClientID());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				handleCommand(json.toString());
			}
		}
	}

	@Override
	public void sync(Client c){
		logger.log(Level.FINE, name + " " + clients.size());
		clients.forEach((id,client)->{
			JSONObject json = new JSONObject();
			try {
				json.put("command", "addeduser");
				json.put("userid", client.getClientID());
				json.put("username", client.getName());
				json.put("roomid", this.id);
				c.sendCommand(json.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@Override
	public void handleCommand(String c) {
		buildingManager.handleCommand(c, id);
	}

	@Override
	public void terminate(){
		clients.forEach((id, client) -> client.terminate());
	}


}
