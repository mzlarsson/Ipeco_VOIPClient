package se.chalmers.fleetspeak.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.ConnectionListener;
import se.chalmers.fleetspeak.Log;

public abstract class TCPHandler extends Thread{

	private Socket clientSocket;
	private List<ConnectionListener> listeners;

	public TCPHandler(Socket clientSocket){
		super("TCPHandler");
		this.clientSocket = clientSocket;
		this.listeners = new ArrayList<ConnectionListener>();
	}

	public boolean terminate(){
		try {
			if(clientSocket != null){
				clientSocket.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	protected InputStream getInputStream(){
		if(clientSocket == null){
			return null;
		}

		try {
			return clientSocket.getInputStream();
		} catch (IOException e) {
			Log.log("Could not fetch input stream: "+e.getClass().getCanonicalName());
			return null;
		}
	}

	protected OutputStream getOutputStream(){
		if(clientSocket == null){
			return null;
		}

		try {
			return clientSocket.getOutputStream();
		} catch (IOException e) {
			Log.log("Could not fetch output stream: "+e.getClass().getCanonicalName());
			return null;
		}
	}
	protected ObjectOutputStream getObjectOutputStream(){
		if(clientSocket == null){
			return null;
		}

		try {
			return new ObjectOutputStream(getOutputStream());
		} catch (IOException e) {
			Log.log("Could not fetch output stream: "+e.getClass().getCanonicalName());
			return null;
		}
	}
	public void addConnectionListener(ConnectionListener listener){
		this.listeners.add(listener);
	}
	
	public void removeConnectionListener(ConnectionListener listener){
		this.listeners.remove(listener);
	}
	
	protected void notifyConnectionLost(){
		for(int i = 0; i<listeners.size(); i++){
			listeners.get(i).connectionLost();
		}
	}
}