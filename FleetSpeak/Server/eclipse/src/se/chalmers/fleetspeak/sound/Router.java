package se.chalmers.fleetspeak.sound;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.fleetspeak.util.Log;
import se.chalmers.fleetspeak.util.PortFactory;

/**
 * 
 * A class for routing data
 *
 */
public class Router implements Runnable{
	private boolean running;
	private DatagramSocket inport;
	private DatagramSocket outport;
	private Map<Integer, DatagramPacket> clients;
	private DatagramPacket buffer;
	
	/**
	 * Creates a Router instance from a selected inport
	 * @param inPort The port from which the data is taken from
	 */
	public Router(){
		try {
			inport = new DatagramSocket(PortFactory.getInstance().getPort());
			outport = new DatagramSocket();
		} catch (SocketException e) {
			Log.log("Failed to create datagram sockets");
			e.printStackTrace();
		}
		clients = new HashMap<Integer, DatagramPacket>();
		byte[] b = new byte[172];
		buffer = new DatagramPacket(b, b.length);
	}
	@Override
	public void run() {
			running = true;
		while(running){
			try {
				inport.receive(buffer);
			} catch (IOException e) {
				Log.log("Error while reading packet");
				e.printStackTrace();
			}
			send(buffer.getData());
		}
		
	}
	
	/**
	 * A method for transmitting data to the clients
	 * @param data The data to be transmitted
	 */
	private synchronized void send(byte[] data){
		
		for(DatagramPacket p: clients.values()){
			p.setData(data);
			try {
				outport.send(p);
			} catch (IOException e) {
				Log.log("Failed sending packet");
				e.printStackTrace();
			}
		}
	}
	/**
	 * Adds a client to the client list
	 * @param id The id of the client
	 * @param address The IP address of the client
	 * @param port The port from which the data should be transmitted to
	 */
	public synchronized void addClient(int id, InetAddress address, int port){
		byte[] buf = new byte[172];
		DatagramPacket p = new DatagramPacket(buf, buf.length, address, port);
		clients.put(id, p);
	}
	/**
	 * Removes a client from the client list
	 * @param id
	 */
	public synchronized void removeClient(int id){
		clients.remove(id);
	}
	/**
	 * Removes all the clients from the client list
	 */
	public synchronized void removeAllClients(){
		clients.clear();
	}
	
	/**
	 * Retrieves the port that the user should send sound data to
	 * @return The port that the user should send data to
	 */
	public int getReceivePort(){
		return this.inport.getPort();
	}
	
	/**
	 * Closes this router thread
	 */
	public void terminate(){
		running = false;
		removeAllClients();
		PortFactory.getInstance().freePort(inport.getLocalPort());
	}
}