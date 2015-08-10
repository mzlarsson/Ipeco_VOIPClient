package se.chalmers.fleetspeak.core;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.audio.sound.Router;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.UserInfoPacket;

/**
 * A class that handles all connectors with the app
 * @author Nieo
 * @author Patrik Haar
 */


public class Client implements CommandHandler {

	private String alias;
	private TCPHandler tcp;
	private int clientID;

	private InetAddress ip;	//TODO Is it necessary for the client to hold it IP?
	private Router soundRouter;

	private Logger logger;

	private CommandHandler room;

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

	public void sendCommand(Command c){
		tcp.sendCommand(c);
	}

	public void setCommandHandler(CommandHandler ch){
		room = ch;
	}

	//FIXME temporary
	public void sendToPort(Client c, int port){
		tcp.sendCommand(new Command("sendStuffTo", c.ip.getHostAddress(), port));
	}



	/**
	 * Moves this client to another room symbolized by a list of clients
	 * and adds them as listeners to its stream.
	 * @param clientList A list of clients symbolizing all clients in a room.
	 */
	public synchronized void moveToRoom(List<Client> clientList){
		removeAllListeningClients();
		for (Client c : clientList) {
			requestListeningClient(c);
		}
	}

	/**
	 * Requests a port for this client to start connection
	 * @param client The client to listen to.
	 */
	public void requestListeningClient(Client client){
		tcp.sendCommand(new Command("requestSoundPort", client.getClientID(), null));
	}

	/**
	 * Adds a client to this client connection list
	 * @param remoteClient The remote client
	 * @param port The port that is used
	 */
	public void addListeningClient(Client remoteClient, int port){
		soundRouter.addClient(remoteClient.getClientID(), remoteClient.ip, port);
	}

	/**
	 * Removes a client that listens to this client.
	 * @param client The client to be removed.
	 */
	public void removeListeningClient(Client client) {
		soundRouter.removeClient(client.clientID);
	}

	/**
	 * Removes all clients that listens to this client.
	 */
	public void removeAllListeningClients() {
		soundRouter.removeAllClients();
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
	}

	/**
	 * Logs an error-message and terminates the client.
	 */
	public void connectionLost() {
		logger.log(Level.INFO, "Client disconnected - closing streams");
		this.terminate();
	}

	@Override
	public void handleCommand(Command c) {
		logger.log(Level.FINER,"[Client]userid: "+ clientID + "s Got command " + c.getCommand() + " key "+ c.getKey() + " value "+ c.getValue());
		switch(c.getCommand().toLowerCase()){
		case "move":
			room.handleCommand(new Command("moveclient", clientID, c.getKey()));
			break;
		case "movenewroom":
			room.handleCommand(new Command("movenewroom", clientID, c.getKey()));
			break;
		case "disconnect":
			tcp.terminate();
			break;
		default:
			room.handleCommand(c);
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
