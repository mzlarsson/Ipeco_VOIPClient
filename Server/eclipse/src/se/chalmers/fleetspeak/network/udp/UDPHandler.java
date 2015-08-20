package se.chalmers.fleetspeak.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles incoming and outgoing UDP traffic to a client.
 *
 * @author Patrik Haar
 */
public class UDPHandler extends Thread{

	private DatagramSocket socket;
	private boolean isRunning = false;
	private Logger logger;
	private DatagramPacket receivePacket, outgoingPacket;
	private int packetSize;
	private PacketReceiver receiver;
	
	/**
	 * Constructor for the UDPHandler.
	 * @param socket The DatagramSocket already connected to the remote client.
	 * @param packetSize The size of the incoming packets.
	 */
	public UDPHandler(DatagramSocket socket, int packetSize) {
		super("UDPHandler:port"+socket.getLocalPort());
		logger = Logger.getLogger("Debug");
		this.socket = socket;
		this.packetSize = packetSize;
		receivePacket = new DatagramPacket(new byte[this.packetSize], this.packetSize);
		if (socket.isConnected()) {
			outgoingPacket = new DatagramPacket(new byte[172], 172, socket.getRemoteSocketAddress());
		} else {
			throw new IllegalArgumentException("The socket is not connected to a remote adress.");
		}
	}

	@Override
	public void run() {
		isRunning = true;
		logger.log(Level.INFO, "Listening to UDP traffic on port: " + socket.getLocalPort());
		while(isRunning) {
			try {
				socket.receive(receivePacket);
			} catch (IOException e) {
				logger.log(Level.WARNING, "UDP-socket " + socket.getLocalPort() + " caught an exception");
				e.printStackTrace();
			}
			handlePacket(receivePacket.getData());			
		}
	}
	
	/**
	 * Sends the given byte-array-packet to the address connected to the socket.
	 * @param packet byte-array of the data to be sent.
	 */
	public void sendPacket(byte[] packet) {
		outgoingPacket.setData(packet);
		try {
			socket.send(outgoingPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handlePacket(byte[] packet) {
		if (receiver != null) {
			receiver.handlePacket(packet);
		}
	}
	
	/**
	 * Sets where the UDPHandler sends incoming packets.
	 * @param receiver The receiver to which incoming traffic will be sent.
	 */
	public void setReceiver(PacketReceiver receiver) {
		this.receiver = receiver;
	}
	
	/**
	 * Free active resources and terminate the connection.
	 */
	public void terminate() {
		isRunning = false;
		socket.close();
	}
}
