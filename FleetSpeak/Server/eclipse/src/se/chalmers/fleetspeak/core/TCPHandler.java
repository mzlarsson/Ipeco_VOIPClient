package se.chalmers.fleetspeak.core;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.command.impl.CommandInfo;
import se.chalmers.fleetspeak.core.command.impl.CommandResponse;
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
	private boolean isRunning = false;
	private Map<String, CommandInfo> cmds;
	
	/**
	 * Constructs the TCPHandler for a specific client. 
	 * @param clientSocket The socket of the client.
	 * @param clientID The ID identifying the client.
	 */
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
		EventBus.getInstance().addSubscriber(this);
		initializeAndroidCommands();
		syncToClient();
	}
	
	/**
	 * Syncs the current model to the client so it is fully updated
	 */
	public void syncToClient(){
		RoomHandler handler = RoomHandler.getInstance();
		for(Room r : handler.getRooms()){
			sendData(new Command("createdRoom", r.getId(), r.getName()));
			for(Client c : handler.getClients(r)){
				if(c.getClientID() != clientID){
					sendData(new Command("addedUser", c.getClientID(), r.getId()));
					sendData(new Command("changedUsername", c.getClientID(), c.getName()));
				}
			}
		}
	}
	
	/**
	 * Returns the client of the TCPHandler.
	 * @return ID of the client.
	 */
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
				runAndroidCommand(c);
			}
		} catch(EOFException eofe){
			doCommand(cmds.get("disconnect"), clientID, null);
		} catch(SocketTimeoutException e){
			Log.logError("Got Socket Timeout. Removing client");
			doCommand(cmds.get("disconnect"), clientID, null);
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
				doCommand(cmds.get("disconnect"), clientID, null);
			}
		} catch(IOException e){
			Log.logException(e);
		}
	}
	/**
	 * Sets up the HashMap with the commands from the Android.
	 */
	private void initializeAndroidCommands() {
		cmds = new HashMap<String, CommandInfo>();
		cmds.put("setUsername", Commands.getInstance().findCommand("setUsername"));
		cmds.put("setSoundPort", Commands.getInstance().findCommand("setSoundPort"));
		cmds.put("moveUser", Commands.getInstance().findCommand("moveUser"));
		cmds.put("createRoom", Commands.getInstance().findCommand("createRoom"));
		cmds.put("disconnect", Commands.getInstance().findCommand("disconnect"));
	}
	/**
	 * Translates the command from the client.
	 * @param c The command which to be executed.
	 */
	private void runAndroidCommand(Command c){
		//Do translation Android --> Server according to spec
		switch(c.getCommand().toLowerCase()){
			case "setname":			doCommand(cmds.get("setUsername"), clientID, c.getKey());break;
			case "setsoundport":	doCommand(cmds.get("setSoundPort"), clientID, c.getKey()+","+c.getValue());
									//FIXME temporary
									RoomHandler handler = RoomHandler.getInstance();
									handler.findClient((int)c.getKey()).sendToPort(handler.findClient(clientID), (int)c.getValue());break;
			case "move":			doCommand(cmds.get("moveUser"), clientID, c.getKey());break;
			case "movenewroom":		Object[] data = doCommand(cmds.get("createRoom"), c.getKey(), null);
									int roomID = (data!=null&&data.length>0?(Integer)data[0]:-1);
									doCommand(cmds.get("moveUser"), clientID, roomID);break;
			case "disconnect":		doCommand(cmds.get("disconnect"), clientID, null);break;
		}
	}
	
	/**
	 * Executes the command from the client
	 * @param cmd The command to be executed.
	 * @param key First parameter for command.
	 * @param value Second parameter for command.
	 * @return
	 */
	public Object[] doCommand(CommandInfo cmd, Object key, Object value){
		Commands com = Commands.getInstance();
		CommandResponse r = com.execute(clientID, cmd, key, value);
		Log.logDebug("Got command response: ["+(r.wasSuccessful()?"Success":"Failure")+": "+r.getMessage()+"]");
		return r.getData();
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
			if (event.getActor()==null || event.getActor()==this) {
				sendData(event.getCommand());
			}
		}
	}
}
