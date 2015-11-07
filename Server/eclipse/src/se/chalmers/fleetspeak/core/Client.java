package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.fleetspeak.network.tcp.TCPHandler;
import se.chalmers.fleetspeak.network.udp.RTPHandler;
import se.chalmers.fleetspeak.sound.BufferedAudioStream;
import se.chalmers.fleetspeak.util.Location;

/**
 * A class that handles all connectors with the app
 * @author Nieo
 * @author Patrik Haar
 */


public class Client implements CommandHandler, NetworkUser {

	private String alias;
	private TCPHandler tcp;
	private RTPHandler rtp;
	private int clientID;

	private Location location;	
	private InetAddress ip;	//TODO Is it necessary for the client to hold it IP?

	private Logger logger;

	private CommandHandler ch;

	/**
	 * Creates a client with the functionality for sending and receiving
	 * commands and sound-streams.
	 * @param socket The socket for the tcp-connection to this client.
	 */
	public Client(int id, String alias, InetAddress ip, TCPHandler tcph) {
		logger = Logger.getLogger("Debug");
		this.clientID = id;
		this.alias = alias;
		this.ip = ip;
		this.tcp = tcph;
		this.tcp.setCommandHandler(this);
		location = new Location(0,0);
		JSONObject json = new JSONObject();
		try {
			json.put("command" , "setinfo");
			json.put("userid", clientID);
			sendCommand(json.toString());
		} catch (JSONException e) {
			logger.log(Level.WARNING, "Could not create JSON object (for some random reason)", e);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Caught IOException when sending setinfo, userid. Message: "
					+ e.getMessage());
		}
	}

	@Override
	public void sendCommand(String command) throws IOException{
		tcp.sendCommand(command);
	}

	@Override
	public void setCommandHandler(CommandHandler ch){
		this.ch = ch;
	}

	/**
	 * Gets the clients ID.
	 * @return The ID of the client.
	 */
	public int getClientID() {
		return clientID;
	}

	/**
	 * Gets the name of this client.
	 * @return The name of the client.
	 */
	public String getName() {
		return alias;
	}
	
	/**
	 * Gets the location of this client.
	 * @return The location of the client.
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Set the name of this client.
	 * @param name The new name of the client.
	 */
	protected void setName(String name) {
		if(name != null){
			this.alias = name;
		}
	}

	/**
	 * Remove this client and all services associated with it.
	 */
	public void terminate() {
		if (tcp != null) {
			tcp.terminate();
		}
		if(rtp != null){
			rtp.terminate();
		}
	}

	public void setTCPHandler(TCPHandler tcp) {
		if (this.tcp != null) {
			this.tcp.terminate();
		}
		this.tcp = tcp;
	}
	
	public void setRTPHandler(RTPHandler rtp) {
		if (this.rtp != null) {
			this.rtp.terminate();
		}
		this.rtp = rtp;
	}

	public BufferedAudioStream getAudioStream(){
		return rtp.getBufferedAudioStream();
	}

	public BlockingQueue<byte[]> getOutputBuffer(){
		return rtp.getOutputBuffer();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Client: name=" + alias + ", clientID=" + clientID + ", ip=" + ip;
	}


	@Override
	public void handleCommand(String string) {
		try {
			JSONObject json = new JSONObject(string);
			switch(json.getString("command")){
			case "disconnect":
				json.put("userid", this.clientID);
				ch.handleCommand(json.toString());
				break;
			case "updatelocation":
				location = new Location(json.getDouble("latitude"), json.getDouble("longitude"));
				ch.handleCommand(string);
				break;
			default:
				ch.handleCommand(string);
				break;
			}

		} catch (JSONException e) {
			logger.log(Level.WARNING, "Could not create JSON object (for some random reason)", e);
		}
	}
}
