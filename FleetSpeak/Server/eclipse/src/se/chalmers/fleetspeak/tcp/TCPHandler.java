package se.chalmers.fleetspeak.tcp;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log;

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
			objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			Log.log("Command connection established");
		} catch (IOException e) {
			Log.logException(e);
		}
		eventBus = EventBus.getInstance();
		eventBus.addSubscriber(this);

	}

	public void run() {
		isRunning = true;
		try {
			while (isRunning) {
				Object o = objectInputStream.readObject();
				Command c = (Command) o ;
				Log.log("[TCPHandler] <i>Got command " + c.getCommand()+"</i>");
				eventBus.fireEvent(new EventBusEvent("CommandHandler", c, this));
				Thread.sleep(500);
			}
		} catch(EOFException eofe){
			eventBus.fireEvent(new EventBusEvent("CommandHandler", new Command("disconnect", clientID, null), this));
		} catch(SocketException e){
			//Only log if the handler is not terminated
			if(isRunning){
				Log.logException(e);
			}
		}catch (IOException e) {
			Log.logException(e);
		} catch (ClassNotFoundException e) {
			Log.logException(e);
		} catch (InterruptedException e) {
			eventBus.fireEvent(new EventBusEvent("CommandHandler", new Command("disconnect", clientID, null), this));
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
	
	public void sendData(Command command){
		try{
			objectOutputStream.writeObject(command);
			Log.log("[TCPHandler] <i>Command sent: "+command.getCommand()+"</i>");
		}catch(IOException e){
			e.printStackTrace();
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
