package se.chalmers.fleetspeak.core;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;
import se.chalmers.fleetspeak.util.Command;

/**
 * For handling of TCP connections with the andriod app
 *
 * @author Nieo, Patrik Haar
 */

public class TCPHandler extends Thread implements IEventBusSubscriber {

	private boolean synced = false;
	private Socket clientSocket;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	private boolean isRunning = false;
	private CommandHandler ch;
	private Logger logger;

	/**
	 * Constructs the TCPHandler for a specific client.
	 * @param clientSocket The socket of the client.
	 * @param clientID The ID identifying the client.
	 */
	public TCPHandler(Socket clientSocket) {
		super("TCPHandler:port"+clientSocket.getPort());
		logger = Logger.getLogger("Debug");
		this.clientSocket = clientSocket;
		try {
			logger.log(Level.FINE,"[TCPHandler]Trying to get streams");
			objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			logger.log(Level.FINE,"[TCPHandler]Got streams");
		} catch (IOException e) {
			logger.log(Level.WARNING,e.getMessage());
		}
		EventBus.getInstance().addSubscriber(this);
	}

	/**
	 * Syncs the current model to the client so it is fully updated
	 */
	public void syncToClient(){
		logger.log(Level.WARNING, "this does not work anymore");
		//TODO doesnt work anymore needs to be placed somewhere else
		/*RoomHandler handler = RoomHandler.getInstance();

		for(Room r : handler.getRooms()){
			sendCommand(new Command("createdRoom", r.getId(), r.getName()));
			for(Client c : handler.getClients(r)){
				sendCommand(new Command("addedUser", c.getInfoPacket().setRoomID(r.getId()), null));
			}
		}
		synced = true;*/
	}

	/**
	 * Looks for new incoming messages
	 */
	@Override
	public void run() {
		isRunning = true;
		try {
			while (isRunning && objectInputStream != null) {
				logger.log(Level.FINER,"[TCPHandler] trying to read");
				Object o = objectInputStream.readObject();

				if (o.getClass() == Command.class) {
					receivedCommand((Command) o);
				} else {
					logger.log(Level.SEVERE, "[TCPHandler] Found a non-Command object: " + o.getClass().toString());
				}
			}
		} catch(EOFException eofe){
			receivedCommand(new Command("disconnect", null, null));
		} catch(SocketTimeoutException e){
			logger.log(Level.SEVERE, "Got Socket Timeout. Removing client");
			receivedCommand(new Command("disconnect", null, null));
		} catch(SocketException e){
			//Only log if the handler is not terminated
			if(isRunning){
				logger.log(Level.SEVERE, e.getMessage());
			}
		}catch (IOException e) {
			logger.log(Level.SEVERE,e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.log(Level.FINE,"[TCPHandler]" + e.getMessage());
		}
	}

	private void receivedCommand(Command c) {
		if (ch != null) {
			ch.handleCommand(c);
		} else {
			logger.log(Level.SEVERE, "[TCPHandler] Received a Command without a set CommandHandler");
		}
	}

	/**
	 * Tries to send a command to the socket.
	 * @param command The Command to be sent.
	 */
	public void sendCommand(Command command){
		try{
			logger.log(Level.FINER,"[TCPHandler]Sending Command: [ " + command.getCommand()+" | "+command.getKey()+" | "+command.getValue()+" ]");
			objectOutputStream.writeObject(command);
		} catch(SocketException e){
			if(command==null || !command.getCommand().equals("userDisconnected")){
				receivedCommand(new Command("disconnect", null, null));
			}
		} catch(IOException e){
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Returns the IP of the connection.
	 * @return The IP of the connection.
	 */
	public InetAddress getInetAddress() {
		return clientSocket.getInetAddress();
	}

	/**
	 * Sets the CommandHandler which will handle the incoming Commands.
	 * @param ch The CommandHandler to be used.
	 */
	public void setCommandHandler(CommandHandler ch) {
		this.ch = ch;
	}
	/**
	 * Stops the TCPHandler. Unsubscribes the TCPHandler from the Eventbus and closes the clientSocket.
	 * @return If the clientSocket was successfully closed returns true, else false.
	 */
	public boolean terminate() {
		isRunning = false;
		EventBus.getInstance().removeSubscriber(this);
		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}


	@Override
	public void eventPerformed(EventBusEvent event) {
		// Will forward the command to its client if this event starts with broadcast and the actor is this class or null.
		if (event.getReciever().startsWith("broadcast")) {
			if(synced){
				sendCommand(event.getCommand());
			}
		}
	}
}
