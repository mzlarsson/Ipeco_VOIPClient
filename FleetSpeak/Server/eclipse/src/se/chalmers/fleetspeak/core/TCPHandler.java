package se.chalmers.fleetspeak.core;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import se.chalmers.fleetspeak.core.command.Commands;
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
		
		sendData(new Command("setID", clientID, null));
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
				runAndroidCommand(c);
			}
		} catch(EOFException eofe){
			doCommand("disconnect", clientID, null);
		} catch(SocketTimeoutException e){
			Log.logError("Got Socket Timeout. Removing client");
			doCommand("disconnect", clientID, null);
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
				doCommand("disconnect", clientID, null);
			}
		} catch(IOException e){
			Log.logException(e);
		}
	}
	
	private void runAndroidCommand(Command c){
		//Do translation Android --> Server according to spec
		switch(c.getCommand()){
			case "setName":			doCommand("setUsername", clientID, c.getKey());break;
			case "setSoundPort":	doCommand("setSoundPort", clientID, c.getKey()+","+c.getValue());break;
			case "move":			doCommand("moveUser", clientID, c.getKey());break;
			case "moveNewRoom":		Object[] data = doCommand("createRoom", c.getKey(), null);
									int roomID = (data!=null&&data.length>0?(Integer)data[0]:-1);
									doCommand("moveUser", clientID, roomID);break;
			case "disconnect":		doCommand("disconnect", clientID, null);break;
		}
	}
	
	public Object[] doCommand(String cmd, Object key, Object value){
		Commands com = Commands.getInstance();
		CommandResponse r = com.execute(clientID, com.findCommand(cmd), key, value);
		Log.logDebug("Got command response: ["+(r.wasSuccessful()?"Success":"Failure")+": "+r.getMessage()+"]");
		return r.getData();
	}

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
