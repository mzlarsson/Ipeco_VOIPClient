package se.chalmers.fleetspeak.core;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.network.tcp.TCPHandler;
import se.chalmers.fleetspeak.network.udp.RTPHandler;
import se.chalmers.fleetspeak.sound.BufferedAudioStream;
import se.chalmers.fleetspeak.sound.Router;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.UserInfoPacket;

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

	private InetAddress ip;	//TODO Is it necessary for the client to hold it IP?
	private Router soundRouter;

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
		this.tcp.sendCommand(new Command("setInfo", getInfoPacket(), null));

		this.soundRouter = new Router();
		this.tcp.sendCommand(new Command("useSoundPort", soundRouter.getReceivePort(), null));
		soundRouter.start();


	}

	@Override
	public void sendCommand(Command c){
		tcp.sendCommand(c);
	}

	@Override
	public void setCommandHandler(CommandHandler ch){
		this.ch = ch;
	}
	/**
	 * Gets the information of the client in a bundle.
	 * @return The information of the client.
	 */
	public UserInfoPacket getInfoPacket() {
		return new UserInfoPacket(clientID, alias);
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
		if (soundRouter != null) {
			soundRouter.terminate();
		}
		if (tcp != null) {
			tcp.terminate();
		}
		if(rtp != null){
			rtp.terminate();
		}
	}

	/**
	 * Logs an error-message and terminates the client.
	 */
	public void connectionLost() {
		logger.log(Level.INFO, "Client disconnected - closing streams");
		this.terminate();
	}

	public void setRTPHandler(RTPHandler rtp) {
		this.rtp = rtp;
	}

	public BufferedAudioStream getAudioStream(){
		return rtp.getBufferedAudioStream();
	}

	public BlockingQueue<byte[]> getOutputBuffer(){
		return rtp.getOutputBuffer();
	}

	@Override
	public void handleCommand(Command c) {
		logger.log(Level.FINER,"[Client]userid: "+ clientID + "s Got command " + c.getCommand() + " key "+ c.getKey() + " value "+ c.getValue());
		switch(c.getCommand().toLowerCase()){
		case "move":
			ch.handleCommand(new Command("moveclient", clientID, c.getKey()));
			break;
		case "movenewroom":
			ch.handleCommand(new Command("movenewroom", clientID, c.getKey()));
			break;
		case "disconnect":
			tcp.terminate();
			ch.handleCommand(new Command("disconnect", clientID,null));
			break;
		default:
			ch.handleCommand(c);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Client: name=" + alias + ", clientID=" + clientID + ", ip=" + ip;
	}
}
