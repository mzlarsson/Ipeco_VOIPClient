package se.chalmers.fleetspeak.core;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log;

/**
 * For handling of TCP connections with the andriod app
 * @author Nieo
 *
 */

public class TCPHandler extends Thread implements IEventBusSubscriber {

	private int clientID;
	private Socket clientSocket;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	EventBus eventBus;
	private boolean isRunning = false;

	public TCPHandler(Socket clientSocket, int clientID) {
		this.clientID = clientID;
		this.clientSocket = clientSocket;
		try {
			Log.log("[TCPHandler]Trying to get streams");
			objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			Log.log("[TCPHandler]Got streams");
		} catch (IOException e) {
			Log.logException(e);
		}
		eventBus = EventBus.getInstance();
		eventBus.addSubscriber(this);

	}
	
	public int getClientID(){
		return this.clientID;
	}
	
	/**
	 * Looks for new incoming messages 
	 */

	public void run() {
		isRunning = true;
		try {
			while (isRunning && objectInputStream != null) {
				Log.log("[TCPHandler] trying to read");
				Object o = objectInputStream.readObject();
				Log.log("[TCPHandler] Found: " + o.getClass().toString());
				Command c = (Command) o ;//objectInputStream.readObject();
				Log.log("[TCPHandler] Got command " + c.getCommand());
				eventBus.fireEvent(new EventBusEvent("CommandHandler", c, this));
			}
		} catch(EOFException eofe){
			eventBus.fireEvent(new EventBusEvent("CommandHandler", new Command("disconnect", clientID, null), this));
		} catch(SocketTimeoutException e){
			Log.logError("Got Socket Timeout. Removing client");
			eventBus.fireEvent(new EventBusEvent("CommandHandler", new Command("disconnect", clientID, null), this));
		} catch(SocketException e){
			//Only log if the handler is not terminated
			if(isRunning){
				Log.logException(e);
			}
		}catch (IOException e) {
			Log.logException(e);
		} catch (ClassNotFoundException e) {
			Log.log("[TCPHandler]" + e.getMessage());
		}
	}

	@Override
	public void eventPerformed(EventBusEvent event) {
		// Will forward the command to its client if this event starts with broadcast and the actor is this class or null.
		if (event.getReciever().startsWith("broadcast")) {
			if (event.getActor()==null || event.getActor()==this) {
				sendData(event.getCommand());
			}
		}
	}
	/**
	 * Tries to send a command to the socket 
	 * @param command
	 */
	public void sendData(Command command){
		try{
			Log.log("[TCPHandler]Trying to send " + command.getCommand());
			objectOutputStream.writeObject(command);
			Log.log("[TCPHandler] <i>Command sent: "+command.getCommand()+"</i>");
		} catch(SocketException e){
			if(command==null || !command.getCommand().equals("userDisconnected")){
				eventBus.fireEvent(new EventBusEvent("CommandHandler", new Command("disconnect", clientID, null), this));
			}
		} catch(IOException e){
			Log.logException(e);
		}
	}

	public boolean terminate() {
		isRunning = false;
		eventBus.removeSubscriber(this);
		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}