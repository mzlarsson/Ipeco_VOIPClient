package se.chalmers.fleetspeak.core;

import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import se.chalmers.fleetspeak.sound.Router;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.IDFactory;
import se.chalmers.fleetspeak.util.Log;

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
		this.tcp.start();
		this.tcp.sendData(new Command("setID", clientID, null));

		this.ip = socket.getInetAddress();
		this.soundRouter = new Router();
	}
	
	/**
	 * Moves this client to another room symbolized by a list of clients
	 * and adds them as listeners to its stream.
	 * @param clientList A list of clients symbolizing all clients in a room.
	 */
	public synchronized void moveToRoom(List<Client> clientList){
		// FIXME Make android-client drop all incoming ports.
		removeAllListeningClients();
		for (Client c : clientList) {
			addListeningClient(c);
		}
	}

	/**
	 * Adds a client to listen to this client.
	 * @param client The client to listen to.
	 */
	public void addListeningClient(Client client){
		soundRouter.addClient(client.clientID, client.ip, client.requestSoundPort());
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
	 * Requests a port from the client to use for incoming sound-packages.
	 * @return The port-number of a port ready to accept sound-packages.
	 */
	private int requestSoundPort() {
		return -1; // FIXME Request a usable port for incoming sound from the client.
	}
	
	/**
	 * Set the name of this client.
	 * @param name The new name of the client.
	 */
	public void setName(String name) {
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
		Log.log("Client disconnected - closing streams");
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
