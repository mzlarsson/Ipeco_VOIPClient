package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.fleetspeak.database.UserInfo;
import se.chalmers.fleetspeak.network.tcp.TCPHandler;
import se.chalmers.fleetspeak.network.udp.RTPHandler;
import se.chalmers.fleetspeak.network.udp.STUNInitiator;
import se.chalmers.fleetspeak.network.udp.STUNListener;

public class AndroidClientCreator implements STUNListener {

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
		stun.addSTUNListener(this);
		stun.start();
	}

	private void finalizeClient(Client client, DatagramSocket socket) {
		client.setRTPHandler(new RTPHandler(socket));
		JSONObject json = new JSONObject();
		try {
			json.put("command", "authenticationresult");
			json.put("result", true);
			client.sendCommand(json.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Caught IOException when sending authenticationresult, true. Message: "
					+ e.getMessage() + " terminating client");
			client.terminate();
		}
		logger.log(Level.INFO, "A new person joined id: " + client.getClientID() + " Alias: " + client.getName());
		building.addClient(client, targetRoom);		
	}

	@Override
	public void stunSuccessful(NetworkUser nu, DatagramSocket udp) {
		finalizeClient((Client)nu, udp);
	}

	@Override
	public void stunFailed(NetworkUser nu, String error) {
		// TODO Let the android client know that the STUN initiation failed.
	}
}
