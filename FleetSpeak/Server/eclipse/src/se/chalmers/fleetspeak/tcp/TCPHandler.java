package se.chalmers.fleetspeak.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
			Log.log("Trying to get streams");
			objectOutputStream = new ObjectOutputStream(
					clientSocket.getOutputStream());
			objectInputStream = new ObjectInputStream(
					clientSocket.getInputStream());
			Log.log("Got streams");
		} catch (IOException e) {
			e.printStackTrace();
		}
		eventBus = EventBus.getInstance();
		eventBus.addSubscriber(this);

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

	public void run() {
		isRunning = true;
		try {
			while (isRunning) {
				Object o = objectInputStream.readObject();
				Log.log(o.getClass().toString());
				Command c = (Command) o ;//objectInputStream.readObject();
				Log.log("[TCPHandler] Got command " + c.getCommand());
				eventBus.fireEvent(new EventBusEvent("CommandHandler", c, this));
				Thread.sleep(500);
			}
		} catch (IOException e) {
			Log.log(e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.log(e.getMessage());
		} catch (InterruptedException e) {
			eventBus.fireEvent(new EventBusEvent("CommandHandler",
					new Command("disconnect", clientID, null), this));
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
			Log.log("[TCPHandler]Trying to send " + command.getCommand());
			objectOutputStream.writeObject(command);
			Log.log("[TCPHandler]Command sent");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
