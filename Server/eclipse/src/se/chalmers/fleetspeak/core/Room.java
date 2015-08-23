package se.chalmers.fleetspeak.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.IDFactory;


public class Room implements CommandHandler{
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
	public Client removeClient(int clientid){
		logger.log(Level.FINER, "Room:" + id + " Removing user with id: " + clientid);
		Client c = clients.get(clientid);
		clients.remove(clientid);
		return c;
	}

	public boolean canDelete(){
		return clients.isEmpty() && !permanent;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Sends a command to every Client in the room
	 * @param c Command to send
	 */
	public void postUpdate(Command c){
		clients.forEach((id, client) -> client.sendCommand(c));
	}

	public void sync(Client c){
		logger.log(Level.FINE, name + " " + clients.size());
		clients.forEach((id,client)->{
			c.sendCommand(new Command("addeduser", client.getInfoPacket(), this.id));
		});
	}

	@Override
	public void handleCommand(Command c) {
		buildingManager.handleCommand(c, id);
	}
}
