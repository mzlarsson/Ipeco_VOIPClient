package se.chalmers.fleetspeak.audio.sound;

import se.chalmers.fleetspeak.util.PortFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * A class for routing data
 *
 */
public class Router extends Thread{
	private boolean running;
	private int inportNbr;
	private DatagramSocket inport;
	private DatagramSocket outport;
	private Map<Integer, DatagramPacket> clients;
	private DatagramPacket buffer;
	private Logger logger;
	
	/**
	 * Creates a Router instance with a port it will listen on.
	 */
	public Router(){
		logger = Logger.getLogger("Debug");
		inportNbr = PortFactory.getInstance().getPort();
		try {
			inport = new DatagramSocket(inportNbr);
			outport = new DatagramSocket();
		} catch (SocketException e) {
			logger.log(Level.WARNING, "Failed to create datagram sockets: " +e.getMessage());
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
			} catch(SocketException e){
				this.terminate();
			}catch (IOException e) {
				logger.log(Level.WARNING,"Error while reading packet: " +e.getMessage());
			}
			send(buffer.getData());
		}
		
	}
	
	/**
	 * A method for transmitting data to the clients
	 * @param data The data to be transmitted
	 */
	private synchronized void send(byte[] data){
//		for(int i = 0; i<12; i++){
//			System.out.print(data[i]+" ");
//		}
//		System.out.println();
		for(DatagramPacket p: clients.values()){
			p.setData(data);
			try {
				outport.send(p);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed sending packet: " +e.getMessage());
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
		return inportNbr;
	}
	
	/**
	 * Closes this router thread
	 */
	public void terminate(){
		running = false;
		inport.close();
		outport.close();
		removeAllClients();
		PortFactory.getInstance().freePort(inportNbr);
	}
}
