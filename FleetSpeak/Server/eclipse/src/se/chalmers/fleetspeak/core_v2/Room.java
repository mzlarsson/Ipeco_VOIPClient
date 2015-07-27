package se.chalmers.fleetspeak.core_v2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.core.CommandHandler;
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
	public Client removeClient(int clientid){
		logger.log(Level.FINER, "Room:" + id + " Removing user with id: " + clientid);
		Client c = clients.get(clientid);
		clients.remove(clientid);
		if(clients.isEmpty() && !permanent) {
			buildingManager.handleCommand(new Command("roomempty",null,null), this.id);
		}
		return c;
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

	public void postUpdate(Command c){
		clients.forEach((id, client) -> client.sendCommand(c));
	}

	public void sync(Client c){
		clients.forEach((id,client)->{
			c.sendCommand(new Command("addeduser", c.getInfoPacket(), this.id));
		});
	}

	@Override
	public void handleCommand(Command c) {
		buildingManager.handleCommand(c, id);
	}
}
