package se.chalmers.fleetspeak.core;

import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.database.UserInfo;
import se.chalmers.fleetspeak.network.tcp.TCPHandler;
import se.chalmers.fleetspeak.network.udp.RTPHandler;
import se.chalmers.fleetspeak.network.udp.STUNInitiator;
import se.chalmers.fleetspeak.util.Command;

public class AndroidClientCreator implements CommandHandler {

	private Building building;
	
	private Logger logger;

	//TODO Add to config file
	private int targetRoom = 1;
	
	public AndroidClientCreator(Building building) {
		this.building = building;
		logger = Logger.getLogger("Debug");
	}
	
	public void newAndroidClient(UserInfo ui, TCPHandler tcp) {
		Client client = new Client(ui.getID(), ui.getAlias(), tcp.getInetAddress(), tcp);
		establishUDPConnection(client);
	}
	
	private void establishUDPConnection(Client client) {
		STUNInitiator stun = new STUNInitiator(client, client.getClientID());
		stun.addCommandHandler(this);
		stun.start();
	}

	private void finalizeClient(Client client, DatagramSocket socket) {
		client.setRTPHandler(new RTPHandler(socket));
		client.sendCommand(new Command("authenticationResult", true, "Successful authentication"));
		logger.log(Level.INFO, "A new person joined id: " + client.getClientID() + " Alias: " + client.getName());
		building.addClient(client, targetRoom);		
	}
	
	@Override
	public void handleCommand(Command c) {
		if (c.getCommand().toLowerCase().equals("datagramsocketstun")) {
			finalizeClient((Client)c.getKey(), (DatagramSocket)c.getValue());
		}
	}
}
