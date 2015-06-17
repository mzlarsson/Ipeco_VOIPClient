package se.chalmers.fleetspeak.core;

import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;

import se.chalmers.fleetspeak.sound.Router;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.IDFactory;
import se.chalmers.fleetspeak.util.Log;
import se.chalmers.fleetspeak.util.Log2;

/**
 * A class that handles all connectors with the app
 * @author Nieo
 * @author Patrik Haar
 */


public class Client{

	private String name;
	private TCPHandler tcp;
	private int clientID;
	
	private InetAddress ip;
	private Router soundRouter;

	/**
	 * Creates a client with the functionality for sending and receiving
	 * commands and sound-streams.
	 * @param socket The socket for the tcp-connection to this client.
	 */
	public Client(Socket socket) {
		this.clientID = IDFactory.getInstance().getID();
		this.name = "UnknownUser";
		this.tcp = new TCPHandler(socket, clientID);
		this.tcp.sendData(new Command("setID", clientID, null));

		this.ip = socket.getInetAddress();
		this.soundRouter = new Router();
		this.tcp.sendData(new Command("useSoundPort", soundRouter.getReceivePort(), null));
		soundRouter.start();
	}
	
	/**
	 * Starts the the TCPHandler
	 */
	public void start(){
		this.tcp.start();
		
	}
	
	//FIXME temporary
	public void sendToPort(Client c, int port){
		tcp.sendData(new Command("sendStuffTo", c.ip.getHostAddress(), port));
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
		tcp.sendData(new Command("requestSoundPort", client.getClientID(), null));
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
		tcp.sendData(new Command("closeSoundPort", client.getClientID(), null));
	}
	
	/**
	 * Removes all clients that listens to this client.
	 */
	public void removeAllListeningClients() {
		soundRouter.removeAllClients();
	}
	
	/**
	 * Set the name of this client.
	 * @param name The new name of the client.
	 */
	protected void setName(String name) {
		if(name != null){
			this.name = name;
		}
	}

	/**
	 * Gets the name of this client.
	 * @return The name of the client.
	 */
	public String getName() {
		return name;
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
		IDFactory.getInstance().freeID(clientID);
	}

	/**
	 * Logs an error-message and terminates the client.
	 */
	public void connectionLost() {
		Log2.log(Level.INFO, "Client disconnected - closing streams");
		this.terminate();
	}

	/**
	 * Gets the clients ID.
	 * @return The ID of the client.
	 */
	public int getClientID() {
		return clientID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Client: name=" + name + ", clientID=" + clientID + ", ip=" + ip;
	}
}
