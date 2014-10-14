package se.chalmers.fleetspeak.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import se.chalmers.fleetspeak.Command;
import se.chalmers.fleetspeak.Log;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;

public class TCPHandler extends Thread implements IEventBusSubscriber {

	private Socket clientSocket;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	EventBus eventBus;
	private boolean isRunning = false;

	public TCPHandler(Socket clientSocket) {

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
				Command c = (Command) objectInputStream.readObject();
				Log.log("[TCPHandler] Got command " + c.getCommand());
				eventBus.fireEvent(new EventBusEvent("CommandHandler", c, this));
				Thread.sleep(500);
			}
		} catch (IOException e) {
			Log.log(e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.log(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.log(e.getMessage());
		}

	}

	@Override
	public void eventPerformed(EventBusEvent event) {
		if (event.getReciever().startsWith("broadcast")) {
			try {
				objectOutputStream.writeObject(event.getCommand());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	public void sendData(Command command){
		try{
			Log.log("[TCPHandler]Trying to send a command");
			objectOutputStream.writeObject(command);
			Log.log("[TCPHandler]Sent the command");
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
